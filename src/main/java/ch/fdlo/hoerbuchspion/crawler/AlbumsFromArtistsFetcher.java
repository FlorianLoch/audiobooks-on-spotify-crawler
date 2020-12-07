package ch.fdlo.hoerbuchspion.crawler;

import java.io.IOException;

import com.neovisionaries.i18n.CountryCode;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.AlbumSimplified;

import org.apache.hc.core5.http.ParseException;

import ch.fdlo.hoerbuchspion.crawler.types.Album;

public class AlbumsFromArtistsFetcher extends AbstractFetcher<Album> {
  private final String artistId;

  public AlbumsFromArtistsFetcher(SpotifyApi authorizedApi, String artistId) {
    super(authorizedApi);

    this.artistId = artistId;
  }

  @Override
  public Iterable<Album> fetch() throws ParseException, SpotifyWebApiException, IOException {
    var builder = this.spotifyApi.getArtistsAlbums(this.artistId);

    builder.market(CountryCode.DE);
    // TODO: only request albums, no compilations etc..
    // see "include_groups": https://developer.spotify.com/documentation/web-api/reference-beta/#endpoint-get-an-artists-albums

    return this.executeRequest(builder, (AlbumSimplified album) -> {
      return new Album(album);
    });
  }

}
