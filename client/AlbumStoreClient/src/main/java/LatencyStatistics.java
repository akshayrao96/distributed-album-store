import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LatencyStatistics {
  private final List<Long> responseTimes;

  public LatencyStatistics() {
    responseTimes = new ArrayList<>();
  }

  public void addResponseTime(long responseTime) {
    responseTimes.add(responseTime);
  }

  public void addResponseTimesFromDeque(ConcurrentLinkedQueue<Long> responseTimeDeque) {
    responseTimes.addAll(responseTimeDeque);
  }

  public void addResponseTimesFromCSV(String filePath) throws IOException {
    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
      String line;
      while ((line = reader.readLine()) != null) {
        String[] parts = line.split(",");
        if (parts.length >= 3) {
          try {
            long responseTime = Long.parseLong(parts[2].trim());
            responseTimes.add(responseTime);
          } catch (NumberFormatException e) {
            // Ignore invalid data
          }
        }
      }
    }
  }

  public long calculateMean() {
    if (responseTimes.isEmpty()) {
      return 0;
    }

    long totalResponseTime = 0;
    for (long responseTime : responseTimes) {
      totalResponseTime += responseTime;
    }

    return totalResponseTime / responseTimes.size();
  }

  public long calculateMedian() {
    if (responseTimes.isEmpty()) {
      return 0;
    }

    Collections.sort(responseTimes);
    int middle = responseTimes.size() / 2;
    if (responseTimes.size() % 2 == 1) {
      return responseTimes.get(middle);
    } else {
      long left = responseTimes.get(middle - 1);
      long right = responseTimes.get(middle);
      return (left + right) / 2;
    }
  }

  public long calculatePercentile(int percentile) {
    if (responseTimes.isEmpty()) {
      return 0;
    }

    Collections.sort(responseTimes);
    int index = (int) Math.ceil((percentile / 100.0) * responseTimes.size()) - 1;
    return responseTimes.get(index);
  }

  public long calculateMin() {
    if (responseTimes.isEmpty()) {
      return 0;
    }

    return Collections.min(responseTimes);
  }

  public long calculateMax() {
    if (responseTimes.isEmpty()) {
      return 0;
    }

    return Collections.max(responseTimes);
  }
}