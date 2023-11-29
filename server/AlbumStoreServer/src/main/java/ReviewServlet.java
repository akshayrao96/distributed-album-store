import com.google.gson.JsonObject;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import rabbitMQ.RabbitMQProducer;

@WebServlet(name = "ReviewServlet", value = "/review/*")
public class ReviewServlet extends HttpServlet {

  private RabbitMQProducer producer;
  private final String QUEUE_NAME = "reviewsQueue";

  @Override
  public void init() throws ServletException {
    super.init();
    producer = new RabbitMQProducer(QUEUE_NAME);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    String urlPath = request.getPathInfo();

    if (urlPath == null || urlPath.isEmpty() || urlPath.split("/").length != 3) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.getWriter().write("{\"error\": \"Invalid URL format\"}");
      return;
    }

    String[] urlParts = urlPath.split("/");
    String reviewType = urlParts[1];
    String albumID = urlParts[2];

    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("albumID", albumID);
    jsonObject.addProperty("reviewType", reviewType);


    if (isValidRequest(jsonObject)) {
      producer.publishMessage(jsonObject.toString());
      response.setStatus(HttpServletResponse.SC_OK);
      response.getWriter().write("{\"message\": \"Review processed\"}");
    } else {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.getWriter().write("{\"error\": \"Invalid request data\"}");
    }
  }

  private boolean isValidRequest(JsonObject jsonObject) {
    return jsonObject.has("albumID") && jsonObject.has("reviewType");
  }
}
