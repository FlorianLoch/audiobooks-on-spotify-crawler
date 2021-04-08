package ch.fdlo.hoerbuchspion.crawler.util;

import java.util.Iterator;
import java.util.function.Function;

// TODO: add tests
public class MappingIterator<T, R> implements Iterator<R> {
    final Iterator<T> innerIterator;
    final Function<T, R> mapFn;

    public MappingIterator(Iterator<T> innerIterator, Function<T, R> mapFn) {
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