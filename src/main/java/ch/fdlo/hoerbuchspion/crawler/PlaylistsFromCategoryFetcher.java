package ch.fdlo.hoerbuchspion.crawler;

import java.io.IOException;

import com.neovisionaries.i18n.CountryCode;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.PlaylistSimplified;

import org.apache.hc.core5.http.ParseException;

import ch.fdlo.hoerbuchspion.crawler.types.Playlist;

public class PlaylistsFromCategoryFetcher extends AbstractFetcher<Playlist> {
  private final String category;

  public PlaylistsFromCategoryFetcher(SpotifyApi authorizedApi, String category) {
    super(authorizedApi);

    this.category = category;
  }

  @Override
  public Iterable<Playlist> fetch() throws ParseException, SpotifyWebApiException, IOException {
    var builder = this.spotifyApi.getCategorysPlaylists(this.category);

    builder.country(CountryCode.DE);

    return this.executeRequest(builder, (PlaylistSimplified playlist) -> {
      return new Playlist(playlist);
    });
  }

}
