package ch.fdlo.hoerbuchspion.crawler.types;

import com.wrapper.spotify.model_objects.specification.ArtistSimplified;

public class Artist {
  private String id;
  private String name;

  public Artist(String id, String name) {
    this.id = id;
    this.name = name;
  }

  public Artist(ArtistSimplified artist) {
    this(artist.getId(), artist.getName());
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return "ARTIST: " + this.name + " (" + this.id + ")";
  }
}
