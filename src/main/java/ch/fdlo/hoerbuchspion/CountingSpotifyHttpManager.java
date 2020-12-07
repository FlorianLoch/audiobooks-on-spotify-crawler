package ch.fdlo.hoerbuchspion;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.atomic.AtomicInteger;

import com.wrapper.spotify.IHttpManager;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;

import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;

class CountingSpotifyHttpManager implements IHttpManager {
    private static AtomicInteger requestCounter = new AtomicInteger();
    private IHttpManager wrappedManager;

    public CountingSpotifyHttpManager(IHttpManager httpManager) {
        this.wrappedManager = httpManager;
    }

    @Override
    public String get(URI uri, Header[] headers) throws IOException, SpotifyWebApiException, ParseException {
        requestCounter.incrementAndGet();

        System.out.println("Going to GET: " + uri);

        return this.wrappedManager.get(uri, headers);
    }

    @Override
    public String post(URI uri, Header[] headers, HttpEntity body)
            throws IOException, SpotifyWebApiException, ParseException {
        requestCounter.incrementAndGet();

        System.out.println("Going to POST: " + uri);

        return this.wrappedManager.post(uri, headers, body);
    }

    @Override
    public String put(URI uri, Header[] headers, HttpEntity body)
            throws IOException, SpotifyWebApiException, ParseException {
        requestCounter.incrementAndGet();

        System.out.println("Going to PUT: " + uri);

        return this.wrappedManager.put(uri, headers, body);
    }

    @Override
    public String delete(URI uri, Header[] headers, HttpEntity body)
            throws IOException, SpotifyWebApiException, ParseException {
        requestCounter.incrementAndGet();

        System.out.println("Going to DELETE: " + uri);

        return this.wrappedManager.delete(uri, headers, body);
    }

    public static int getCount() {
      return requestCounter.get();
    }
}