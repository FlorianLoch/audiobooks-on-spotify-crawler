package ch.fdlo.hoerbuchspion.crawler.api;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;

public class AuthorizedSpotifyAPI {
    private final SpotifyApi instance;

    public AuthorizedSpotifyAPI(String clientId, String clientSecret) {
        final var httpManager = new SpotifyHttpManager.Builder().build();
        final var tokenInhectingHttpManager = new TokenInjectingHttpManager(httpManager);
        final var requestCountingHttpManager = new RequestCountingHttpManager(tokenInhectingHttpManager);
        final var rateLimitMonitoringHttpManager = new RateLimitMonitoringHttpManager(requestCountingHttpManager);

        final SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setHttpManager(rateLimitMonitoringHttpManager)
                .setClientId("No necessary, handled by TokenManager")
                .setClientSecret("No necessary, handled by TokenManager")
                .setAccessToken("Going to be set by TokenInjectingHttpManager")
                .build();

        var tokenManager = new TokenManager(spotifyApi, clientId, clientSecret);
        tokenInhectingHttpManager.setTokenManager(tokenManager);

        this.instance = spotifyApi;
    }

    public SpotifyApi getInstance() {
        return this.instance;
    }
}
