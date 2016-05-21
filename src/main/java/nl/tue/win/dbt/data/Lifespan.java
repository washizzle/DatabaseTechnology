package nl.tue.win.dbt.data;

import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import nl.tue.win.dbt.util.IntegerRanges;

import java.util.Objects;

public class Lifespan<T> {
    private final T data;
    private final RangeSet<Integer> rangeSet;

    public Lifespan(T data) {
        Objects.requireNonNull(data);
        this.data = data;
        this.rangeSet = TreeRangeSet.create();
    }

    public Lifespan(T data, int timeStamp) {
        this(data, IntegerRanges.closed(timeStamp, timeStamp));
    }

    public Lifespan(T data, Range<Integer> range) {
        this(data);
        Objects.requireNonNull(range);
        this.rangeSet.add(range.canonical(DiscreteDomain.integers()));
    }

    public Lifespan(T data, RangeSet<Integer> rangeSet) {
        Objects.requireNonNull(data);
        Objects.requireNonNull(rangeSet);
        this.data = data;
        this.rangeSet = rangeSet;
    }

    public T getData() {
        return data;
    }

    public RangeSet<Integer> getRangeSet() {
        return rangeSet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Lifespan<?> lifespan = (Lifespan<?>) o;

        if (data != null ? !data.equals(lifespan.data) : lifespan.data != null) return false;
        return rangeSet != null ? rangeSet.equals(lifespan.rangeSet) : lifespan.rangeSet == null;

    }

    @Override
    public int hashCode() {
        int result = data != null ? data.hashCode() : 0;
        result = 31 * result + (rangeSet != null ? rangeSet.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Lifespan{" +
                "data=" + data +
                ", rangeSet=" + rangeSet +
                '}';
    }
}
