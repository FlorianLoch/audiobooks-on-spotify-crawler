package ch.fdlo.hoerbuchspion.crawler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import ch.fdlo.hoerbuchspion.crawler.api.AuthorizedSpotifyAPI;
import ch.fdlo.hoerbuchspion.crawler.api.RequestCountingHttpManager;
import ch.fdlo.hoerbuchspion.crawler.config.Config;
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

        System.out.println("Utilizing " + (ForkJoinPool.getCommonPoolParallelism() + 1) + " threads.");

        var entityManager = DBHelper.getEntityManagerInstance(verboseLogging);
        var albumDAO = new AlbumDAO(entityManager);
        var crawlStatsKVDAO = new CrawlStatsKVDAO(entityManager);

        System.out.println("Connected to DB.");

        Instant start = Instant.now();

        if (args.length == 1 && args[0].equals("--clean-run")) {
            albumDAO.truncateTables();
            crawlStatsKVDAO.truncateTable();
            System.out.println("Successfully truncated tables.");
        }

        AuthorizedSpotifyAPI apiFactory = new AuthorizedSpotifyAPI(clientId, clientSecret);

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
//            crawler.addArtist(new SpotifyArtistObject("0I5CMdNszqP3qJTmhGxlsA", "Ken Follett"));

            var configFilePath = Paths.get(System.getProperty("user.dir"), "config.yaml");
            var config = Config.LoadConfig(Files.newInputStream(configFilePath));
            config.profiles.forEach(crawler::addProfile);
            config.categories.forEach(crawler::addCategory);
            config.playlists.forEach(crawler::addPlaylist);
            config.artists.forEach(crawler::addArtist);

            var albumsProcessedCounter = new AtomicInteger();

            var simpleAlbums = crawler.crawlAlbums();
            var fullAlbums = simpleAlbums.parallelStream().
                    filter(simpleAlbum -> !albumDAO.recordExists(simpleAlbum)).
                    map(album -> {
                        System.out.println(albumsProcessedCounter.incrementAndGet() + "/" + simpleAlbums.size());
                        return augmenter.inflateAlbum(album);
                    }).
                    collect(Collectors.toList());

            Artist.getAllArtists().parallelStream().forEach(augmenter::augmentArtist);

            albumDAO.upsert(fullAlbums);

            long timeElapsed = Duration.between(start, Instant.now()).toMillis();

            // TODO: replace dummy values
            var stats = new HashSet<CrawlStatsKV>();
            stats.add(new CrawlStatsKV(KVKey.PLAYLISTS_CONSIDERED_COUNT, 0));
            stats.add(new CrawlStatsKV(KVKey.PROFILES_CONSIDERED_COUNT, 0));
            stats.add(new CrawlStatsKV(KVKey.ARTISTS_CONSIDERED_COUNT, 1));
            stats.add(new CrawlStatsKV(KVKey.ALBUMS_FOUND_COUNT, simpleAlbums.size()));
            stats.add(new CrawlStatsKV(KVKey.ALBUM_DETAILS_FETCHED_COUNT, fullAlbums.size()));
            stats.add(new CrawlStatsKV(KVKey.ARTIST_DETAILS_FETCHED_COUNT, Artist.getAllArtists().size()));
            stats.add(new CrawlStatsKV(KVKey.DURATION_LAST_RUN_MS, timeElapsed));
            stats.add(new CrawlStatsKV(KVKey.TOTAL_API_REQUESTS_COUNT, RequestCountingHttpManager.getCount()));
            stats.add(new CrawlStatsKV(KVKey.LAST_RUN_PERFOMED_AT, Instant.now().toEpochMilli()));

            crawlStatsKVDAO.upsert(stats);

            var cachedAlbumsCount = simpleAlbums.size() - fullAlbums.size();
            System.out.println("Total amount of requests performed: " + RequestCountingHttpManager.getCount());
            System.out.println(cachedAlbumsCount + " albums were in the DB already. " + fullAlbums.size()
                    + " had to be looked up.");
            System.out.println(Artist.getAllArtists().size()
                    + " artist details had to be (re)fetched - because they are associated with one album not present in the DB so far.");

        } catch (ParseException | SpotifyWebApiException | IOException e) {
            // TODO: Auto-generated catch block
            e.printStackTrace();
        }
    }
}
