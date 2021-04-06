package ch.fdlo.hoerbuchspion.crawler.types;

import ch.fdlo.hoerbuchspion.crawler.languageDetector.Language;
import com.wrapper.spotify.model_objects.specification.Copyright;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "ALBUM")
public class Album {
  @Id
  private String id;
  private String name;
  @ManyToMany(cascade = CascadeType.MERGE)
  private List<Artist> artists;
  private String releaseDate;
  @Embedded
  @AttributeOverrides({
          @AttributeOverride(name = "large", column = @Column(name = "albumArtLarge")),
          @AttributeOverride(name = "medium", column = @Column(name = "albumArtMedium")),
          @AttributeOverride(name = "small", column = @Column(name = "albumArtSmall"))
  })
  private ImageURLs albumArtURLs;
  @Enumerated(EnumType.STRING)
  private AlbumType albumType;
  @Enumerated(EnumType.STRING)
  private StoryType storyType;
  private int totalTracks;
  private long totalDurationMs;
  private boolean allTracksNotExplicit = true;
  private boolean allTracksPlayable = true;
  private String preview = ""; // URL to preview clip
  private int popularity;
  private String label;
  private String copyright;
  @Enumerated(EnumType.STRING)
  private Language assumedLanguage = Language.UNKNOWN;

  // Required by JPA
  private Album() {
  }

  public Album(com.wrapper.spotify.model_objects.specification.Album album, Language assumedLanguage) {
    this.id = album.getId();

    this.name = album.getName();

    assert (album.getArtists().length > 0);

    this.artists = new ArrayList<>(album.getArtists().length);
    for (var artist : album.getArtists()) {
      this.artists.add(Artist.getArtist(artist.getId()));
    }

    this.releaseDate = album.getReleaseDate();

    this.albumArtURLs = ImageURLs.from(album.getImages());

    this.albumType = AlbumType.from(album.getAlbumType());

    this.storyType = StoryType.analyze(album.getName());

    this.popularity = album.getPopularity();

    this.label = album.getLabel();

    this.copyright = Arrays.stream(album.getCopyrights()).map(Copyright::getText).collect(Collectors.joining(", "));

    this.assumedLanguage = assumedLanguage;
  }

  public void digestTrack(Track track) {
    this.totalTracks++;

    this.totalDurationMs = this.totalDurationMs + track.getDurationMs();

    this.allTracksNotExplicit = this.allTracksNotExplicit && !track.isExplicit();

    this.allTracksPlayable = this.allTracksPlayable && track.isPlayable();

    if (this.preview.isEmpty() && !track.getPreviewUrl().isEmpty()) {
      this.preview = track.getPreviewUrl();
    }
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public List<Artist> getArtists() {
    return artists;
  }

  public String getReleaseDate() {
    return releaseDate;
  }

  public ImageURLs getAlbumArtURLs() {
    return albumArtURLs;
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
    ABRIDGED("abridged"), UNABRIDGED("unabridged"), UNKNOWN("unknown");

    private String name;

    StoryType(String name) {
      this.name = name;
    }

    @Override
    public String toString() {
      return this.name;
    }

    public static StoryType analyze(String albumName) {
      albumName = albumName.toLowerCase();

      // As the second conditional block below is a subset we need to check for the full sequence first
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
    ALBUM, SINGLE, COMPILATION;

    public static AlbumType from(com.wrapper.spotify.enums.AlbumType albumType) {
      switch (albumType) {
        case ALBUM:
          return ALBUM;
        case SINGLE:
          return SINGLE;
        case COMPILATION:
          return COMPILATION;
        default:
          // This should never happen except the AlbumType enum gets extended
          assert false;
          return null;
      }
    }
  }
}
