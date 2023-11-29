package rabbitMQ;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.TimeoutException;


public class RabbitMQProducer {

  private final String queueName;
  private final ConnectionFactory factory;
  private Connection connection;
  private Channel channel;
  private final String HOST = "35.87.82.240";


  public RabbitMQProducer(String queueName) {
    this.queueName = queueName;
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
      channel.queueDeclare(queueName, false, false, false, null);
    } catch (IOException | TimeoutException e) {
      throw new RuntimeException("Failed to connect to RabbitMQ", e);
    }
  }

  public void publishMessage(String message) {
    try {
      channel.basicPublish("", queueName, null, message.getBytes());
    } catch (IOException e) {
      throw new RuntimeException("Failed to publish message to RabbitMQ", e);
    }
  }

  public void close() {
    try {
      if (channel != null) {
        channel.close();
      }
      if (connection != null) {
        connection.close();
      }
    } catch (IOException | TimeoutException e) {
      System.err.println("Failed to close RabbitMQ connection/channel: " + e.getMessage());
    }
  }
}
