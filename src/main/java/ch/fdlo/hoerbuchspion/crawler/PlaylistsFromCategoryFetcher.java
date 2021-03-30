package ch.fdlo.hoerbuchspion.crawler;

import ch.fdlo.hoerbuchspion.crawler.types.SpotifyObject;
import com.neovisionaries.i18n.CountryCode;
import com.wrapper.spotify.SpotifyApi;

public class PlaylistsFromCategoryFetcher extends AbstractFetcher<SpotifyObject> {
  public PlaylistsFromCategoryFetcher(SpotifyApi authorizedApi) {
    super(authorizedApi);
  }

  @Override
  public Iterable<SpotifyObject> fetch(String id) {
    var builder = this.spotifyApi.getCategorysPlaylists(id);

    builder.country(CountryCode.DE);

    return this.executeRequest(builder, playlist -> {
      return SpotifyObject.playlist(playlist.getId(), playlist.getName());
    });
  }
}
