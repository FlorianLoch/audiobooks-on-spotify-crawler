package ch.fdlo.hoerbuchspion.crawler;

import ch.fdlo.hoerbuchspion.crawler.types.SpotifyObject;
import com.wrapper.spotify.SpotifyApi;

public class PlaylistsFromProfileFetcher extends AbstractFetcher<SpotifyObject> {
  public PlaylistsFromProfileFetcher(SpotifyApi authorizedApi) {
    super(authorizedApi);
  }

  @Override
  public Iterable<SpotifyObject> fetch(String id) {
    var builder = this.spotifyApi.getListOfUsersPlaylists(id);

    return this.executeRequest(builder, playlist -> {
      return SpotifyObject.playlist(playlist.getId(), playlist.getName());
    });
  }
}
