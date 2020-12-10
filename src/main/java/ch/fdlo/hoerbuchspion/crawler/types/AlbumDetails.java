package ch.fdlo.hoerbuchspion.crawler.types;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import ch.fdlo.hoerbuchspion.crawler.LanguageDetector.Language;

@Embeddable
public class AlbumDetails {
  private int totalTracks;
  private long totalDurationMs;
  private boolean allTracksNotExplicit = true;
  private boolean allTracksPlayable = true;
  private String previewURL = "";
  private int popularity;
  private String label = "";
  private String copyright = "";
  @Enumerated(EnumType.STRING)
  private Language assumedLanguage = Language.UNKNOWN;


  public void processTrack(Track track) {
    this.totalTracks++;

    this.totalDurationMs = this.totalDurationMs + track.getDurationMs();

    this.allTracksNotExplicit = this.allTracksNotExplicit && !track.isExplicit();

    this.allTracksPlayable = this.allTracksPlayable && track.isPlayable();

    if (this.previewURL.isEmpty() && !track.getPreviewUrl().isEmpty()) {
      this.previewURL = track.getPreviewUrl();
    }
  }

  public void setPopularity(int popularity) {
    this.popularity = popularity;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public void setCopyright(String copyright) {
    this.copyright = copyright;
  }

  public void setAssumedLanguage(Language assumedLanguage) {
    this.assumedLanguage = assumedLanguage;
  }
}
