package model;

import java.util.concurrent.atomic.AtomicInteger;
import model.Album;

public class AlbumProfile {

  private static final AtomicInteger idCounter = new AtomicInteger(1);
  private String albumID;
  private Album profile;

  public AlbumProfile(Album profile) {
    this.albumID = String.valueOf(idCounter.getAndIncrement());
    this.profile = profile;
  }

  public String getAlbumID() {
    return albumID;
  }

  public Album getProfile() {
    return profile;
  }
}
