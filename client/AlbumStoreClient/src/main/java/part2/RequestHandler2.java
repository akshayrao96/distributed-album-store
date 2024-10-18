package part2;

import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.DefaultApi;
import io.swagger.client.api.LikeApi;
import io.swagger.client.model.AlbumInfo;
import io.swagger.client.model.AlbumsProfile;
import io.swagger.client.model.ImageMetaData;
import io.swagger.client.model.Likes;
import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

public class RequestHandler2 {

  private static final int MAX_REQUESTS = 5;
  private static String id = "1";

  public static ResponseData postAlbum(DefaultApi albumApi, AtomicInteger validAlbumKey) {
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

        //if the post request is successful, set the validAlbumKey to the albumID
        if(data.getStatusCode() == 201 || data.getStatusCode() == 200){
          int albumID = Integer.parseInt(data.getData().getAlbumID());
          if(albumID > validAlbumKey.get()){
            validAlbumKey.set(albumID);
          }
        }
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

  public static ResponseData getLikes(LikeApi likeApi, String id) {
    long startTime = System.currentTimeMillis();
    int curr = 0;
    while (curr < MAX_REQUESTS) {
      try {
        ApiResponse<Likes> data = likeApi.getLikesWithHttpInfo(id);
        long endTime = System.currentTimeMillis();
        long latency = endTime - startTime;
        return new ResponseData(startTime, "GET-LIKES", latency, data.getStatusCode());
      } catch (ApiException e) {
        if (e.getCode() >= 400 && e.getCode() < 600) {
          curr++;
        } else {
          break;
        }
      }
    }
    if (curr >= MAX_REQUESTS) {
      System.err.println("Unable to get album " + id);
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
