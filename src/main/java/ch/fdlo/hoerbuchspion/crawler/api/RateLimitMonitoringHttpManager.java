package ch.fdlo.hoerbuchspion.crawler.api;

import java.io.IOException;
import java.net.URI;

import com.wrapper.spotify.IHttpManager;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.exceptions.detailed.TooManyRequestsException;

import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;

import static java.lang.Thread.currentThread;

public class RateLimitMonitoringHttpManager extends AbstractDecoratingHttpManager {
    public static int DEFAULT_RETRY_AFTER = 2; // given in seconds

    public RateLimitMonitoringHttpManager(IHttpManager httpManager) {
        super(httpManager);
    }

    @Override
    String handleImpl(VerbHandler wrappedVerbHandler, HttpVerb verb,
                      URI uri, Header[] headers, HttpEntity body) throws IOException, SpotifyWebApiException, ParseException {
        try {
            return wrappedVerbHandler.apply(uri, headers, body);
        } catch (TooManyRequestsException e) {
            StringBuilder sb = new StringBuilder("[" + currentThread().getId() + "] Triggered the API rate limit: ");

            int retryAfter = e.getRetryAfter();
            if (retryAfter > 0) {
                sb.append("Going to wait for ").append(retryAfter).append(" seconds.");
            } else {
                retryAfter = DEFAULT_RETRY_AFTER;
                sb.append("No 'retry after' value given... Going to wait ").append(DEFAULT_RETRY_AFTER).append(" seconds.");
            }

            System.out.println(sb);

            try {
                Thread.sleep(retryAfter * 1000L);
            } catch (InterruptedException e1) {
                // guess there is not much we can do about it...
                e1.printStackTrace();
            }

            return wrappedVerbHandler.apply(uri, headers, body);
        }
    }

}
