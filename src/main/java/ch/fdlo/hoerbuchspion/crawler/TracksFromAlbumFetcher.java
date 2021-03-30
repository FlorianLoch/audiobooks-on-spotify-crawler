package ch.fdlo.hoerbuchspion.crawler;

import com.neovisionaries.i18n.CountryCode;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.model_objects.specification.TrackSimplified;

import ch.fdlo.hoerbuchspion.crawler.types.Track;

public class TracksFromAlbumFetcher extends AbstractFetcher<Track> {
  public TracksFromAlbumFetcher(SpotifyApi authorizedApi) {
    super(authorizedApi);
  }

  @Override
  public Iterable<Track> fetch(String id) {
    var builder = this.spotifyApi.getAlbumsTracks(id);

    builder.market(CountryCode.DE);

    return this.executeRequest(builder, (TrackSimplified track) -> {
      return new Track(track.getDurationMs(), track.getIsExplicit(), track.getIsPlayable(), track.getPreviewUrl());
    });
  }
}
