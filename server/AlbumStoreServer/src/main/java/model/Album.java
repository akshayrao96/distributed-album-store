package model;

import java.util.Objects;

public class Album {

  private final String artist;
  private final String title;
  private final String year;

  public Album(String artist, String title, String year) {
    this.artist = artist;
    this.title = title;
    this.year = year;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Album albums = (Album) o;
    return Objects.equals(artist, albums.artist) && Objects.equals(title,
        albums.title) && Objects.equals(year, albums.year);
  }

  @Override
  public int hashCode() {
    return Objects.hash(artist, title, year);
  }
}
