package ch.fdlo.hoerbuchspion.crawler.types;

import java.util.*;

import javax.persistence.*;

import com.wrapper.spotify.model_objects.specification.ArtistSimplified;

@Entity
@Table(name = "ARTIST")
public class Artist {
  // Multiple threads might access this instance pool simultaneously
  private static Map<String, Artist> instances = new HashMap<>();

  @Id
  private String id;
  private String name;
  @Embedded
  @AttributeOverrides({
          @AttributeOverride(name = "large", column = @Column(name = "artistImageLarge")),
          @AttributeOverride(name = "medium", column = @Column(name = "artistImageMedium")),
          @AttributeOverride(name = "small", column = @Column(name = "artistImageSmall"))
  })
  private ImageURLs artistImageURLs;
  private int popularity;

  // Required by JPA
  private Artist() {
  }

  private Artist(String id) {
    this.id = id;
  }

  public static synchronized Artist getArtist(String id) {
    return instances.computeIfAbsent(id, Artist::new);
  }

  public synchronized static Collection<Artist> getAllArtists() {
    return Collections.unmodifiableCollection(instances.values());
  }

  public String getId() {
    return id;
  }


  public void setArtistImage(ImageURLs artistImageURLs) {
    this.artistImageURLs = artistImageURLs;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPopularity(int popularity) {
    this.popularity = popularity;
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
