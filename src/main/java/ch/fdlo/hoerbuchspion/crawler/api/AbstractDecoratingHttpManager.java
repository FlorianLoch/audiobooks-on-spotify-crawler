package ch.fdlo.hoerbuchspion.crawler.api;

import com.wrapper.spotify.IHttpManager;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractDecoratingHttpManager implements IHttpManager {
    private Map<HttpVerb, VerbHandler> wrappedHandlerFns = new HashMap<>();

    private IHttpManager wrappedManager;

    public AbstractDecoratingHttpManager(IHttpManager httpManager) {
        this.wrappedManager = httpManager;

        // We need to do this after the the wrappedManager has been set - otherwise the shortcut syntax below
        // causes NPEs
        this.wrappedHandlerFns.put(HttpVerb.GET, (URI uri, Header[] headers, HttpEntity body) -> {
            return this.wrappedManager.get(uri, headers);
        });
        this.wrappedHandlerFns.put(HttpVerb.POST, this.wrappedManager::post);
        this.wrappedHandlerFns.put(HttpVerb.PUT, this.wrappedManager::put);
        this.wrappedHandlerFns.put(HttpVerb.DELETE, this.wrappedManager::delete);
    }

    private String handle(HttpVerb verb, URI uri, Header[] headers, HttpEntity body) throws IOException, SpotifyWebApiException, ParseException {
        VerbHandler wrappedVerbHandler = this.wrappedHandlerFns.get(verb);

        return this.handleImpl(wrappedVerbHandler, verb, uri, headers, body);
    }

    abstract String handleImpl(VerbHandler wrappedVerbHandler, HttpVerb verb, URI uri, Header[] headers, HttpEntity body) throws IOException, SpotifyWebApiException, ParseException;

    @Override
    public String get(URI uri, Header[] headers) throws IOException, SpotifyWebApiException, ParseException {
        return this.handle(HttpVerb.GET, uri, headers, null);
    }

    @Override
    public String post(URI uri, Header[] headers, HttpEntity body)
            throws IOException, SpotifyWebApiException, ParseException {
        return this.handle(HttpVerb.POST, uri, headers, body);
    }

    @Override
    public String put(URI uri, Header[] headers, HttpEntity body)
            throws IOException, SpotifyWebApiException, ParseException {
        return this.handle(HttpVerb.PUT, uri, headers, body);
    }

    @Override
    public String delete(URI uri, Header[] headers, HttpEntity body)
            throws IOException, SpotifyWebApiException, ParseException {
        return this.handle(HttpVerb.DELETE, uri, headers, body);
    }

    public enum HttpVerb {
        GET("GET"), POST("POST"), PUT("PUT"), DELETE("PUT");

        private String name;

        private HttpVerb(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public interface VerbHandler {
        String apply(URI uri, Header[] headers, HttpEntity body) throws IOException, SpotifyWebApiException, ParseException;
    }
}
