package part2;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;


public class ClientApp2 {

  private static final int INIT_NUM_REQUESTS = 50;
  private static final int NUM_REQUESTS = 50;
  private static final int DELAY = 2;
  private static final int GET_REQ = 3;
  private static final Random rand = new Random();

  public static void main(String[] args) throws InterruptedException {

    validateArgs(args);

    int numThreads = Integer.parseInt(args[0]);
    int threadGroups = Integer.parseInt(args[1]);
    String path = args[2];
    AtomicInteger validAlbumKey = new AtomicInteger(0);

    CountDownLatch countDownLatchInitial = new CountDownLatch(numThreads);
    ExecutorService executorService = Executors.newFixedThreadPool(threadGroups);

    runInitial(executorService, path, numThreads, countDownLatchInitial, validAlbumKey);

    System.out.println("---FINISHED INITIAL THREADS---\n");

    System.out.println(
        "threadGroupSize = " + numThreads + ", numThreadGroups = " + threadGroups + ", delay = "
            + DELAY);

    CountDownLatch countdownLatchLoading = new CountDownLatch(numThreads * threadGroups);
    ConcurrentLinkedDeque<ResponseData> data = new ConcurrentLinkedDeque<>();
    AtomicInteger success = new AtomicInteger(0);
    AtomicInteger failed = new AtomicInteger(0);

    long start = System.currentTimeMillis();
    runTrackedThreads(executorService, path, numThreads, threadGroups,
        countdownLatchLoading, data, success, failed, validAlbumKey);
    long end = System.currentTimeMillis();

    double wallTime = (double) (end - start) / 1000;
    int totalRequests = numThreads * threadGroups * NUM_REQUESTS * 4 + 90;

    System.out.println();
    System.out.println("Total API Requests : " + totalRequests);
    System.out.println("Walltime : " + wallTime + " seconds");
    int throughput = (int) (totalRequests / wallTime);
    System.out.println("Throughput : " + throughput);
    System.out.println("Successful requests: " + success);
    System.out.println("Failed requests: " + failed);

    //writeResponseDataToCsv(data);
    System.out.println("\n---STATISTICS FOR REQUESTS---\n");
    showStatistics(data);
  }

  private static void validateArgs(String[] args) {
    if (args.length != 3) {
      System.exit(1);
    }

    int threads;
    int threadGroup;

    try {
      threads = Integer.parseInt(args[0]);
      threadGroup = Integer.parseInt(args[1]);
      if (threads <= 0 || threadGroup <= 0) {
        throw new NumberFormatException();
      }
    } catch (NumberFormatException e) {
      System.out.println("Invalid thread size and group given");
      System.exit(1);
    }
  }

  private static void runInitial(ExecutorService executorService, String path, int numThreads,
      CountDownLatch countDownLatch, AtomicInteger validAlbumKey) throws InterruptedException {
    for (int i = 0; i < numThreads; i++) {
      executorService.execute(
          new ThreadLogic2(path, INIT_NUM_REQUESTS, countDownLatch, null, null, null, validAlbumKey));
    }
    countDownLatch.await();
  }

  private static void runTrackedThreads(ExecutorService executorService, String path,
      int numThreads, int threadGroups, CountDownLatch countDownLatch,
      ConcurrentLinkedDeque<ResponseData> data, AtomicInteger success, AtomicInteger failed, AtomicInteger validAlbumKey)
      throws InterruptedException {
    int numAlbums = 0;

    for (int i = 0; i < threadGroups; i++) {
      executorService.execute(() -> {
        ExecutorService executorService2 = Executors.newFixedThreadPool(numThreads);
        for (int j = 0; j < numThreads; j++) {
          executorService2.execute(
              new ThreadLogic2(path, NUM_REQUESTS, countDownLatch, data, success, failed, validAlbumKey));
        }
        executorService2.shutdown();
      });
      //Todo: fix this not to use arbitrary number
      //when there is a successful post, we set the validAlbumKey to the max(validAlbumKey, returnedAlbumID)
      numAlbums = validAlbumKey.get(); // 1000 albums posted
      
      CountDownLatch getLatch = new CountDownLatch(3);
      ExecutorService executorService3 = Executors.newFixedThreadPool(GET_REQ);

      for (int j = 0; j < GET_REQ; j++) {
        String randomID = String.valueOf(rand.nextInt(numAlbums + 1));
        executorService3.execute(new ThreadLogic2(path, NUM_REQUESTS, getLatch, data, success, failed).getLikes(randomID));
      }
      getLatch.await();
      executorService3.shutdown();
      Thread.sleep(DELAY * 1000);
    }
    countDownLatch.await();
    executorService.shutdown();
  }

  public static void writeResponseDataToCsv(ConcurrentLinkedDeque<ResponseData> responseDataQueue) {
    try (FileWriter csvWriter = new FileWriter("response_data.csv")) {
      csvWriter.append("Start, Request, Latency (ms), Code\n");
      while (!responseDataQueue.isEmpty()) {
        ResponseData responseData = responseDataQueue.poll();
        csvWriter.append(responseData.toCsvRow()).append("\n");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void showStatistics(ConcurrentLinkedDeque<ResponseData> data) {
    showStatisticsForType(data, "POST-ALBUM");
    System.out.println();
    showStatisticsForType(data, "POST-LIKE");
    System.out.println();
    showStatisticsForType(data, "POST-DISLIKE");
    System.out.println();
    showStatisticsForType(data, "GET-LIKES");
    System.out.println();

  }

  public static void showStatisticsForType(ConcurrentLinkedDeque<ResponseData> data, String type) {
    LatencyStatistics statisticsCalculator = new LatencyStatistics();
    data.stream()
        .filter(responseData -> responseData.getRequestType().equals(type))
        .forEach(
            responseData -> statisticsCalculator.addResponseTime(type, responseData.getLatency()));

    System.out.println("Statistics for " + type + ":");

    long meanResponseTime = statisticsCalculator.calculateMean(type);
    long medianResponseTime = statisticsCalculator.calculateMedian(type);
    long p99ResponseTime = statisticsCalculator.calculatePercentile(type, 99);
    long minResponseTime = statisticsCalculator.calculateMin(type);
    long maxResponseTime = statisticsCalculator.calculateMax(type);

    System.out.println("Mean Response Time: " + meanResponseTime);
    System.out.println("Median Response Time: " + medianResponseTime);
    System.out.println("99th Percentile Response Time (p99): " + p99ResponseTime);
    System.out.println("Min Response Time: " + minResponseTime);
    System.out.println("Max Response Time: " + maxResponseTime);

  }
}
