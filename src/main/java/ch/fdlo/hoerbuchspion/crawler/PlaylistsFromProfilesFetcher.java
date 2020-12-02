package ch.fdlo.hoerbuchspion.crawler;

import com.wrapper.spotify.SpotifyApi;

public class PlaylistsFromProfilesFetcher extends Fetcher<String> {
  private final String profileId;

  public PlaylistsFromProfilesFetcher(SpotifyApi authorizedApi, String profileId) {
    super(authorizedApi);

    this.profileId = profileId;
  }

@Override
  public Iterable<String> fetch() {
    this.spotifyApi.getUsersProfile(profileId);
  }

}
