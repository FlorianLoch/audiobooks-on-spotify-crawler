package ch.fdlo.hoerbuchspion;

import java.io.IOException;
import java.util.HashSet;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;

import org.apache.hc.core5.http.ParseException;

import ch.fdlo.hoerbuchspion.crawler.Augmenter;
import ch.fdlo.hoerbuchspion.crawler.Crawler;
import ch.fdlo.hoerbuchspion.crawler.LanguageDetector;
import ch.fdlo.hoerbuchspion.crawler.db.AlbumDAO;
import ch.fdlo.hoerbuchspion.crawler.types.Album;
import ch.fdlo.hoerbuchspion.crawler.types.Artist;

public class App {
    public static final String ENV_CLIENT_ID = "HOERBUCHSPION_SPOTIFY_CLIENTID";
    public static final String ENV_CLIENT_SECRET = "HOERBUCHSPION_SPOTIFY_CLIENTSECRET";

    public static void main(String[] args) {
        final String clientId = System.getenv(ENV_CLIENT_ID);
        final String clientSecret = System.getenv(ENV_CLIENT_SECRET);

        // LanguageDetector.detectUsingWordlists("Die Tore der Welt");

        // System.exit(1);

        if (clientId == null || clientId.isEmpty() || clientSecret == null || clientSecret.isEmpty()) {
            System.out.println("Credentials for Spotify API not found. Please make sure '" + ENV_CLIENT_ID + "' and '"
                    + ENV_CLIENT_SECRET + "' are set.");
            System.exit(1);
        }

        AlbumDAO albumDAO = new AlbumDAO();

        AuthorizedSpotifyAPIFactory apiFactory = new AuthorizedSpotifyAPIFactory(clientId, clientSecret);

        try {
            var crawler = new Crawler(apiFactory);
            var augmenter = new Augmenter(apiFactory);
            // crawler.addCategory("audiobooks");
            // crawler.addProfile("argonhörbücher");
            // crawler.addArtist("2YlvvdXUqRjiXmeL2GRuZ9", "Sherlock Holmes");
            crawler.addArtist("0I5CMdNszqP3qJTmhGxlsA", "Ken Follett");

            var albums = crawler.crawlAlbums();
            var prunedAlbums = new HashSet<Album>();
            // TODO: rename this and explain why we do this
            var prunedArtists = new HashSet<Artist>();

            for (var album : albums) {
                // if (!albumDAO.recordExists(album.getId())) {
                    prunedAlbums.add(album);
                    prunedArtists.add(album.getArtist());
                // }
            }

            augmenter.augmentAlbums(prunedAlbums);
            augmenter.augmentArtists(prunedArtists);

            albumDAO.upsert(prunedAlbums);

            System.out.println("Total amount of requests performed: " + CountingSpotifyHttpManager.getCount());
        } catch (ParseException | SpotifyWebApiException | IOException e) {
            // TODO: Auto-generated catch block
            e.printStackTrace();
        }
    }
}
