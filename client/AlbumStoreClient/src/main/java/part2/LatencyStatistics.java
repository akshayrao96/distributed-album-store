package part2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LatencyStatistics {

  private final Map<String, List<Long>> responseTimesByType;

  public LatencyStatistics() {
    responseTimesByType = new HashMap<>();
  }

  public void addResponseTime(String requestType, long responseTime) {
    responseTimesByType.computeIfAbsent(requestType, k -> new ArrayList<>()).add(responseTime);
  }

  public long calculateMean(String requestType) {
    List<Long> responseTimes = getResponseTimes(requestType);
    if (responseTimes.isEmpty()) {
      return 0;
    }
    long totalResponseTime = responseTimes.stream().mapToLong(Long::longValue).sum();
    return totalResponseTime / responseTimes.size();
  }

  public long calculateMedian(String requestType) {
    List<Long> responseTimes = getSortedResponseTimes(requestType);
    if (responseTimes.isEmpty()) {
      return 0;
    }
    int middle = responseTimes.size() / 2;
    if (responseTimes.size() % 2 == 1) {
      return responseTimes.get(middle);
    } else {
      return (responseTimes.get(middle - 1) + responseTimes.get(middle)) / 2;
    }
  }

  public long calculatePercentile(String requestType, int percentile) {
    List<Long> responseTimes = getSortedResponseTimes(requestType);
    if (responseTimes.isEmpty()) {
      return 0;
    }
    int index = (int) Math.ceil(percentile / 100.0 * responseTimes.size()) - 1;
    return responseTimes.get(Math.max(index, 0));
  }

  public long calculateMin(String requestType) {
    return responseTimesByType.getOrDefault(requestType, Collections.emptyList())
        .stream()
        .min(Long::compare)
        .orElse(0L);
  }

  public long calculateMax(String requestType) {
    return responseTimesByType.getOrDefault(requestType, Collections.emptyList())
        .stream()
        .max(Long::compare)
        .orElse(0L);
  }

  private List<Long> getResponseTimes(String requestType) {
    return responseTimesByType.getOrDefault(requestType, Collections.emptyList());
  }

  private List<Long> getSortedResponseTimes(String requestType) {
    List<Long> sortedTimes = new ArrayList<>(getResponseTimes(requestType));
    Collections.sort(sortedTimes);
    return sortedTimes;
  }
}
