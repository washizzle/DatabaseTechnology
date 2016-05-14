package nl.tue.win.dbt.util;

import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IntegerRangeSets {
    private IntegerRangeSets() {
        // Do not construct utility class.
    }

    public static Set<Integer> toTimestamps(RangeSet<Integer> rangeSet) {
        return toStream(rangeSet).collect(Collectors.toSet());
    }

    public static BitSet toBitSet(RangeSet<Integer> rangeSet) {
        int limit = 0;
        if(!rangeSet.isEmpty()) {
            limit = rangeSet.span().upperEndpoint();
        }
        return toBitSet(rangeSet, limit);
    }

    public static BitSet toBitSet(RangeSet<Integer> rangeSet, int limit) {
        if(limit < 0) {
            throw new IllegalArgumentException("Limit should be non-negative");
        }
        BitSet bs = new BitSet(limit);
        toStream(rangeSet).filter(i -> i < limit)
                .forEach(bs::set);
        return bs;
    }

    public static <T extends Comparable<T>> RangeSet<T> intersect(RangeSet<T> rs1, RangeSet<T> rs2) {
        // RangeSet.intersect still missing in Guava
        // https://github.com/google/guava/issues/1825
        RangeSet<T> copy = TreeRangeSet.create(rs1);
        copy.removeAll(rs2.complement());
        return copy;
    }

    public static int totalSize(RangeSet<Integer> rangeSet) {
        return rangeSet.asRanges()
                .stream()
                .mapToInt(r -> IntegerRanges.toContiguousSet(r).size())
                .sum();
    }

    public static int maximumSize(RangeSet<Integer> rangeSet) {
        return rangeSet.asRanges()
                .stream()
                .map(r -> IntegerRanges.toContiguousSet(r).size())
                .max(Integer::max)
                .orElseThrow(NoSuchElementException::new);
    }

    public static int minimumSize(RangeSet<Integer> rangeSet) {
        return rangeSet.asRanges()
                .stream()
                .map(r -> IntegerRanges.toContiguousSet(r).size())
                .max(Integer::min)
                .orElseThrow(NoSuchElementException::new);
    }

    public static RangeSet<Integer> maximalRanges(RangeSet<Integer> rangeSet) {
        return create(
                Iterators.maximalElements(
                        rangeSet.asRanges(),
                        r -> IntegerRanges.toContiguousSet(r).size(),
                        Comparator.comparing(i -> i),
                        Integer.MIN_VALUE,
                        HashSet::new));
    }

    public static RangeSet<Integer> minimalRanges(RangeSet<Integer> rangeSet) {
        return create(
                Iterators.minimalElements(
                        rangeSet.asRanges(),
                        r -> IntegerRanges.toContiguousSet(r).size(),
                        Comparator.comparing(i -> i),
                        Integer.MAX_VALUE,
                        HashSet::new));
    }

    public static Stream<Integer> toStream(RangeSet<Integer> rangeSet) {
        return rangeSet.asRanges()
                .stream()
                .flatMap(r -> IntegerRanges.toContiguousSet(r).stream());
    }

    public static RangeSet<Integer> create(Iterable<Range<Integer>> ranges) {
        RangeSet<Integer> rangeSet;
        rangeSet = TreeRangeSet.create();
        ranges.forEach(rangeSet::add);
        return rangeSet;
    }
}
