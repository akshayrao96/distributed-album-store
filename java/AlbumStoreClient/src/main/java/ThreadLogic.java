import io.swagger.client.ApiClient;
import io.swagger.client.Configuration;
import io.swagger.client.api.DefaultApi;
import java.util.concurrent.CountDownLatch;

public class ThreadLogic implements Runnable {

  private final String path;
  private final int numRequests;
  private final CountDownLatch completed;

  public ThreadLogic(String path, int numRequests, CountDownLatch completed) {
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
      RequestHandler.post(albumsApi);
      RequestHandler.get(albumsApi);
    }
    this.completed.countDown();
  }
}
