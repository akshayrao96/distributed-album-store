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

  @Override
  public void init() throws ServletException {
    super.init();
    // Retrieve the producer from the servlet context
    producer = (RabbitMQProducer) getServletContext().getAttribute("rabbitMQProducer");
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    String urlPath = request.getPathInfo();

    // Basic URL path validation
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
      try {
        // Queue the message for batch processing instead of directly publishing
        producer.queueMessage(jsonObject.toString());
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("{\"message\": \"Review queued\"}");
      } catch (Exception e) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.getWriter().write("{\"error\": \"Failed to queue review\"}");
      }
    } else {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.getWriter().write("{\"error\": \"Invalid request data\"}");
    }
  }

  private boolean isValidRequest(JsonObject jsonObject) {
    return jsonObject.has("albumID") && jsonObject.has("reviewType");
  }
}
