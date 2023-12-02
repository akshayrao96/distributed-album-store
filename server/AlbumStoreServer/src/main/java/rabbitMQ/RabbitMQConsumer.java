package rabbitMQ;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rabbitmq.client.*;

import dynamoDB.DynamoDBController;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class RabbitMQConsumer {

  private final String exchangeName;
  private final String queueName;
  private final String routingKey = "myRoutingKey";
  private final DynamoDBController dbController;
  private Connection connection;
  private Channel channel;
  private final String HOST = "35.86.106.120";

  public RabbitMQConsumer(String exchangeName, DynamoDBController dbController, String queueName) {
    this.exchangeName = exchangeName;
    this.queueName = queueName;
    this.dbController = dbController;

    initializeRabbitMQ();
  }

  private void initializeRabbitMQ() {
    try {
      ConnectionFactory factory = new ConnectionFactory();
      factory.setHost(HOST);
      factory.setPort(5672);
      factory.setUsername("guest");
      factory.setPassword("guest");

      connection = factory.newConnection();
      channel = connection.createChannel();

      channel.exchangeDeclare(exchangeName, "direct");
      channel.queueDeclare(queueName, true, false, false, null);
      channel.queueBind(queueName, exchangeName, routingKey);

      DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
        processMessageAndUpdateDB(message, delivery.getEnvelope().getDeliveryTag());
      };

      channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {});
    } catch (IOException | TimeoutException e) {
      throw new RuntimeException("Failed to initialize RabbitMQ consumer", e);
    }
  }

  private void processMessageAndUpdateDB(String message, long deliveryTag) {
    try {
      JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();
      String albumID = jsonObject.get("albumID").getAsString();
      String reviewType = jsonObject.get("reviewType").getAsString();

      if ("like".equals(reviewType)) {
        dbController.incrementLike(albumID);
      } else if ("dislike".equals(reviewType)) {
        dbController.incrementDislike(albumID);
      }
    } catch (Exception e) {
      System.err.println("Failed to process message: " + e.getMessage());
    }
  }

  public void close() {
    try {
      if (channel != null && channel.isOpen()) {
        channel.close();
      }
      if (connection != null && connection.isOpen()) {
        connection.close();
      }
    } catch (IOException | TimeoutException e) {
      e.printStackTrace();
    }
  }
}
