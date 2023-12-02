package part1;

import io.swagger.client.ApiException;
import io.swagger.client.api.DefaultApi;
import io.swagger.client.model.AlbumInfo;
import io.swagger.client.model.AlbumsProfile;
import io.swagger.client.model.ImageMetaData;
import java.io.File;

public class RequestHandler1 {

  private static final int MAX_REQUESTS = 5;

  public static void get(DefaultApi albumApi) {
    int curr = 0;
    while (curr < MAX_REQUESTS) {
      try {
        AlbumInfo album = albumApi.getAlbumByKey("1");
        break;
      } catch (ApiException e) {
        if (e.getCode() >= 400 && e.getCode() < 600) {
          System.out.println(e.getCode());
          curr++;
        } else {
          break;
        }
      }
    }
    if (curr >= MAX_REQUESTS) {
      System.err.println("Unable to getAlbum from server");
    }
  }

  public static void post(DefaultApi albumApi) {
    int curr = 0;
    AlbumsProfile profile = new AlbumsProfile();
    profile.setArtist("string");
    profile.setTitle("string");
    profile.setYear("string");

    File image = new File("nmtb.png");

    while (curr < MAX_REQUESTS) {
      try {
        ImageMetaData data = albumApi.newAlbum(image, profile);
        break;
      } catch (ApiException e) {
        if (e.getCode() >= 400 && e.getCode() < 600) {
          System.out.println(e.getCode());
          curr++;
        } else {
          break;
        }
      }
    }
    if (curr >= MAX_REQUESTS) {
      System.err.println("Unable to post to server");
    }
  }
}

