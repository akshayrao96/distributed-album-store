import com.rabbitmq.client.*;
import dynamoDB.DynamoDBController;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class RabbitMQConsumer {

  private final String queueName;
  private final DynamoDBController dbController;

  private final String HOST = "35.87.82.240";

  public RabbitMQConsumer(String queueName, DynamoDBController dbController) {
    this.queueName = queueName;
    this.dbController = dbController;
  }

  public void startConsumer() {
    ConnectionFactory factory = new ConnectionFactory();

    factory.setHost(HOST);
    factory.setPort(5672);
    factory.setUsername("guest");
    factory.setPassword("guest");

    try (Connection connection = factory.newConnection();
        Channel channel = connection.createChannel()) {

      channel.queueDeclare(queueName, true, false, false, null);
      DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
        // Process the message and update the database
        processMessageAndUpdateDB(message);
      };
      channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {});
    } catch (IOException | TimeoutException e) {
      e.printStackTrace();
    }
  }

  private void processMessageAndUpdateDB(String message) {
    try {
      JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();
      String albumID = jsonObject.get("albumID").getAsString();
      String reviewType = jsonObject.get("reviewType").getAsString();

      // Logic to update the database
      if (reviewType.equals("like")) {
        dbController.incrementLike(albumID);
      } else if ("dislike".equals(reviewType)) {
        dbController.incrementDislike(albumID);
      }
    } catch (Exception e) {
      System.err.println("Failed to process message: " + e.getMessage());
    }
  }
}
