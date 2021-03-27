package ch.fdlo.hoerbuchspion.crawler;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Stream;

import ch.fdlo.hoerbuchspion.crawler.types.*;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.Copyright;

import org.apache.hc.core5.http.ParseException;

import ch.fdlo.hoerbuchspion.AuthorizedSpotifyAPIFactory;
import ch.fdlo.hoerbuchspion.crawler.languageDetector.LanguageDetector;

public class Augmenter {
  private SpotifyApi api;
  private LanguageDetector languageDetector;

  public Augmenter(AuthorizedSpotifyAPIFactory apiFactory, LanguageDetector languageDetector) throws ParseException, SpotifyWebApiException, IOException {
    this.api = apiFactory.createInstance();
    this.languageDetector = languageDetector;
  }

  public void augmentAlbums(Collection<Album> albums) throws ParseException, SpotifyWebApiException, IOException {
    for (Album album : albums) {
      var albumDetails = new AlbumDetails();

      var spotifyAlbum = this.api.getAlbum(album.getId()).build().execute();

      // TODO: looks horrible
      var copyright = String.join(", ", new Iterable<String>() {
        @Override
        public Iterator<String> iterator() {
          return Stream.of(spotifyAlbum.getCopyrights()).map((Copyright copyright) -> {
            return copyright.getText();
          }).iterator();
        }
      });
      albumDetails.setCopyright(copyright);

      albumDetails.setLabel(spotifyAlbum.getLabel());

      albumDetails.setPopularity(spotifyAlbum.getPopularity());

      albumDetails.setAssumedLanguage(this.languageDetector.detectLanguage(album.getName()));

      var tracksFromAlbumFetcher = new TracksFromAlbumFetcher(this.api, album.getId());
      for (Track track : tracksFromAlbumFetcher.fetch()) {
        albumDetails.processTrack(track);
      }

      album.setAlbumDetails(albumDetails);
    }
  }

  public void augmentArtists(Collection<Artist> artists) throws ParseException, SpotifyWebApiException, IOException {
    for (Artist artist : artists) {
      var artistDetails = new ArtistDetails();

      var spotifyArtist = this.api.getArtist(artist.getId()).build().execute();

      artistDetails.setArtistImage(ImageURLs.from(spotifyArtist.getImages()));
      artistDetails.setPopularity(spotifyArtist.getPopularity());

      artist.setArtistDetails(artistDetails);
    }
  }
}
