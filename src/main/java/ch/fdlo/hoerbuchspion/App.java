package ch.fdlo.hoerbuchspion;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;

import org.apache.hc.core5.http.ParseException;

import ch.fdlo.hoerbuchspion.crawler.Augmenter;
import ch.fdlo.hoerbuchspion.crawler.Crawler;
import ch.fdlo.hoerbuchspion.crawler.db.AlbumDAO;
import ch.fdlo.hoerbuchspion.crawler.db.CrawlStatsKVDAO;
import ch.fdlo.hoerbuchspion.crawler.db.DBHelper;
import ch.fdlo.hoerbuchspion.crawler.languageDetector.CombiningLanguageDetector;
import ch.fdlo.hoerbuchspion.crawler.languageDetector.OptimaizeLanguageDetector;
import ch.fdlo.hoerbuchspion.crawler.languageDetector.WordlistLanguageDetector;
import ch.fdlo.hoerbuchspion.crawler.types.*;
import ch.fdlo.hoerbuchspion.crawler.types.CrawlStatsKV.KVKey;

public class App {
    public static final String ENV_CLIENT_ID = "HOERBUCHSPION_SPOTIFY_CLIENTID";
    public static final String ENV_CLIENT_SECRET = "HOERBUCHSPION_SPOTIFY_CLIENTSECRET";
    public static final String ENV_VERBOSE_LOGGING = "HOERBUCHSPION_VERBOSE_LOGGING";

    public static void main(String[] args) {
        final String clientId = System.getenv(ENV_CLIENT_ID);
        final String clientSecret = System.getenv(ENV_CLIENT_SECRET);
        final boolean verboseLogging = Boolean.parseBoolean(System.getenv(ENV_VERBOSE_LOGGING)); // accepts only "true"
                                                                                                 // as true, case does
                                                                                                 // not matter

        if (clientId == null || clientId.isEmpty() || clientSecret == null || clientSecret.isEmpty()) {
            System.out.println("Credentials for Spotify API not found. Please make sure '" + ENV_CLIENT_ID + "' and '"
                    + ENV_CLIENT_SECRET + "' are set.");
            System.exit(1);
        }

        Instant start = Instant.now();

        var entityManager = DBHelper.getEntityManagerInstance(verboseLogging);
        var albumDAO = new AlbumDAO(entityManager);
        var crawlStatsKVDAO = new CrawlStatsKVDAO(entityManager);

        System.out.println("Connected to DB.");

        AuthorizedSpotifyAPIFactory apiFactory = new AuthorizedSpotifyAPIFactory(clientId, clientSecret);

        try {
            var optimaizeLanguageDetector = new OptimaizeLanguageDetector();
            var wordlistLanguageDetector = new WordlistLanguageDetector();
            wordlistLanguageDetector.tryToAddDebianWordlists();
            var combinedLanguageDetector = new CombiningLanguageDetector(optimaizeLanguageDetector,
                    wordlistLanguageDetector);

            var crawler = new Crawler(apiFactory);
            var augmenter = new Augmenter(apiFactory, combinedLanguageDetector);

            // crawler.addCategory("audiobooks");
            // crawler.addProfile("argonhörbücher");
            // crawler.addArtist("2YlvvdXUqRjiXmeL2GRuZ9", "Sherlock Holmes");
            crawler.addArtist("0I5CMdNszqP3qJTmhGxlsA", "Ken Follett");

            var albums = crawler.crawlAlbums();
            var prunedAlbums = new HashSet<Album>();
            // TODO: rename this and explain why we do this
            var prunedArtists = new HashSet<Artist>();

            for (var album : albums) {
                if (!albumDAO.recordExists(album)) {
                    prunedAlbums.add(album);
                    prunedArtists.add(album.getArtist());
                }
            }

            augmenter.augmentAlbums(prunedAlbums);
            augmenter.augmentArtists(prunedArtists);

            albumDAO.upsert(prunedAlbums);

            long timeElapsed = Duration.between(start, Instant.now()).toMillis();

            // TODO: replace dummy values
            var stats = new HashSet<CrawlStatsKV>();
            stats.add(new CrawlStatsKV(KVKey.PLAYLISTS_CONSIDERED_COUNT, 0));
            stats.add(new CrawlStatsKV(KVKey.PROFILES_CONSIDERED_COUNT, 0));
            stats.add(new CrawlStatsKV(KVKey.ARTISTS_CONSIDERED_COUNT, 1));
            stats.add(new CrawlStatsKV(KVKey.ALBUMS_FOUND_COUNT, albums.size()));
            stats.add(new CrawlStatsKV(KVKey.ALBUM_DETAILS_FETCHED_COUNT, prunedAlbums.size()));
            stats.add(new CrawlStatsKV(KVKey.ARTIST_DETAILS_FETCHED_COUNT, prunedArtists.size()));
            stats.add(new CrawlStatsKV(KVKey.DURATION_LAST_RUN_MS, timeElapsed));
            stats.add(new CrawlStatsKV(KVKey.TOTAL_API_REQUESTS_COUNT, CountingSpotifyHttpManager.getCount()));
            stats.add(new CrawlStatsKV(KVKey.LAST_RUN_PERFOMED_AT, Instant.now().toEpochMilli()));

            crawlStatsKVDAO.upsert(stats);

            var cachedAlbumsCount = albums.size() - prunedAlbums.size();
            System.out.println("Total amount of requests performed: " + CountingSpotifyHttpManager.getCount());
            System.out.println(cachedAlbumsCount + " albums were in the DB already. " + prunedAlbums.size()
                    + " had to be looked up.");
            System.out.println(prunedArtists.size()
                    + " artist details had to be (re)fetched - because they are associated with one album not present in the DB so far.");

        } catch (ParseException | SpotifyWebApiException | IOException e) {
            // TODO: Auto-generated catch block
            e.printStackTrace();
        }
    }
}
