package ch.fdlo.hoerbuchspion.crawler;

import ch.fdlo.hoerbuchspion.crawler.types.SpotifyObject;
import com.neovisionaries.i18n.CountryCode;
import com.wrapper.spotify.SpotifyApi;

public class AlbumsFromArtistFetcher extends AbstractFetcher<SpotifyObject> {
  public AlbumsFromArtistFetcher(SpotifyApi authorizedApi) {
    super(authorizedApi);
  }

  @Override
  public Iterable<SpotifyObject> fetch(String id) {
    var builder = this.spotifyApi.getArtistsAlbums(id);

    builder.market(CountryCode.DE);

    return this.executeRequest(builder, album -> {
      return SpotifyObject.album(album.getId(), album.getName());
    });
  }
}
