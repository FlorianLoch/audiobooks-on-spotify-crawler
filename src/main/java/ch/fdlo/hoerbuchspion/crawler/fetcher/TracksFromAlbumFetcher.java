package ch.fdlo.hoerbuchspion.crawler.fetcher;

import com.neovisionaries.i18n.CountryCode;
import com.wrapper.spotify.SpotifyApi;

import ch.fdlo.hoerbuchspion.crawler.types.Track;

public class TracksFromAlbumFetcher extends AbstractFetcher<Track> {
    public TracksFromAlbumFetcher(SpotifyApi authorizedApi) {
        super(authorizedApi);
    }

    @Override
    public Iterable<Track> fetch(String id) {
        var builder = this.spotifyApi.getAlbumsTracks(id);

        builder.market(CountryCode.DE);

        return this.executeRequest(builder, track -> new Track(track.getDurationMs(), track.getIsExplicit(), track.getIsPlayable(), track.getPreviewUrl()));
    }
}
