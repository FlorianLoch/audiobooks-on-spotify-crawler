package ch.fdlo.hoerbuchspion.crawler;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;

import org.apache.hc.core5.http.ParseException;

import ch.fdlo.hoerbuchspion.AuthorizedSpotifyAPIFactory;
import ch.fdlo.hoerbuchspion.crawler.types.Album;
import ch.fdlo.hoerbuchspion.crawler.types.Artist;
import ch.fdlo.hoerbuchspion.crawler.types.Playlist;

public class Crawler {
  private SpotifyApi api;
  // TODO: Check which implemention of Set makes the most sense
  private Set<String> categories = new HashSet<>();
  private Set<String> profiles = new HashSet<>();
  private Set<Playlist> playlists = new HashSet<>();
  private Set<Artist> artists = new HashSet<>();
  private Set<Album> albums = new HashSet<>();

  public Crawler(AuthorizedSpotifyAPIFactory apiFactory) throws ParseException, SpotifyWebApiException, IOException {
    this.api = apiFactory.createInstance();
  }

  public void addCategory(String category) {
    this.categories.add(category);
  }

  public void addProfile(String profile) {
    this.profiles.add(profile);
  }

  public void addPlaylist(String playlistId, String playlistName) {
    this.playlists.add(new Playlist(playlistId, playlistName));
  }

  public void addArtist(String artistId, String artistName) {
    this.artists.add(new Artist(artistId, artistName));
  }

  public void crawl() throws ParseException, SpotifyWebApiException, IOException {
    this.collectPlaylists();
    System.out.println("Found " + this.playlists.size() + " playlists.");

    this.collectArtists();
    System.out.println("Found " + this.artists.size() + " artists.");

    this.collectAlbums();
    System.out.println("Found " + this.albums.size() + " albums.");

    for (Album album : this.albums) {
      System.out.println(album);
    }
  }

  private void collectPlaylists() throws ParseException, SpotifyWebApiException, IOException {
    for (String category : this.categories) {
      var playlistsFromCategoryFetcher = new PlaylistsFromCategoryFetcher(this.api, category);
      for (Playlist playlist : playlistsFromCategoryFetcher.fetch()) {
          this.playlists.add(playlist);
      }
    }

    for (String profile : this.categories) {
      var playlistsFromCategoryFetcher = new PlaylistsFromProfilesFetcher(this.api, profile);
      for (Playlist playlist : playlistsFromCategoryFetcher.fetch()) {
          this.playlists.add(playlist);
      }
    }
  }

  private void collectArtists() throws ParseException, SpotifyWebApiException, IOException {
    for (Playlist playlist : this.playlists) {
      var artistsFromPlaylistsFetcher = new ArtistsFromPlaylistsFetcher(this.api, playlist.getId());
      for (Artist artist : artistsFromPlaylistsFetcher.fetch()) {
          this.artists.add(artist);
      }
    }
  }

  private void collectAlbums() throws ParseException, SpotifyWebApiException, IOException {
    for (Artist artist : this.artists) {
      var albumsFromArtistsFetcher = new AlbumsFromArtistsFetcher(this.api, artist.getId());
      for (Album album : albumsFromArtistsFetcher.fetch()) {
          this.albums.add(album);
      }
    }
  }
}