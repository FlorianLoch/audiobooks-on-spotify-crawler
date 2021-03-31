package ch.fdlo.hoerbuchspion;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import com.wrapper.spotify.IHttpManager;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;

import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.message.BasicHeader;

import static java.lang.Thread.currentThread;

class RequestCountingHttpManager extends AbstractDecoratingHttpManager {
    private static final AtomicInteger requestCounter = new AtomicInteger();

    public RequestCountingHttpManager(IHttpManager httpManager) {
        super(httpManager);
    }

    @Override
    String handleImpl(VerbHandler wrappedVerbHandler, HttpVerb verb, URI uri, Header[] headers, HttpEntity body)
            throws IOException, SpotifyWebApiException, ParseException {
        requestCounter.incrementAndGet();

        System.out.println("[" + currentThread().getId() + "] " + verb + ": " + uri);

        return wrappedVerbHandler.apply(uri, headers, body);
    }

    public static int getCount() {
        return requestCounter.get();
    }
}