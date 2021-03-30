package ch.fdlo.hoerbuchspion.crawler;

import ch.fdlo.hoerbuchspion.crawler.types.SpotifyObject;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.model_objects.IPlaylistItem;
import com.wrapper.spotify.model_objects.specification.Track;

public class ArtistsFromPlaylistFetcher extends AbstractFetcher<SpotifyObject> {
  public ArtistsFromPlaylistFetcher(SpotifyApi authorizedApi) {
    super(authorizedApi);
  }

  @Override
  public Iterable<SpotifyObject> fetch(String id) {
    var builder = this.spotifyApi.getPlaylistsItems(id);

    return this.executeRequest(builder, playlistTrack -> {
      IPlaylistItem playlistItem = playlistTrack.getTrack();
      // TODO: Add instance-of check
      Track track = (Track) playlistItem;
      var artists = track.getArtists();

      assert artists.length > 0;

      return SpotifyObject.artist(artists[0].getId(), artists[0].getName());
    });
  }
}
