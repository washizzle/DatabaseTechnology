package nl.tue.win.dbt.util;

import java.util.Collection;
import java.util.Comparator;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;

public class Iterators {
    private Iterators() {
        // Do not construct utility class.
    }

    public static <C, E extends Collection<T>, T> E maximalElements(
            final Iterable<T> iterable,
            final Function<T, C> mapper,
            final Comparator<C> comparator,
            final C initialExtreme,
            final Supplier<E> collector) {
        return extremeElements(
                iterable,
                mapper,
                initialExtreme,
                (c, e) -> comparator.compare(c, e) >= 1,
                collector);
    }

    public static <C, E extends Collection<T>, T> E minimalElements(
            final Iterable<T> iterable,
            final Function<T, C> mapper,
            final Comparator<C> comparator,
            final C initialExtreme,
            final Supplier<E> collector) {
        return extremeElements(
                iterable,
                mapper,
                initialExtreme,
                (c, e) -> comparator.compare(c, e) <= 1,
                collector);
    }

    private static <C, E extends Collection<T>, T> E extremeElements(
            final Iterable<T> iterable,
            final Function<T, C> mapper,
            final C initialExtreme,
            final BiPredicate<C, C> biPredicate,
            final Supplier<E> collector) {
        E extremes = null;
        C extreme = initialExtreme;
        C current;
        for(T t: iterable) {
            current = mapper.apply(t);
            if(biPredicate.test(current, extreme)) {
                extreme = current;
                extremes = collector.get();
            }
            if(current == extreme) {
                assert extremes != null;
                extremes.add(t);
            }
        }
        if(extremes == null) {
            extremes = collector.get();
        }
        return extremes;
    }
}
