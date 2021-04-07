package ch.fdlo.hoerbuchspion.crawler;

import ch.fdlo.hoerbuchspion.crawler.types.SpotifyObject;
import ch.fdlo.hoerbuchspion.crawler.types.SpotifyPlaylistObject;
import ch.fdlo.hoerbuchspion.crawler.types.SpotifyProfileObject;
import com.wrapper.spotify.SpotifyApi;

public class PlaylistsFromProfileFetcher extends AbstractFetcher<SpotifyPlaylistObject> {
  public PlaylistsFromProfileFetcher(SpotifyApi authorizedApi) {
    super(authorizedApi);
  }

  @Override
  public Iterable<SpotifyPlaylistObject> fetch(String id) {
    var builder = this.spotifyApi.getListOfUsersPlaylists(id);

    return this.executeRequest(builder, playlist -> new SpotifyPlaylistObject(playlist.getId(), playlist.getName()));
  }
}
