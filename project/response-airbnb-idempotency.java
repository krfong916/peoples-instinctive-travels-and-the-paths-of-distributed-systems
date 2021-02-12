public <R extends Object, S extends Object, A extends IdempotencyRequest> Response process(
   String idempotencyKey,
   UriInfo uriInfo,
   SetupExecutable<A> preRpcExecutable, // Pre-RPC lambda
   ProcessExecutable<R, A> rpcExecutable, // RPC lambda
   PostProcessExecutable<R, S> postRpcExecutable) // Post-RPC lambda
   throws YourCustomException {
 try {
   // Find previous request (for retries), otherwise create
   IdempotencyRequest idempotencyRequest = createOrFindRequest(idempotencyKey, apiUri);
   Optional<Response> responseOptional = findIdempotencyResponse(idempotencyRequest);

   // Return the response for any deterministic end-states, such as
   // non-retryable errors and previously successful responses
   if (responseOptional.isPresent()) {
     return responseOptional.get();
   }

   boolean isRetry = idempotencyRequest.isRetry();
   A requestObject = null;

   // STEP 1: Pre-RPC phase:
   // Typically used to create transaction and related sub-entities
   // Skipped if request is a retry
   if(!isRetry) {
     // Before a request is made to the external service, we record
     // the request and idempotency commit in a single DB transaction
     requestObject =
         dbTransactionManager.execute(
             tc -> {
               final A preRpcResource = preRpcExecutable.execute();
               updateIdempotencyResource(idempotencyKey, preRpcResource);

               return preRpcResource;
             });
   } else {
     requestObject = findRequestObject(idempotencyRequest);
   }

   // STEP 2: RPC phase:
   // One or more network calls to the service. May include
   // additional idempotency logic in the case of a retry
   // Note: NO database transactions should exist in this executable
   R rpcResponse = rpcExecutable.execute(isRetry, requestObject);

   // STEP 3: Post-RPC phase:
   // Response is recorded and idempotency information is updated,
   // such as releasing the lease on the idempotency key. Again,
   // all in one single DB transaction
   S response = dbTransactionManager.execute(
       tc -> {
         final S postRpcResponse = postRpcExecutable.execute(isRetry, rpcResponse);
         updateIdempotencyResource(idempotencyKey, postRpcResponse);

         return postRpcResponse;
       });

   return serializeResponse(response);
 } catch (Throwable exception) {
   // If CustomException, return error code and response based on
   // ‘retryable’ or ‘non-retryable’. Otherwise, classify as ‘retryable’
   // and return a 500.
 }
}
