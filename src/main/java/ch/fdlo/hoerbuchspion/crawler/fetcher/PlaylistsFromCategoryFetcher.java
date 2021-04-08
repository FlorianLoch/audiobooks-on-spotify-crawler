package ch.fdlo.hoerbuchspion.crawler.fetcher;

import ch.fdlo.hoerbuchspion.crawler.types.SpotifyPlaylistObject;
import com.neovisionaries.i18n.CountryCode;
import com.wrapper.spotify.SpotifyApi;

public class PlaylistsFromCategoryFetcher extends AbstractFetcher<SpotifyPlaylistObject> {
    public PlaylistsFromCategoryFetcher(SpotifyApi authorizedApi) {
        super(authorizedApi);
    }

    @Override
    public Iterable<SpotifyPlaylistObject> fetch(String id) {
        var builder = this.spotifyApi.getCategorysPlaylists(id);

        builder.country(CountryCode.DE);

        return this.executeRequest(builder, playlist -> new SpotifyPlaylistObject(playlist.getId(), playlist.getName()));
    }
}
