package ch.fdlo.hoerbuchspion.crawler;

import java.io.IOException;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;

import org.apache.hc.core5.http.ParseException;

public class ArtistsFromPlaylistsFetcher extends Fetcher<String> {
  private final String playlistId;

  public ArtistsFromPlaylistsFetcher(SpotifyApi authorizedApi, String playlistId) {
    super(authorizedApi);

    this.playlistId = playlistId;
  }

  @Override
  public Iterable<String> fetch() throws ParseException, SpotifyWebApiException, IOException {
    var builder = this.spotifyApi.getPlaylistsItems(this.playlistId);

    return this.executeRequest(builder, (PlaylistTrack track) -> {
      return track.getTrack().
    });
  }

}
