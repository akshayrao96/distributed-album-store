package part1;

import io.swagger.client.ApiClient;
import io.swagger.client.Configuration;
import io.swagger.client.api.DefaultApi;
import java.util.concurrent.CountDownLatch;

public class ThreadLogic1 implements Runnable {

  private final String path;
  private final int numRequests;
  private final CountDownLatch completed;


  public ThreadLogic1(String path, int numRequests, CountDownLatch completed) {
    this.path = path;
    this.numRequests = numRequests;
    this.completed = completed;
  }

  @Override
  public void run() {
    ApiClient apiClient = Configuration.getDefaultApiClient();
    apiClient.setBasePath(this.path);
    DefaultApi albumsApi = new DefaultApi(apiClient);

    for (int j = 0; j < numRequests; j++) {
      RequestHandler1.post(albumsApi);
      RequestHandler1.get(albumsApi);
    }
    this.completed.countDown();
  }
}
