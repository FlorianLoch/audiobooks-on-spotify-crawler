package ch.fdlo.hoerbuchspion.crawler;

import java.io.IOException;

import com.neovisionaries.i18n.CountryCode;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.TrackSimplified;

import org.apache.hc.core5.http.ParseException;

import ch.fdlo.hoerbuchspion.crawler.types.Track;

public class TracksFromAlbumFetcher extends AbstractFetcher<Track> {
  private final String albumId;

  public TracksFromAlbumFetcher(SpotifyApi authorizedApi, String albumId) {
    super(authorizedApi);

    this.albumId = albumId;
  }

  @Override
  public Iterable<Track> fetch() throws ParseException, SpotifyWebApiException, IOException {
    var builder = this.spotifyApi.getAlbumsTracks(this.albumId);

    builder.market(CountryCode.DE);

    return this.executeRequest(builder, (TrackSimplified track) -> {
      return new Track(track.getDurationMs(), track.getIsExplicit(), track.getIsPlayable(), track.getPreviewUrl());
    });
  }

}
