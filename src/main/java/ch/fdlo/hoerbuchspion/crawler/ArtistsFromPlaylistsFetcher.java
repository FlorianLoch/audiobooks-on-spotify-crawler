package ch.fdlo.hoerbuchspion.crawler;

import java.io.IOException;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.IPlaylistItem;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import com.wrapper.spotify.model_objects.specification.Track;

import org.apache.hc.core5.http.ParseException;

import ch.fdlo.hoerbuchspion.crawler.types.Artist;

public class ArtistsFromPlaylistsFetcher extends AbstractFetcher<Artist> {
  private final String playlistId;

  public ArtistsFromPlaylistsFetcher(SpotifyApi authorizedApi, String playlistId) {
    super(authorizedApi);

    this.playlistId = playlistId;
  }

  @Override
  public Iterable<Artist> fetch() throws ParseException, SpotifyWebApiException, IOException {
    var builder = this.spotifyApi.getPlaylistsItems(this.playlistId);

    return this.executeRequest(builder, (PlaylistTrack playlistTrack) -> {
      IPlaylistItem playlistItem = playlistTrack.getTrack();
      // TODO: Add instance of check
      Track track = (Track) playlistItem;
      var artists = track.getArtists();

      if (artists.length == 0) {
        return Artist.getArtist("UNKNOWN", "UNKNOWN");
      }

      return Artist.getArtist(artists[0]);

      // return Arrays.asList(artists).stream().skip(1).map((artist) -> {
      //   return artist.getName();
      // }).reduce(artists[0].getName(), (acc, artistName) -> {
      //   return acc + ", " + artistName;
      // });
    });
  }

}
