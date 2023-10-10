public class ResponseData {

  protected long startTime;
  protected String requestType;
  protected long latency;
  protected int responseCode;

  public ResponseData(long startTime, String requestType, long latency, int responseCode) {
    this.startTime = startTime;
    this.requestType = requestType;
    this.latency = latency;
    this.responseCode = responseCode;
  }
}
