package albums;

import java.util.UUID;

public class AlbumProfile {

  private String albumID;
  private Album profile;

  public AlbumProfile(Album profile) {
    this.albumID = UUID.randomUUID().toString();
    this.profile = profile;
  }

  public String getAlbumID() {
    return albumID;
  }

  public Album getProfile() {
    return profile;
  }
}
