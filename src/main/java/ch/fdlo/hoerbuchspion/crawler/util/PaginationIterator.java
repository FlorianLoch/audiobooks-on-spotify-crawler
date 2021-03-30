package ch.fdlo.hoerbuchspion.crawler.util;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.requests.IRequest;
import com.wrapper.spotify.requests.data.IPagingRequestBuilder;

import org.apache.hc.core5.http.ParseException;

  // TODO: add tests
  public class PaginationIterator<T> implements Iterator<T> {
    final static int LIMIT = 50;
    final IPagingRequestBuilder<T, ? extends IRequest.Builder<Paging<T>, ?>> builder;
    T[] currentPageItems;
    int curIndex; // gets reset with every new request
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