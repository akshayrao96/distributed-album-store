package part2;

import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.DefaultApi;
import io.swagger.client.api.LikeApi;
import io.swagger.client.model.AlbumInfo;
import io.swagger.client.model.AlbumsProfile;
import io.swagger.client.model.ImageMetaData;
import java.io.File;

public class RequestHandler2 {

  private static final int MAX_REQUESTS = 5;
  private static String id = "1";

  public static ResponseData postAlbum(DefaultApi albumApi) {
    long startTime = System.currentTimeMillis();
    int curr = 0;
    AlbumsProfile profile = new AlbumsProfile();
    profile.setArtist("string");
    profile.setTitle("string");
    profile.setYear("string");

    File image = new File("nmtb.png");

    while (curr < MAX_REQUESTS) {
      try {
        ApiResponse<ImageMetaData> data = albumApi.newAlbumWithHttpInfo(image, profile);
        long endTime = System.currentTimeMillis();
        long latency = endTime - startTime;
        id = data.getData().getAlbumID();
        return new ResponseData(startTime, "POST-ALBUM", latency, data.getStatusCode());
      } catch (ApiException e) {
        if (e.getCode() >= 400 && e.getCode() < 600) {
          curr++;
        } else {
          break;
        }
      }
    }
    if (curr >= MAX_REQUESTS) {
      System.err.println("Unable to post to server");
    }
    return null;
  }

  public static ResponseData postLike(LikeApi likeApi) {
    long startTime = System.currentTimeMillis();
    int curr = 0;
    while (curr < MAX_REQUESTS) {
      try {
        ApiResponse<Void> data = likeApi.reviewWithHttpInfo("like", id);
        long endTime = System.currentTimeMillis();
        long latency = endTime - startTime;
        return new ResponseData(startTime, "POST-LIKE", latency, data.getStatusCode());
      } catch (ApiException e) {
        if (e.getCode() >= 400 && e.getCode() < 600) {
          curr++;
        } else {
          break;
        }
      }
    }
    if (curr >= MAX_REQUESTS) {
      System.err.println("Unable to post to server");
    }
    return null;
  }

  public static ResponseData postDislike(LikeApi likeApi) {
    long startTime = System.currentTimeMillis();
    int curr = 0;
    while (curr < MAX_REQUESTS) {
      try {
        ApiResponse<Void> data = likeApi.reviewWithHttpInfo("dislike", id);
        long endTime = System.currentTimeMillis();
        long latency = endTime - startTime;
        return new ResponseData(startTime, "POST-DISLIKE", latency, data.getStatusCode());
      } catch (ApiException e) {
        if (e.getCode() >= 400 && e.getCode() < 600) {
          curr++;
        } else {
          break;
        }
      }
    }
    if (curr >= MAX_REQUESTS) {
      System.err.println("Unable to post to server");
    }
    return null;
  }

  public static ResponseData getAlbum(DefaultApi albumApi) {
    long startTime = System.currentTimeMillis();
    int curr = 0;
    while (curr < MAX_REQUESTS) {
      try {
        ApiResponse<AlbumInfo> album = albumApi.getAlbumByKeyWithHttpInfo(id);
        long endTime = System.currentTimeMillis();
        long latency = endTime - startTime;
        return new ResponseData(startTime, "GET", latency, album.getStatusCode());
      } catch (ApiException e) {
        if (e.getCode() >= 400 && e.getCode() < 600) {
          curr++;
        } else {
          break;
        }
      }
    }
    if (curr >= MAX_REQUESTS) {
      System.err.println("Unable to getAlbum from server");
    }
    return null;
  }
}
