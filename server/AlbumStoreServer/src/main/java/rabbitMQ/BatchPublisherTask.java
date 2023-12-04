package rabbitMQ;

import java.util.TimerTask;

public class BatchPublisherTask extends TimerTask {
  private RabbitMQProducer producer;

  public BatchPublisherTask(RabbitMQProducer producer) {
    this.producer = producer;
  }

  @Override
  public void run() {
    try {
      producer.publishMessagesInBatch();
    } catch (Exception e) {
      e.printStackTrace(); // Handle exception appropriately
    }
  }
}
