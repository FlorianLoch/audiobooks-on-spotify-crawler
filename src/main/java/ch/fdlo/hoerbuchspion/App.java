package ch.fdlo.hoerbuchspion;

import java.io.IOException;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;

import org.apache.hc.core5.http.ParseException;

import ch.fdlo.hoerbuchspion.crawler.Crawler;
import ch.fdlo.hoerbuchspion.crawler.db.AlbumDAO;

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

        AlbumDAO albumDAO = new AlbumDAO();

        AuthorizedSpotifyAPIFactory apiFactory = new AuthorizedSpotifyAPIFactory(clientId, clientSecret);

        try {
            var crawler = new Crawler(apiFactory);

            //crawler.addCategory("audiobooks");
            crawler.addProfile("argonhörbücher");
            //crawler.addArtist("2YlvvdXUqRjiXmeL2GRuZ9", "Sherlock Holmes");

            var albums = crawler.crawlAlbums();

            albumDAO.persist(albums);

            System.out.println("Total amount of requests performed: " + CountingSpotifyHttpManager.getCount());
        } catch (ParseException | SpotifyWebApiException | IOException e) {
            // TODO: Auto-generated catch block
            e.printStackTrace();
        }
    }
}
