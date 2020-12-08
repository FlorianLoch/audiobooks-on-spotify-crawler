package ch.fdlo.hoerbuchspion;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import com.wrapper.spotify.IHttpManager;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.exceptions.detailed.TooManyRequestsException;

import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;

public class RateLimitMonitoringHttpManager implements IHttpManager {
  public static int DEFAULT_RETRY_AFTER = 2; // given in seconds

  private Map<HttpVerb, VerbHandler> handlerFns = new HashMap<>();

  private IHttpManager wrappedManager;

  public RateLimitMonitoringHttpManager(IHttpManager httpManager) {
    this.wrappedManager = httpManager;

    // We need to do this after the the wrappedManager has been set - otherwise the shortcut syntax below
    // causes NPEs
    this.handlerFns.put(HttpVerb.GET, (URI uri, Header[] headers, HttpEntity body) -> {
      return this.wrappedManager.get(uri, headers);
    });
    this.handlerFns.put(HttpVerb.POST, this.wrappedManager::post);
    this.handlerFns.put(HttpVerb.PUT, this.wrappedManager::put);
    this.handlerFns.put(HttpVerb.DELETE, this.wrappedManager::delete);
  }

  private String handle(HttpVerb verb, URI uri, Header[] headers, HttpEntity body) throws IOException, SpotifyWebApiException, ParseException {
    VerbHandler handlerFn = this.handlerFns.get(verb);

    try {
      return handlerFn.fn(uri, headers, body);
    } catch (TooManyRequestsException e) {
      StringBuilder sb = new StringBuilder("Triggered the API rate limit: ");

      int retryAfter = e.getRetryAfter();
      if (retryAfter > 0) {
        sb.append("Going to wait for ").append(e.getRetryAfter()).append(" seconds.");
      } else {
        retryAfter = DEFAULT_RETRY_AFTER;
        sb.append("No retry after given... Going to wait ").append(DEFAULT_RETRY_AFTER).append(" seconds.");
      }

      System.out.println(sb);

      try {
        Thread.sleep(retryAfter * 1000L);
      } catch (InterruptedException e1) {
        // guess there is not much we can do about it...
        e1.printStackTrace();
      }

      return handlerFn.fn(uri, headers, body);
    }
  }

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

  enum HttpVerb {
    GET, POST, PUT, DELETE;
  }

  interface VerbHandler {
    String fn(URI uri, Header[] headers, HttpEntity body) throws IOException, SpotifyWebApiException, ParseException;
  }
}
