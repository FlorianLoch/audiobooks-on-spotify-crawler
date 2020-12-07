package ch.fdlo.hoerbuchspion.crawler;

import java.io.IOException;
import java.util.Iterator;
import java.util.function.Function;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.requests.IRequest;
import com.wrapper.spotify.requests.data.IPagingRequestBuilder;

import org.apache.hc.core5.http.ParseException;

import ch.fdlo.hoerbuchspion.crawler.util.*;

public abstract class Fetcher<T> {
  protected final SpotifyApi spotifyApi;

  public Fetcher(SpotifyApi authorizedApi) {
    this.spotifyApi = authorizedApi;
  }

  abstract public Iterable<T> fetch() throws ParseException, SpotifyWebApiException, IOException;

  // Helper function to be called from the various fetch implementations.
  protected <R> Iterable<T> executeRequest(IPagingRequestBuilder<R, ? extends IRequest.Builder<Paging<R>, ?>> builder, Function<R, T> mapFn)
      throws ParseException, SpotifyWebApiException, IOException {

    return new Iterable<T>() {
      @Override
      public Iterator<T> iterator() {
        var pagingIter = new PaginationIterator<>(builder);

        return new MappingIterator<>(pagingIter, mapFn);
      }
    };
  }
}
