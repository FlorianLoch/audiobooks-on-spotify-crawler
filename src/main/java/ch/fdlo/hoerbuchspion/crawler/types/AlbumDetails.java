package ch.fdlo.hoerbuchspion.crawler.types;

import javax.persistence.Embeddable;

@Embeddable
public class AlbumDetails {
  private int totalTracks;
  private long totalDurationMs;
  private boolean allTracksNotExplicit = true;
  private boolean allTracksPlayable = true;
  private String previewURL = "";

  public void processTrack(Track track) {
    this.totalTracks++;

    this.totalDurationMs = this.totalDurationMs + track.getDurationMs();

    this.allTracksNotExplicit = this.allTracksNotExplicit && !track.isExplicit();

    this.allTracksPlayable = this.allTracksPlayable && track.isPlayable();

    if (this.previewURL.isEmpty() && !track.getPreviewUrl().isEmpty()) {
      this.previewURL = track.getPreviewUrl();
    }
  }
}
