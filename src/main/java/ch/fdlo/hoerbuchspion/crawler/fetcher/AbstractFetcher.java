package ch.fdlo.hoerbuchspion.crawler.fetcher;

import java.util.function.Function;

import ch.fdlo.hoerbuchspion.crawler.types.SpotifyObject;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.requests.IRequest;
import com.wrapper.spotify.requests.data.IPagingRequestBuilder;

import ch.fdlo.hoerbuchspion.crawler.util.*;

public abstract class AbstractFetcher<T> {
    protected final SpotifyApi spotifyApi;

    public AbstractFetcher(SpotifyApi authorizedApi) {
        this.spotifyApi = authorizedApi;
    }

    abstract public Iterable<T> fetch(SpotifyObject target);

    // Helper function to be called from the various fetch implementations.
    protected <R> Iterable<T> executeRequest(IPagingRequestBuilder<R, ? extends IRequest.Builder<Paging<R>, ?>> builder, Function<R, T> mapFn) {
        return () -> {
            var pagingIter = new PaginationIterator<>(builder);

            return new MappingIterator<>(pagingIter, mapFn);
        };
    }
}
