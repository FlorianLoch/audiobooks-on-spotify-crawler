package ch.fdlo.hoerbuchspion;

import java.io.IOException;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import com.wrapper.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;

import org.apache.hc.core5.http.ParseException;

public class AuthorizedSpotifyAPIFactory {
    private final String clientId;
    private final String clientSecret;
    private String accessToken;

    public AuthorizedSpotifyAPIFactory(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public synchronized SpotifyApi createInstance() throws ParseException, SpotifyWebApiException, IOException {
        final SpotifyApi spotifyApi = new SpotifyApi.Builder().setClientId(this.clientId)
                .setClientSecret(this.clientSecret).build();

        if (this.accessToken.isEmpty()) {
            final ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials().build();
            final ClientCredentials clientCredentials = clientCredentialsRequest.execute();

            this.accessToken = clientCredentials.getAccessToken();

            // TODO: replace this with call to proper logger
            System.out.println("Expires in: " + clientCredentials.getExpiresIn());
        }

        spotifyApi.setAccessToken(this.accessToken);

        return spotifyApi;
    }
}
