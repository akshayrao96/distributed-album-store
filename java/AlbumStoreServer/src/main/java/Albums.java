import java.util.Objects;

public class Albums {

  private String artist;
  private String title;
  private String year;

  public Albums(String artist, String title, String year) {
    this.artist = artist;
    this.title = title;
    this.year = year;
  }

  public String getArtist() {
    return artist;
  }

  public String getTitle() {
    return title;
  }

  public String getYear() {
    return year;
  }

  public void setArtist(String artist) {
    this.artist = artist;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setYear(String year) {
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
    Albums albums = (Albums) o;
    return Objects.equals(artist, albums.artist) && Objects.equals(title,
        albums.title) && Objects.equals(year, albums.year);
  }

  @Override
  public int hashCode() {
    return Objects.hash(artist, title, year);
  }
}
