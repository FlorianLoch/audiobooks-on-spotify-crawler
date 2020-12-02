package ch.fdlo.hoerbuchspion.crawler;

import com.wrapper.spotify.SpotifyApi;

public abstract class Fetcher<T> {
  protected final SpotifyApi spotifyApi;

  public Fetcher(SpotifyApi authorizedApi) {
    this.spotifyApi = authorizedApi;
  }

  abstract public Iterable<T> fetch();
}
