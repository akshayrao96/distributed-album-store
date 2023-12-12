package part2;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ResponseData {

  protected long startTime;
  protected String requestType;
  protected long latency;
  protected int responseCode;
  protected int albumKey;

  public ResponseData(long startTime, String requestType, long latency, int responseCode) {
    this.startTime = startTime;
    this.requestType = requestType;
    this.latency = latency;
    this.responseCode = responseCode;
  }


  @SuppressWarnings("DefaultLocale")
  public String toCsvRow() {
    @SuppressWarnings("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    String formattedStartTime = sdf.format(new Date(startTime));
    return String.format("%s, %s, %d, %d", formattedStartTime, requestType, latency, responseCode);
  }

  public String getRequestType() {
    return this.requestType;
  }

  public Long getLatency() {
    return this.latency;
  }

  //return the response code
  public int getResponseCode() {
    return this.responseCode;
  }
}
