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
  private final int NUM_CONSUMERS = 22;
  private List<RabbitMQConsumer> consumers = new ArrayList<>();
  private Timer batchPublishTimer;

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    executorService = Executors.newFixedThreadPool(NUM_CONSUMERS);

    // Initialize each consumer and handle exceptions per consumer
    for (int i = 0; i < NUM_CONSUMERS; i++) {
      try {
        RabbitMQConsumer consumer = new RabbitMQConsumer(
            "reviewsExchange",
            new DynamoDBController(DynamoDBConfig.initDBClient(), "AlbumsData"),
            "myQueue"
        );
        consumers.add(consumer);
        // TODO: Optionally start consumer threads here if needed
      } catch (IOException e) {
        // Log the exception and continue initializing other consumers
        e.printStackTrace(); // Consider using a logging framework
      }
    }

    // Initialize RabbitMQProducer with error handling
    try {
      producer = new RabbitMQProducer("reviewsExchange");
      sce.getServletContext().setAttribute("rabbitMQProducer", producer);
    } catch (Exception e) {
      e.printStackTrace(); // Log this exception
    }

    // Schedule the BatchPublisherTask
    try {
      batchPublishTimer = new Timer();
      BatchPublisherTask batchTask = new BatchPublisherTask(producer);
      batchPublishTimer.scheduleAtFixedRate(batchTask, 0, 5000);
    } catch (Exception e) {
      e.printStackTrace(); // Log this exception
    }
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
    // Close each consumer
    for (RabbitMQConsumer consumer : consumers) {
      if (consumer != null) {
        consumer.close();
      }
    }

    // Shutdown executor service
    executorService.shutdown();
    try {
      if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
        executorService.shutdownNow();
      }
    } catch (InterruptedException e) {
      executorService.shutdownNow();
      Thread.currentThread().interrupt();
      e.printStackTrace(); // Log this exception
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