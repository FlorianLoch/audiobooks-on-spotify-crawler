package ch.fdlo.hoerbuchspion.crawler.fetcher;

import ch.fdlo.hoerbuchspion.crawler.types.SpotifyAlbumObject;
import ch.fdlo.hoerbuchspion.crawler.types.SpotifyObject;
import com.neovisionaries.i18n.CountryCode;
import com.wrapper.spotify.SpotifyApi;

public class AlbumsFromArtistFetcher extends AbstractFetcher<SpotifyAlbumObject> {
    public AlbumsFromArtistFetcher(SpotifyApi authorizedApi) {
        super(authorizedApi);
    }

    @Override
    public Iterable<SpotifyAlbumObject> fetch(SpotifyObject target) {
        var builder = this.spotifyApi.getArtistsAlbums(target.getId());

        builder.market(CountryCode.DE);

        return this.executeRequest(builder, album -> new SpotifyAlbumObject(album.getId(), album.getName(), target));
    }
}
