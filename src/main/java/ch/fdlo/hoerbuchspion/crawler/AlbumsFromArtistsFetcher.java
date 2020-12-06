package ch.fdlo.hoerbuchspion.crawler;

import java.io.IOException;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.AlbumSimplified;

import org.apache.hc.core5.http.ParseException;

public class AlbumsFromArtistsFetcher extends Fetcher<String> {
  private final String artistId;

  public AlbumsFromArtistsFetcher(SpotifyApi authorizedApi, String artistId) {
    super(authorizedApi);

    this.artistId = artistId;
  }

  @Override
  public Iterable<String> fetch() throws ParseException, SpotifyWebApiException, IOException {
    var builder = this.spotifyApi.getArtistsAlbums(this.artistId);

    return this.executeRequest(builder, (AlbumSimplified album) -> {
      return album.getName();
    });
  }

}
