package ch.fdlo.hoerbuchspion.crawler.types;

public class Track {
  private final int durationMs;
  private final boolean explicit;
  private final boolean playable;
  private final String previewUrl;

  public Track(Integer durationMs, Boolean explicit, Boolean isPlayable, String previewUrl) {
    this.durationMs = durationMs;
    this.explicit = explicit;
    this.playable = isPlayable;

    // It occured, previewUrl can be null...
    if (previewUrl == null) {
      previewUrl = "";
    }
    this.previewUrl = previewUrl;
  }

  public int getDurationMs() {
    return durationMs;
  }

  public boolean isExplicit() {
    return explicit;
  }

  public boolean isPlayable() {
    return playable;
  }

  public String getPreviewUrl() {
    return previewUrl;
  }
}