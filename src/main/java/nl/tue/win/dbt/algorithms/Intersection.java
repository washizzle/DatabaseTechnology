package nl.tue.win.dbt.algorithms;

import com.google.common.collect.Sets;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public interface Intersection {
    <T> Set<T> intersect(Set<T> set1, Set<T> set2);

    class RetainAllIntersection implements Intersection {
        @Override
        public <T> Set<T> intersect(Set<T> set1, Set<T> set2) {
            Objects.requireNonNull(set1);
            Objects.requireNonNull(set2);
            boolean set1IsLarger = set1.size() > set2.size();
            Set<T> set3 = new HashSet<>(set1IsLarger ? set2 : set1);
            set3.retainAll(set1IsLarger ? set1 : set2);
            return set3;
        }
    }

    class SmallestContainsIntersection implements Intersection {
        @Override
        public <T> Set<T> intersect(Set<T> set1, Set<T> set2) {
            Objects.requireNonNull(set1);
            Objects.requireNonNull(set2);
            boolean set1IsLarger = set1.size() > set2.size();
            Set<T> smallest = set1IsLarger ? set2 : set1;
            Set<T> largest = set1IsLarger ? set1 : set2;
            Set<T> s3 = new HashSet<>();
            for (T t : smallest) {
                if (largest.contains(t)) {
                    s3.add(t);
                }
            }
            return s3;
        }
    }

    class SmallestContainsStreamIntersection implements Intersection {
        public <T> Set<T> intersect(Set<T> set1, Set<T> set2) {
            Objects.requireNonNull(set1);
            Objects.requireNonNull(set2);
            boolean set1IsLarger = set1.size() > set2.size();
            Set<T> smallest = set1IsLarger ? set2 : set1;
            Set<T> largest = set1IsLarger ? set1 : set2;
            return smallest.stream().filter(largest::contains).collect(Collectors.toSet());
        }
    }

    class GuavaIntersection implements Intersection {
        @Override
        public <T> Set<T> intersect(Set<T> set1, Set<T> set2) {
            Objects.requireNonNull(set1);
            Objects.requireNonNull(set2);
            boolean set1IsLarger = set1.size() > set2.size();
            Set<T> smallerSet = set1IsLarger ? set2 : set1;
            Set<T> largerSet = set1IsLarger ? set1 : set2;
            return Sets.intersection(smallerSet, largerSet);
        }
    }
}
