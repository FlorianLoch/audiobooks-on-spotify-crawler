package ch.fdlo.hoerbuchspion.crawler;

import java.util.Iterator;
import java.util.function.Function;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.requests.IRequest;
import com.wrapper.spotify.requests.data.IPagingRequestBuilder;

import ch.fdlo.hoerbuchspion.crawler.util.*;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractFetcher<T> {
    protected final SpotifyApi spotifyApi;

    public AbstractFetcher(SpotifyApi authorizedApi) {
        this.spotifyApi = authorizedApi;
    }

    abstract public Iterable<T> fetch(String id);

    // Helper function to be called from the various fetch implementations.
    protected <R> Iterable<T> executeRequest(IPagingRequestBuilder<R, ? extends IRequest.Builder<Paging<R>, ?>> builder, Function<R, T> mapFn) {
        return new Iterable<T>() {
            @NotNull
            @Override
            public Iterator<T> iterator() {
                var pagingIter = new PaginationIterator<>(builder);

                return new MappingIterator<>(pagingIter, mapFn);
            }
        };
    }
}
