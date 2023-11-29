import com.google.gson.Gson;
import com.google.gson.JsonObject;
import config.DynamoDBConfig;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import albums.Album;
import albums.AlbumProfile;
import dynamoDB.DynamoDBController;
import dynamoDB.DynamoDBTable;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

@WebServlet(name = "AlbumsServlet", value = "/albums/*")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 10,
    maxFileSize = 1024 * 1024 * 50,
    maxRequestSize = 1024 * 1024 * 100)
public class AlbumsServlet extends HttpServlet {

  private DynamoDBController dbController;
  private final String TABLE_NAME = "AlbumsData";

  @Override
  public void init() throws ServletException {
    super.init();
    DynamoDbClient ddb = DynamoDBConfig.initDBClient();
    try {
      dbController = new DynamoDBController(ddb, TABLE_NAME);
      if (!DynamoDBTable.doesTableExist(ddb, TABLE_NAME)) {
        DynamoDBTable.createTable(ddb, TABLE_NAME);
      }
    } catch (IOException | DynamoDbException e) {
      throw new RuntimeException("Initialization failed: " + e.getMessage(), e);
    }
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    response.setContentType("application/json");
    Gson gson = new Gson();

    String urlPath = request.getPathInfo();

    Album album;

    String[] urlParts = urlPath.split("/");
    if (urlParts.length != 2) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    } else {
      album = dbController.getProfile(urlParts[1]);
      response.getOutputStream().print(gson.toJson(album));
      response.setStatus(HttpServletResponse.SC_OK);
    }
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    response.setContentType("application/json");

    Part profilePart = request.getPart("profile");
    JsonObject profileObject = null;

    AlbumProfile albumProfile = null;

    if (profilePart != null) {
      try (BufferedReader reader = new BufferedReader(
          new InputStreamReader(profilePart.getInputStream()))) {
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
          stringBuilder.append(line);
        }
        String profileJson = stringBuilder.toString();

        if (profileJson.substring(0, 1).equals("c")) {
          profileJson = profileJson.replace("class AlbumsProfile {", "").replace("}", "").trim();
          String[] keyValuePairs = profileJson.split(":\\s+string");
          StringBuilder jsonString = new StringBuilder();
          jsonString.append("{");
          for (int i = 0; i < keyValuePairs.length - 1; i++) {
            String[] parts = keyValuePairs[i].split(":\\s+");
            jsonString.append("\"").append(parts[0].trim()).append("\":\"string\",");
          }
          String lastPair = keyValuePairs[keyValuePairs.length - 1].trim();
          String[] parts = lastPair.split(":\\s+");
          jsonString.append("\"").append(parts[0].trim()).append("\":\"string\"");
          jsonString.append("}");
          profileJson = jsonString.toString();
//          profileJson.replaceAll("\":\"", "\":");
        }
        Gson gson = new Gson();
        profileObject = gson.fromJson(profileJson, JsonObject.class);
      }
    }

    if (profileObject != null) {
      albumProfile = new AlbumProfile(new Album(
          profileObject.get("artist").getAsString(),
          profileObject.get("title").getAsString(),
          profileObject.get("year").getAsString()));
    }

    StringBuilder imgSize = new StringBuilder();
    Part image = request.getPart("image");
    long size = image.getSize();
    imgSize.append(size).append("KB");

    JsonObject jsonObject = new JsonObject();
    assert albumProfile != null;

    dbController.postProfile(albumProfile);

    jsonObject.addProperty("albumID", albumProfile.getAlbumID());
    jsonObject.addProperty("imageSize", imgSize.toString());

    try (PrintWriter out = response.getWriter()) {
      out.write(jsonObject.toString());
    }
  }
}

