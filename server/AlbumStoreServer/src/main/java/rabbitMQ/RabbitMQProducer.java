package rabbitMQ;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeoutException;

public class RabbitMQProducer {

  private final String exchangeName;
  private final ConnectionFactory factory;
  private Connection connection;
  private Channel channel;
  private final String HOST = "localhost";
  private final String ROUTING_KEY = "myRoutingKey"; // constant routing key

  // Queue to hold messages for batch processing
  private ConcurrentLinkedQueue<String> messageQueue = new ConcurrentLinkedQueue<>();

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
      // Enable publisher confirmations
      channel.confirmSelect();
    } catch (IOException | TimeoutException e) {
      throw new RuntimeException("Failed to connect to RabbitMQ", e);
    }
  }

  public void queueMessage(String message) {
    messageQueue.add(message);
  }

  public void publishMessagesInBatch() {
    try {
      while (!messageQueue.isEmpty()) {
        String message = messageQueue.poll();
        if (message != null) {
          channel.basicPublish(exchangeName, ROUTING_KEY, null, message.getBytes("UTF-8"));
        }
      }
      channel.waitForConfirmsOrDie(5000); // Wait for confirmation, with a timeout
    } catch (IOException | TimeoutException | InterruptedException e) {
      throw new RuntimeException("Failed to publish messages in batch to RabbitMQ", e);
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

