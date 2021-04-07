package ch.fdlo.hoerbuchspion.crawler;

import java.io.IOException;

import ch.fdlo.hoerbuchspion.crawler.types.*;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;

import org.apache.hc.core5.http.ParseException;

import ch.fdlo.hoerbuchspion.AuthorizedSpotifyAPI;
import ch.fdlo.hoerbuchspion.crawler.languageDetector.LanguageDetector;

import static java.lang.Thread.currentThread;

public class Augmenter {
  private final SpotifyApi api;
  private final LanguageDetector languageDetector;
  private final TracksFromAlbumFetcher tracksFromAlbumFetcher;

  public Augmenter(AuthorizedSpotifyAPI apiFactory, LanguageDetector languageDetector) throws ParseException, SpotifyWebApiException, IOException {
    this.api = apiFactory.getInstance();
    this.languageDetector = languageDetector;
    this.tracksFromAlbumFetcher = new TracksFromAlbumFetcher(this.api);
  }

  public Album inflateAlbum(SpotifyAlbumObject simpleAlbum) {
    try {
        // TODO: Improve error handling
        var spotifyAlbum = this.api.getAlbum(simpleAlbum.getId()).build().execute();

        var assumedLanguage = this.languageDetector.detectLanguage(simpleAlbum.getName());

        var fullAlbum = new Album(spotifyAlbum, assumedLanguage);

        tracksFromAlbumFetcher.fetch(simpleAlbum.getId()).forEach(fullAlbum::digestTrack);

        return fullAlbum;
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SpotifyWebApiException e) {
      e.printStackTrace();
    } catch (ParseException e) {
      e.printStackTrace();
    }

    return null;
  }

  public void augmentArtist(Artist artist) {
      try {
          var spotifyArtist = this.api.getArtist(artist.getId()).build().execute();

          artist.setName(spotifyArtist.getName());
          artist.setArtistImage(ImageURLs.from(spotifyArtist.getImages()));
          artist.setPopularity(spotifyArtist.getPopularity());
      } catch (IOException e) {
          e.printStackTrace();
      } catch (SpotifyWebApiException e) {
          e.printStackTrace();
      } catch (ParseException e) {
          e.printStackTrace();
      }
  }
}
