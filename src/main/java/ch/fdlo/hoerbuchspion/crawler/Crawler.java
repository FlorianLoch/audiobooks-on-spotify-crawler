package ch.fdlo.hoerbuchspion.crawler;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import ch.fdlo.hoerbuchspion.crawler.fetcher.*;
import ch.fdlo.hoerbuchspion.crawler.types.*;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;

import org.apache.hc.core5.http.ParseException;

import ch.fdlo.hoerbuchspion.crawler.api.AuthorizedSpotifyAPI;

public class Crawler {
    private SpotifyApi api;
    private Set<SpotifyCategoryObject> categories = new HashSet<>();
    private Set<SpotifyProfileObject> profiles = new HashSet<>();
    private Set<SpotifyPlaylistObject> playlists = Collections.synchronizedSet(new HashSet<>());
    private Set<SpotifyArtistObject> artists = Collections.synchronizedSet(new HashSet<>());
    private Set<SpotifyAlbumObject> albums = Collections.synchronizedSet(new HashSet<>());

    public Crawler(AuthorizedSpotifyAPI apiFactory) throws ParseException, SpotifyWebApiException, IOException {
        this.api = apiFactory.getInstance();
    }

    public void addArtist(SpotifyArtistObject artist) {
        this.artists.add(artist);
    }

    public void addCategory(SpotifyCategoryObject category) {
        this.categories.add(category);
    }

    public void addPlaylist(SpotifyPlaylistObject playlist) {
        this.playlists.add(playlist);
    }

    public void addProfile(SpotifyProfileObject profile) {
        this.profiles.add(profile);
    }

    public Set<SpotifyAlbumObject> crawlAlbums() throws ParseException, SpotifyWebApiException, IOException {
        this.collectPlaylists();
        // TODO: Move this to App.java
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
        var fetcher = new ArtistsFromPlaylistFetcher(this.api);

        this.playlists.parallelStream().forEach(item -> {
            // TODO: Handle runtime exceptions
            fetcher.fetch(item.getId()).forEach(this.artists::addAll);
        });
    }

    private void collectAlbums() {
        this.collect(this.artists, this.albums, AlbumsFromArtistFetcher.class);
    }

    private <R extends SpotifyObject, T extends AbstractFetcher<R>> void collect(Set<? extends SpotifyObject> source, Set<R> target, Class<T> fetcherClass) {
        T fetcher;

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
