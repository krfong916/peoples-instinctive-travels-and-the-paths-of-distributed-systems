public Response processPayment(InitiatePaymentRequest request, UriInfo uriInfo)
   throws YourCustomException {

 return orpheusManager.process(
     request.getIdempotencyKey(),
     uriInfo,
     // 1. Pre-RPC
     () -> {
       // Record payment request information from the request object
       PaymentRequestResource paymentRequestResource = recordPaymentRequest(request);
       return Optional.of(paymentRequestResource);
     },
     // 2. RPC
     (isRetry, paymentRequest) -> {
       return executePayment(paymentRequest, isRetry);
     },
     // 3. Post RPC - record response information to database
     (isRetry, paymentResponse) -> {
       return recordPaymentResponse(paymentResponse);
     });
}