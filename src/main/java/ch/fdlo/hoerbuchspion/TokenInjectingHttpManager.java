package ch.fdlo.hoerbuchspion;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.atomic.AtomicInteger;

import com.wrapper.spotify.IHttpManager;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.message.BasicHeader;

class TokenInjectingHttpManager extends AbstractDecoratingHttpManager {
    private TokenManager tokenManager;

    public TokenInjectingHttpManager(IHttpManager httpManager) {
        super(httpManager);
    }

    public void setTokenManager(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @Override
    String handleImpl(VerbHandler wrappedVerbHandler, HttpVerb verb, URI uri, Header[] headers, HttpEntity body)
            throws IOException, SpotifyWebApiException, ParseException {
        // Ignore requests trying to fetch a token, otherwise we end up with an overflowing stack
        if (!uri.getPath().equals("/api/token")) {
            for (int i = 0; i < headers.length; i++) {
                // For all requests this header should be present.
                // If it is not, no authentication seems to be required.
                if (headers[i].getName().equals("Authorization")) {
                    headers[i] = new BasicHeader("Authorization", "Bearer " + this.tokenManager.getCredentials().getAccessToken());
                    break;
                }
            }
        }


        return wrappedVerbHandler.apply(uri, headers, body);
    }
}