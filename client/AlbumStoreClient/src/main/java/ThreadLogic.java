import io.swagger.client.ApiClient;
import io.swagger.client.Configuration;
import io.swagger.client.api.DefaultApi;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CountDownLatch;

public class ThreadLogic implements Runnable {

  private final String path;
  private final int numRequests;
  private final CountDownLatch completed;

  private final ConcurrentLinkedDeque<ResponseData> data;

  public ThreadLogic(String path, int numRequests, CountDownLatch completed,
      ConcurrentLinkedDeque<ResponseData> data) {
    this.path = path;
    this.numRequests = numRequests;
    this.completed = completed;
    this.data = data;
  }

  @Override
  public void run() {
    ApiClient apiClient = Configuration.getDefaultApiClient();
    apiClient.setBasePath(this.path);
    DefaultApi albumsApi = new DefaultApi(apiClient);

    for (int j = 0; j < numRequests; j++) {
      ResponseData responseGet = RequestHandler.post(albumsApi);
      if (this.data != null && responseGet != null) {
        data.add(responseGet);
      }
      ResponseData responsePost = RequestHandler.get(albumsApi);
      if (this.data != null && responseGet != null) {
        data.add(responsePost);
      }
    }
    this.completed.countDown();
  }
}
