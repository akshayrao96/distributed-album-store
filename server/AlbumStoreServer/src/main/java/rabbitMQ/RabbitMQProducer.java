package rabbitMQ;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitMQProducer {

  private final String exchangeName;
  private final ConnectionFactory factory;
  private Connection connection;
  private Channel channel;
  private final String HOST = "35.86.106.120";
  private final String ROUTING_KEY = "myRoutingKey"; // constant routing key

  public RabbitMQProducer(String exchangeName) {
    this.exchangeName = exchangeName;
    factory = new ConnectionFactory();
    factory.setHost(HOST);
    factory.setPort(5672);
    factory.setUsername("guest");
    factory.setPassword("guest");

    initializeRabbitMQ();
  }

  private void initializeRabbitMQ() {
    try {
      connection = factory.newConnection();
      channel = connection.createChannel();
      channel.exchangeDeclare(exchangeName, "direct");
    } catch (IOException | TimeoutException e) {
      throw new RuntimeException("Failed to connect to RabbitMQ", e);
    }
  }

  public void publishMessage(String message) {
    try {
      channel.basicPublish(exchangeName, ROUTING_KEY, null, message.getBytes());
    } catch (IOException e) {
      throw new RuntimeException("Failed to publish message to RabbitMQ", e);
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
      System.err.println("Failed to close RabbitMQ resources: " + e.getMessage());
    }
  }
}
