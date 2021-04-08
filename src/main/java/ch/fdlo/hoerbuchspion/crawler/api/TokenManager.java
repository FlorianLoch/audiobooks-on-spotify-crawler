package ch.fdlo.hoerbuchspion.crawler.api;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import com.wrapper.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import org.apache.hc.core5.http.ParseException;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class TokenManager {
    public static final int EXPIRES_IN_THRESHOLD = 5;

    private final SpotifyApi api;
    private final List<Credentials> credsList;
    private Iterator<Credentials> credsIterator;

    public TokenManager(SpotifyApi api, String clientId, String clientSecret) {
        this.api = api;

        this.credsList = parseCredentials(clientId, clientSecret);

        System.out.println("Going to use up to " + this.credsList.size() + " credentials.");

        this.credsIterator = this.credsList.iterator();
    }

    private LinkedList<Credentials> parseCredentials(String clientId, String clientSecret) {
        var clientIdSplits = clientId.split(",");
        var clientSecretSplits = clientSecret.split(",");

        if (clientIdSplits.length != clientSecretSplits.length) {
            throw new IllegalArgumentException("clientId and clientSecret have to have the same amount of segments split by ','.");
        }

        var list = new LinkedList<Credentials>();

        for (int i = 0; i < clientIdSplits.length; i++) {
            list.add(new Credentials(clientIdSplits[i], clientSecretSplits[i]));
        }

        return list;
    }

    public synchronized Credentials getCredentials() {
        // If we reached the end of the list jump back to the beginning
        if (!this.credsIterator.hasNext()) {
            this.credsIterator = this.credsList.iterator();
        }

        return this.credsIterator.next();
    }

    class Credentials {
        private final String clientId;
        private final String clientSecret;
        private ClientCredentials spotClientCredentials;

        public Credentials(String clientId, String clientSecret) {
            this.clientId = clientId;
            this.clientSecret = clientSecret;
        }

        public synchronized String getAccessToken() throws ParseException, SpotifyWebApiException, IOException {
            if (this.spotClientCredentials == null || this.spotClientCredentials.getExpiresIn() < EXPIRES_IN_THRESHOLD) {
                if (this.spotClientCredentials != null) {
                    System.out.println("Token expires in " + this.spotClientCredentials.getExpiresIn() + " s. Refreshing it.-");
                }
                this.refreshCredentials();
            }

            return this.spotClientCredentials.getAccessToken();
        }

        private void refreshCredentials() throws ParseException, SpotifyWebApiException, IOException {
            this.spotClientCredentials = new ClientCredentialsRequest.Builder(this.clientId, this.clientSecret)
                    .setDefaults(api.getHttpManager(), api.getScheme(), api.getHost(), api.getPort())
                    .grant_type("client_credentials")
                    .build()
                    .execute();
        }
    }
}
