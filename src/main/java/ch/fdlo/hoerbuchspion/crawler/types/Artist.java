package ch.fdlo.hoerbuchspion.crawler.types;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.wrapper.spotify.model_objects.specification.ArtistSimplified;

@Entity
@Table
public class Artist {
  static Map<String, Artist> instances = new HashMap<>();

  @Id
  private String id;
  private String name;

  // Required by JPA
  private Artist() {}

  private Artist(String id, String name) {
    this.id = id;
    this.name = name;
  }

  public static Artist getArtist(String id, String name) {
    Artist instance = instances.get(id);

    if (instance == null) {
      instance = new Artist(id, name);
      instances.put(id, instance);
    }

    return instance;
  }

  public static Artist getArtist(ArtistSimplified artist) {
    return getArtist(artist.getId(), artist.getName());
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Artist) {
      return this.id.equals(((Artist) obj).id);
    }

    return false;
  }

  @Override
  public int hashCode() {
    return this.id.hashCode();
  }

  @Override
  public String toString() {
    return "ARTIST: " + this.name + " (" + this.id + ")";
  }
}
