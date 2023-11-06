package part2;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;


public class ClientApp2 {

  private static final int INIT_NUM_REQUESTS = 100;
  private static final int NUM_REQUESTS = 1000;
  private static final int DELAY = 2;

  public static void main(String[] args) throws InterruptedException {

    validateArgs(args);

    int numThreads = Integer.parseInt(args[0]);
    int threadGroups = Integer.parseInt(args[1]);
    String path = args[2];

    CountDownLatch countDownLatchInitial = new CountDownLatch(numThreads);
    ExecutorService executorService = Executors.newFixedThreadPool(threadGroups);

    runInitial(executorService, path, numThreads, countDownLatchInitial);

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
        countdownLatchLoading, data, success, failed);
    long end = System.currentTimeMillis();

    double wallTime = (double) (end - start) / 1000;
    int totalRequests = numThreads * threadGroups * NUM_REQUESTS * 2;

    System.out.println();
    System.out.println("Total API Requests : " + totalRequests);
    System.out.println("Walltime : " + wallTime + " seconds");
    int throughput = (int) (totalRequests / wallTime);
    System.out.println("Throughput : " + throughput);
    System.out.println("Successful requests: " + success);
    System.out.println("Failed requests: " + failed);

    writeResponseDataToCsv(data);
    System.out.println("\n---STATISTICS FOR REQUESTS---\n");
    showStatistics();
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
      CountDownLatch countDownLatch) throws InterruptedException {
    for (int i = 0; i < numThreads; i++) {
      executorService.execute(
          new ThreadLogic2(path, INIT_NUM_REQUESTS, countDownLatch, null, null, null));
    }
    countDownLatch.await();
  }

  private static void runTrackedThreads(ExecutorService executorService, String path,
      int numThreads, int threadGroups, CountDownLatch countDownLatch,
      ConcurrentLinkedDeque<ResponseData> data, AtomicInteger success, AtomicInteger failed)
      throws InterruptedException {

    for (int i = 0; i < threadGroups; i++) {
      executorService.execute(() -> {
        ExecutorService executorService2 = Executors.newFixedThreadPool(numThreads);
        for (int j = 0; j < numThreads; j++) {
          executorService2.execute(
              new ThreadLogic2(path, NUM_REQUESTS, countDownLatch, data, success, failed));
        }
        executorService2.shutdown();
      });
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

  public static void showStatistics() {
    LatencyStatistics statisticsCalculator = new LatencyStatistics();
    try {
      statisticsCalculator.addResponseTimesFromCSV("response_data.csv");
    } catch (IOException e) {
      e.printStackTrace();
    }

    long meanResponseTime = statisticsCalculator.calculateMean();
    long medianResponseTime = statisticsCalculator.calculateMedian();
    long p99ResponseTime = statisticsCalculator.calculatePercentile(99);
    long minResponseTime = statisticsCalculator.calculateMin();
    long maxResponseTime = statisticsCalculator.calculateMax();

    System.out.println("Mean Response Time: " + meanResponseTime);
    System.out.println("Median Response Time: " + medianResponseTime);
    System.out.println("99th Percentile Response Time (p99): " + p99ResponseTime);
    System.out.println("Min Response Time: " + minResponseTime);
    System.out.println("Max Response Time: " + maxResponseTime);

  }
}
