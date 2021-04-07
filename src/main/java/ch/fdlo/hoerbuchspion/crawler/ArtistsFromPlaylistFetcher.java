package ch.fdlo.hoerbuchspion.crawler;

import ch.fdlo.hoerbuchspion.crawler.types.SpotifyArtistObject;
import ch.fdlo.hoerbuchspion.crawler.types.SpotifyObject;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.model_objects.IPlaylistItem;
import com.wrapper.spotify.model_objects.specification.Track;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ArtistsFromPlaylistFetcher extends AbstractFetcher<List<SpotifyArtistObject>> {
  public ArtistsFromPlaylistFetcher(SpotifyApi authorizedApi) {
    super(authorizedApi);
  }

  @Override
  public Iterable<List<SpotifyArtistObject>> fetch(String id) {
    var builder = this.spotifyApi.getPlaylistsItems(id);

    return this.executeRequest(builder, playlistTrack -> {
      IPlaylistItem playlistItem = playlistTrack.getTrack();

      if (!(playlistItem instanceof Track)) {
        // TODO: Log a warning message
        return Collections.emptyList();
      }

      Track track = (Track) playlistItem;
      var artists = track.getArtists();

      assert artists.length > 0;

      return Arrays.stream(artists).map(artist -> new SpotifyArtistObject(artist.getId(), artist.getName())).collect(Collectors.toList());
    });
  }
}
