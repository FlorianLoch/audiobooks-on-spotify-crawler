package ch.fdlo.hoerbuchspion;

import java.io.IOException;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;

import org.apache.hc.core5.http.ParseException;

public class App {
    public static final String ENV_CLIENT_ID = "HOERBUCHSPION_SPOTIFY_CLIENTID";
    public static final String ENV_CLIENT_SECRET = "HOERBUCHSPION_SPOTIFY_CLIENTSECRET";

    public static void main(String[] args) {
        final String clientId = System.getenv(ENV_CLIENT_ID);
        final String clientSecret = System.getenv(ENV_CLIENT_SECRET);

        if (clientId.isEmpty() || clientSecret.isEmpty()) {
            System.out.println("Credentials for Spotify API not found. Please make sure '" + ENV_CLIENT_ID + "' and '"
                    + ENV_CLIENT_SECRET + "' are set.");
        }

        AuthorizedSpotifyAPIFactory apiFactory = new AuthorizedSpotifyAPIFactory(clientId, clientSecret);

        try {
            SpotifyApi authorizedApi = apiFactory.createInstance();

            // TODO: Do something with the API...
        } catch (ParseException | SpotifyWebApiException | IOException e) {
            // TODO: Auto-generated catch block
            e.printStackTrace();
        }
    }
}
