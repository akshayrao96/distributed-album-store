package part2;

import io.swagger.client.ApiClient;
import io.swagger.client.Configuration;
import io.swagger.client.api.DefaultApi;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadLogic2 implements Runnable {

  private final String path;
  private final int numRequests;
  private final CountDownLatch completed;

  private final ConcurrentLinkedDeque<ResponseData> data;

  private final AtomicInteger success;
  private final AtomicInteger failed;

  public ThreadLogic2(String path, int numRequests, CountDownLatch completed,
      ConcurrentLinkedDeque<ResponseData> data, AtomicInteger success, AtomicInteger failed) {
    this.path = path;
    this.numRequests = numRequests;
    this.completed = completed;
    this.data = data;
    this.success = success;
    this.failed = failed;
  }

  @Override
  public void run() {
    ApiClient apiClient = Configuration.getDefaultApiClient();
    apiClient.setBasePath(this.path);
    DefaultApi albumsApi = new DefaultApi(apiClient);

    for (int j = 0; j < numRequests; j++) {
      ResponseData responseGet = RequestHandler2.post(albumsApi);
      if (this.data != null && responseGet != null) {
        data.add(responseGet);
        if (success != null) success.incrementAndGet();
      } else {
        if (failed != null) failed.incrementAndGet();
      }
      ResponseData responsePost = RequestHandler2.get(albumsApi);
      if (this.data != null && responseGet != null) {
        //data.add(responsePost);
        if (success != null) success.incrementAndGet();
      } else {
        if (failed != null) failed.incrementAndGet();
      }
    }
    System.out.println(Thread.currentThread().getName() + " has finished");
    this.completed.countDown();
  }
}
