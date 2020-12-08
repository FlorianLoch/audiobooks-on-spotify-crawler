package ch.fdlo.hoerbuchspion.crawler.types;

import com.wrapper.spotify.enums.AlbumGroup;
import com.wrapper.spotify.model_objects.specification.AlbumSimplified;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Album {
  @Id
  private String id;
  private String name;
  @ManyToOne
  private Artist artist;
  private String releaseDate;
  private String albumArtUrl;
  private AlbumType albumType;
  private StoryType storyType;

  public Album(AlbumSimplified album) {
    this.id = album.getId();

    this.name = album.getName();

    assert (album.getArtists().length > 0);
    var primaryArtist = album.getArtists()[0];
    this.artist = new Artist(primaryArtist);

    this.releaseDate = album.getReleaseDate();

    this.albumArtUrl = "";
    if (album.getImages().length > 0) {
      this.albumArtUrl = album.getImages()[0].getUrl();
    }

    // albumGroup reflects the relation between the artist and this album.
    // albumType describes the album itself - we prefer to check the upper one.
    this.albumType = AlbumType.from(album.getAlbumGroup());

    this.storyType = StoryType.analyze(album.getName());
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Artist getArtist() {
    return artist;
  }

  public String getReleaseDate() {
    return releaseDate;
  }

  public String getAlbumArtUrl() {
    return albumArtUrl;
  }

  public AlbumType getAlbumType() {
    return albumType;
  }

  public StoryType getStoryType() {
    return storyType;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Album) {
      return this.id.equals(((Album) obj).id);
    }

    return false;
  }

  @Override
  public int hashCode() {
    return this.id.hashCode();
  }

  @Override
  public String toString() {
    return "ALBUM: " + this.name + " (" + this.id + "), " + this.releaseDate + ", " + this.storyType;
  }

  public enum StoryType {
    ABRIDGED("abridged"), UNABRIDGED("unabridged"), UNKNOWN ("unknown");

    private String name;

    private StoryType(String name) {
      this.name = name;
    }

    @Override
    public String toString() {
      return this.name;
    }

    public static StoryType analyze(String albumName) {
      albumName = albumName.toLowerCase();

      // As the check below is a subset we need to check for the full sequence first
      if (albumName.contains("unabridged") || albumName.contains("ungekürzt")) {
        return UNABRIDGED;
      }

      if (albumName.contains("abridged") || albumName.contains("gekürzt")) {
        return ABRIDGED;
      }

      return UNKNOWN;
    }
  }

  public enum AlbumType {
    ALBUM, SINGLE, COMPILATION, APPEARS_ON, UNKNOWN;

    public static AlbumType from(AlbumGroup albumType) {
      switch (albumType) {
        case ALBUM:
          return ALBUM;
        case SINGLE:
          return SINGLE;
        case COMPILATION:
          return COMPILATION;
        case APPEARS_ON:
          return APPEARS_ON;
        default:
          return UNKNOWN;
      }
    }
  }
}
