import java.text.SimpleDateFormat;
import java.util.Date;

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

  @SuppressWarnings("DefaultLocale")
  public String toCsvRow() {
    @SuppressWarnings("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    String formattedStartTime = sdf.format(new Date(startTime));
    return String.format("%s, %s, %d, %d", formattedStartTime, requestType, latency, responseCode);
  }
}
