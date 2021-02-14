No one definition - definition depends on who you ask.
To a client - single service that offers elastic properties - scales with increase in load, storage capacity needed. The infrastructure that makes their services work is abstracted.

For distributed systems in general, a technologist may talk about distributed systems from its components (just GFS, Chubby, Dynamo).

thinking about distributed systems within these terms

How can we understand the project of distributed systems, as a whole, and the design principles of systems, rather than jumping straight into the details.

"What are the overarching design principles of cloud systems that guide developers towards a cloud-computing mindset quite distinct... from traditional client-server systems or traditional multi-case protocols"

- Frangipani v. distributed storage now

Application detail that may seem trivial actually drives low-level details.

- Iterations of AWS - tale of Aurora

What role does research play in the development of cloud systems?
It seems, topics of academic/research interest do not have the same interest for industry.
Chubby - yeah locking is great, but the research questions should be how to write systems that don't need locking the in the first place
For scaling database - knocking ACID down to
