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
        if (!this.artists.add(artist)) {
            System.out.println("Artist already considered.");
        }
    }

    public void addCategory(SpotifyCategoryObject category) {
        this.categories.add(category);
    }

    public void addPlaylist(SpotifyPlaylistObject playlist) {
        if (!this.playlists.add(playlist)) {
            System.out.println("Playlist already considered.");
        }
    }

    public void addProfile(SpotifyProfileObject profile) {
        this.profiles.add(profile);
    }

    public Set<SpotifyPlaylistObject> collectPlaylists() {
        this.collect(this.categories, this.playlists, PlaylistsFromCategoryFetcher.class);

        this.collect(this.profiles, this.playlists, PlaylistsFromProfileFetcher.class);

        return Collections.unmodifiableSet(this.playlists);
    }

    public Set<SpotifyPlaylistObject> collectArtists() {
        var fetcher = new ArtistsFromPlaylistFetcher(this.api);

        this.playlists.parallelStream().forEach(item -> {
            try {
                fetcher.fetch(item).forEach(this.artists::addAll);
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        });

        return Collections.unmodifiableSet(this.playlists);
    }

    public Set<SpotifyAlbumObject> collectAlbums() {
        this.collect(this.artists, this.albums, AlbumsFromArtistFetcher.class);

        return Collections.unmodifiableSet(this.albums);
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
            try {
                fetcher.fetch(item).forEach(target::add);
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        });
    }
}
