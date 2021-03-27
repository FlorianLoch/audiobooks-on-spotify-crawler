package ch.fdlo.hoerbuchspion.crawler.types;

import javax.persistence.*;

@Embeddable
public class ArtistDetails {
  @Embedded
  @AttributeOverrides({
          @AttributeOverride(name = "large", column = @Column(name = "artistImageLarge")),
          @AttributeOverride(name = "medium", column = @Column(name = "artistImageMedium")),
          @AttributeOverride(name = "small", column = @Column(name = "artistImageSmall"))
  })
  private ImageURLs artistImageURLs;
  private int popularity;

  public void setArtistImage(ImageURLs artistImageURLs) {
    this.artistImageURLs = artistImageURLs;
  }

  public void setPopularity(int popularity) {
    this.popularity = popularity;
  }
}
