package rabbitMQ;

import config.DynamoDBConfig;
import dynamoDB.DynamoDBController;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@WebListener
public class RabbitMQListener implements ServletContextListener {

  private ExecutorService executorService;
  private RabbitMQProducer producer;
  private final int NUM_CONSUMERS = 20;
  private List<RabbitMQConsumer> consumers = new ArrayList<>();
  private Timer batchPublishTimer;

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    executorService = Executors.newFixedThreadPool(NUM_CONSUMERS);
    consumers = new ArrayList<>(); // Initialize the list of consumers

    for (int i = 0; i < NUM_CONSUMERS; i++) {
      try {
        RabbitMQConsumer consumer = new RabbitMQConsumer(
            "reviewsExchange",
            new DynamoDBController(DynamoDBConfig.initDBClient(), "AlbumsData"),
            "myQueue"
        );
        consumers.add(consumer);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    // Initialize the RabbitMQProducer
    producer = new RabbitMQProducer("reviewsExchange");
    sce.getServletContext().setAttribute("rabbitMQProducer", producer);

    // Schedule the BatchPublisherTask for periodic batch publishing
    batchPublishTimer = new Timer();
    BatchPublisherTask batchTask = new BatchPublisherTask(producer);
    // Schedule the task for periodic execution, e.g., every 10 seconds
    batchPublishTimer.scheduleAtFixedRate(batchTask, 0, 5000);
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
    for (RabbitMQConsumer consumer : consumers) {
      consumer.close();
    }
    executorService.shutdown();
    try {
      if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
        executorService.shutdownNow();
      }
    } catch (InterruptedException e) {
      executorService.shutdownNow();
      Thread.currentThread().interrupt();
    }

    // Cancel the batch publishing timer
    if (batchPublishTimer != null) {
      batchPublishTimer.cancel();
    }

    // Close the RabbitMQ producer
    if (producer != null) {
      producer.close();
    }
  }
}
