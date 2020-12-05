package ch.fdlo.hoerbuchspion.crawler;

import java.io.IOException;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;

import org.apache.hc.core5.http.ParseException;

public class PlaylistsFromProfilesFetcher extends Fetcher<String> {
  private final String profileId;

  public PlaylistsFromProfilesFetcher(SpotifyApi authorizedApi, String profileId) {
    super(authorizedApi);

    this.profileId = profileId;
  }

  @Override
  public Iterable<String> fetch() throws ParseException, SpotifyWebApiException, IOException {
    var builder = this.spotifyApi.getListOfUsersPlaylists(this.profileId);

    return this.executeRequest(builder, (PlaylistSimplified playlist) -> {
      return playlist.getId();
    });
  }

}
