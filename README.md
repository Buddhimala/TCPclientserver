TCP Client Server

This program implements a simple client server program in java to get two inputs from the client and outputs their sum. The program consisits of two scripts as
1)Client.java
2)Server.java

to handle client and server side operations respectively.

In order to handle multiple client requests concurrently, a threadpool is used.
The maximum number of client requests that can be handled by the server at a time is set to 50.
If more that 50 client requests come to the server, all the additional requests will be sent to a queue and when a thread gets released, its resources will be allocated to the process in the queue.
