Github URL: 
https://github.com/akshayrao96/cs6650-bsds

Main : src/main/java/ClientApp1.java
Client Part 1 Results: Found in client/part1
Client Part 2 Results: Found in client/part2
Graph : Found in client/part3

Design:
Repo consists of a client server program. Overview of its design and components:

Client - Holds class AlbumStoreClient, which is our client
Server - Holds Java EE servlets and Go-gin server
Client results - Plots and answers for Assignment spec questions.

The main class is found in the src/java folder and is called ClientApp1, which serves as the entry point for running the client. The other classes are the following: 

ThreadLogic: This class implements the Runnable interface and defines the logic for what each thread should execute. Each instance of ThreadLogic instantiates an Api Client instance, and  undergoes a loop of 1000 GET and POST requests. The execution of these requests is managed by a static RequestHandler.

RequestHandler: The RequestHandler class contains methods for handling GET and POST requests. It relies on the swagger SDK API methods to perform these requests. If a thread encounters an issue and cannot execute a request, it retries for a maximum of 5 times. Each GET and POST method returns response data to a list.

ResponseData: This holds data about the GET and POST requests executed in the RequestHandler. It collects information such as response times and status codes.

Latency Statistics: The Latency Statistics class provides statistics based on the data collected by ResponseData. It provides insights into the client's throughput values, including metrics like mean response time, median response time, and the 99th percentile response time.


ClientApp1 Flow: Here's how ClientApp1 operates:

1) It takes three command-line arguments: the number of threads, the number of groups, and the EC2 instance URL.

2) Instantiates CountDownLatch and ExecutorService. ExecutorService consists of a fixed threadPool of max number of threads that will be created (as methods are I/O intensive).

3) Execution of initial threads using ThreadLogic, which calls RequestHandler

4) Execution of load-server threads. Follows the same method as initial threads, but with groups of threads executing in 2 second delay increments. This method also collects response data in a synchronized queue.

5) Uses the synchronized queue with data to generate a csv file, called response_data.csv

6) Calls LatencyStatistics to calculate statistics such as mean response time, median response time, and the 99th percentile response time based on the data collected in ResponseData.
