import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.DefaultApi;
import io.swagger.client.model.AlbumInfo;
import io.swagger.client.model.AlbumsProfile;
import io.swagger.client.model.ImageMetaData;
import java.io.File;

public class RequestHandler {

  public static void get(DefaultApi albumApi) {
    try {
      // Use the albumsApi to make the GET request to list albums
      ApiResponse<AlbumInfo> album = albumApi.getAlbumByKeyWithHttpInfo("1");
    } catch (ApiException e) {
      // Handle exceptions
      System.err.println("API Exception: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public static void post(DefaultApi albumApi) {
    AlbumsProfile profile = new AlbumsProfile();
    profile.setArtist("string");
    profile.setTitle("string");
    profile.setYear("string");

    File image = new File("nmtb.png");

    try {
      ApiResponse<ImageMetaData> data = albumApi.newAlbumWithHttpInfo(image, profile);
    } catch (ApiException e) {
      e.printStackTrace();
    }
  }
}
