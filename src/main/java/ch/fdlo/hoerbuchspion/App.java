package ch.fdlo.hoerbuchspion;

import java.io.IOException;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;

import org.apache.hc.core5.http.ParseException;

import ch.fdlo.hoerbuchspion.crawler.AlbumsFromArtistsFetcher;
import ch.fdlo.hoerbuchspion.crawler.ArtistsFromPlaylistsFetcher;
import ch.fdlo.hoerbuchspion.crawler.PlaylistsFromProfilesFetcher;

public class App {
    public static final String ENV_CLIENT_ID = "HOERBUCHSPION_SPOTIFY_CLIENTID";
    public static final String ENV_CLIENT_SECRET = "HOERBUCHSPION_SPOTIFY_CLIENTSECRET";

    public static void main(String[] args) {
        final String clientId = System.getenv(ENV_CLIENT_ID);
        final String clientSecret = System.getenv(ENV_CLIENT_SECRET);

        if (clientId == null || clientId.isEmpty() || clientSecret == null || clientSecret.isEmpty()) {
            System.out.println("Credentials for Spotify API not found. Please make sure '" + ENV_CLIENT_ID + "' and '"
                    + ENV_CLIENT_SECRET + "' are set.");
            System.exit(1);
        }

        AuthorizedSpotifyAPIFactory apiFactory = new AuthorizedSpotifyAPIFactory(clientId, clientSecret);

        try {
            SpotifyApi authorizedApi = apiFactory.createInstance();

            // var playlistsFromProfilesFetcher = new PlaylistsFromProfilesFetcher(authorizedApi, "digsterdeutschland");
            // for (String playlist : playlistsFromProfilesFetcher.fetch()) {
            //     System.out.println(playlist);
            // }

            // var artistsFromPlaylistsFetcher = new ArtistsFromPlaylistsFetcher(authorizedApi, "0kdso1W3E726FkzZ2fCY8K");
            // for (String trackArtists : artistsFromPlaylistsFetcher.fetch()) {
            //      System.out.println(trackArtists);
            // }

            var albumsFromArtistsFetcher = new AlbumsFromArtistsFetcher(authorizedApi, "0I5CMdNszqP3qJTmhGxlsA");
            for (String albumName : albumsFromArtistsFetcher.fetch()) {
                 System.out.println(albumName);
            }

            System.out.println("Total count of requests performed: " + CountingSpotifyHttpManager.getCount());
        } catch (ParseException | SpotifyWebApiException | IOException e) {
            // TODO: Auto-generated catch block
            e.printStackTrace();
        }
    }
}
