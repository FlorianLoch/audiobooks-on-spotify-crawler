package ch.fdlo.hoerbuchspion.crawler;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import ch.fdlo.hoerbuchspion.crawler.types.SpotifyObject;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;

import org.apache.hc.core5.http.ParseException;

import ch.fdlo.hoerbuchspion.AuthorizedSpotifyAPIFactory;
import ch.fdlo.hoerbuchspion.crawler.types.Album;
import ch.fdlo.hoerbuchspion.crawler.types.Artist;

public class Crawler {
  private SpotifyApi api;
  private Set<SpotifyObject> categories = new HashSet<>();
  private Set<SpotifyObject> profiles = new HashSet<>();
  private Set<SpotifyObject> playlists = Collections.synchronizedSet(new HashSet<>());
  private Set<SpotifyObject> artists = Collections.synchronizedSet(new HashSet<>());
  private Set<SpotifyObject> albums = Collections.synchronizedSet(new HashSet<>());

  public Crawler(AuthorizedSpotifyAPIFactory apiFactory) throws ParseException, SpotifyWebApiException, IOException {
    this.api = apiFactory.createInstance();
  }

  public void addArtist(String id, String name) {
    this.artists.add(SpotifyObject.artist(id, name));
  }

  public void addCategory(String id, String name) {
    this.categories.add(SpotifyObject.category(id, name));
  }

  public void addPlaylist(String id, String name) {
    this.playlists.add(SpotifyObject.playlist(id, name));
  }

  public void addProfile(String id, String name) {
    this.profiles.add(SpotifyObject.profile(id, name));
  }

  public Set<SpotifyObject> crawlAlbums() throws ParseException, SpotifyWebApiException, IOException {
    this.collectPlaylists();
    System.out.println("Found " + this.playlists.size() + " playlists.");

    this.collectArtists();
    System.out.println("Found " + this.artists.size() + " artists.");

    this.collectAlbums();
    System.out.println("Found " + this.albums.size() + " albums.");

    return Collections.unmodifiableSet(this.albums);
  }

  private void collectPlaylists() {
    this.collect(this.categories, this.playlists, PlaylistsFromCategoryFetcher.class);

    this.collect(this.profiles, this.playlists, PlaylistsFromProfileFetcher.class);
  }

  private void collectArtists() {
    this.collect(this.playlists, this.artists, ArtistsFromPlaylistFetcher.class);
  }

  private void collectAlbums() {
    this.collect(this.artists, this.albums, AlbumsFromArtistFetcher.class);
  }

  private <T extends AbstractFetcher<SpotifyObject>> void collect(Set<SpotifyObject> source, Set<SpotifyObject> target, Class<T> fetcherClass) {
    AbstractFetcher<SpotifyObject> fetcher;

    try {
      fetcher = fetcherClass.getDeclaredConstructor(SpotifyApi.class).newInstance(this.api);
    } catch (Exception e) {
      // This should never happen, though
      throw new Error(e);
    }

    source.parallelStream().forEach(item -> {
      // TODO: Handle runtime exceptions
      fetcher.fetch(item.getId()).forEach(target::add);
    });
  }
}
