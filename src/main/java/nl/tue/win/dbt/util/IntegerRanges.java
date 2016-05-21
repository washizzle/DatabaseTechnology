package nl.tue.win.dbt.util;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Range;

public class IntegerRanges {
    private IntegerRanges() {
        // Do not construct utility class.
    }

    public static ContiguousSet<Integer> toContiguousSet(Range<Integer> range) {
        return ContiguousSet.create(range, DiscreteDomain.integers());
    }

    public static Range<Integer> closed(int lower, int upper) {
        return Range.closed(lower, upper).canonical(DiscreteDomain.integers());
    }
}
