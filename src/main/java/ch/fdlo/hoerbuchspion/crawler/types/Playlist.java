package ch.fdlo.hoerbuchspion.crawler.types;

import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;

public class Playlist {
  private String id;
  private String name;

  public Playlist(String id, String name) {
    this.id = id;
    this.name = name;
  }

  public Playlist(PlaylistSimplified playlist) {
    this.id = playlist.getId();
    this.name = playlist.getName();
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Playlist) {
      return this.id.equals(((Playlist) obj).id);
    }
    
    return false;
  }

  @Override
  public int hashCode() {
    return this.id.hashCode();
  }

  @Override
  public String toString() {
    return "PLAYLIST: " + this.name + " (" + this.id + ")";
  }
}
