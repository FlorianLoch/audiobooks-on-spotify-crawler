package ch.fdlo.hoerbuchspion.crawler.types;

import javax.persistence.Embeddable;

@Embeddable
public class ArtistDetails {
  private String artistImage = "";
  private int popularity;

  public void setArtistImage(String artistImage) {
    this.artistImage = artistImage;
  }

  public void setPopularity(int popularity) {
    this.popularity = popularity;
  }
}
