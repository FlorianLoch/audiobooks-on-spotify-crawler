package ch.fdlo.hoerbuchspion.crawler;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.requests.IRequest;
import com.wrapper.spotify.requests.data.IPagingRequestBuilder;

import org.apache.hc.core5.http.ParseException;

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

  // TODO: Extract and add tests
  class MappingIterator<T, R> implements Iterator<R> {
    final Iterator<T> innerIterator;
    final Function<T, R> mapFn;

    MappingIterator(Iterator<T> innerIterator, Function<T, R> mapFn ) {
      this.innerIterator = innerIterator;
      this.mapFn = mapFn;
    }

    @Override
    public boolean hasNext() {
      return this.innerIterator.hasNext();
    }

    @Override
    public R next() {
      return this.mapFn.apply(this.innerIterator.next());
    }
  }

  // TODO: Extract and add tests
  class PaginationIterator<T> implements Iterator<T> {
    final static int LIMIT = 50;
    final IPagingRequestBuilder<T, ? extends IRequest.Builder<Paging<T>, ?>> builder;
    T[] currentPageItems;
    int curIndex; // gets reseted with every new request
    int curOffset = -LIMIT; // in order to start at 0 in the first iteration
    int totalItems;

    public PaginationIterator(IPagingRequestBuilder<T, ? extends IRequest.Builder<Paging<T>, ?>> builder) {
      this.builder = builder;

      builder.limit(LIMIT);
    }

    @Override
    public boolean hasNext() {
      if (this.currentPageItems == null || (this.curIndex == LIMIT && this.curIndex + this.curOffset < this.totalItems)) {
        this.requestNextPage();
      }

      return this.curIndex + this.curOffset < this.totalItems;
    }

    private void requestNextPage() {
      var req = this.builder.offset(this.curOffset + LIMIT).build();

      try {
        var page = req.execute();

        this.currentPageItems = page.getItems();
        this.totalItems = page.getTotal();
      } catch (ParseException | SpotifyWebApiException | IOException e) {
        // TODO: rethrow Runtime Exception. Super important as otherwise a consumer might continue to call next().
        // Required for correct program flow.

        e.printStackTrace();
      }

      this.curOffset = this.curOffset + LIMIT;
      this.curIndex = 0;
    }

    @Override
    public T next() {
      if (!this.hasNext()) {
        throw new NoSuchElementException();
      }

      var curItem = this.currentPageItems[this.curIndex];

      this.curIndex++;

      return curItem;
    }
  }
}
