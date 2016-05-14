package nl.tue.win.dbt;

import nl.tue.win.dbt.algorithms.Intersection;

import java.util.*;

// http://stackoverflow.com/a/7574395
public class IntersectTest {

    static final Random rng = new Random();

    static abstract class RunIt {
        public int count;
        public long nsTime;
        abstract Set<Integer> Run(Set<Integer> s1, Set<Integer> s2);
    }

    static class RetainAllIntersections extends RunIt {
        static final Intersection intersection;

        static {
            intersection = new Intersection.RetainAllIntersection();
        }

        public Set<Integer> Run(Set<Integer> set1, Set<Integer> set2) {
            return intersection.intersect(set1, set2);
        }
    }

    static class SmallestContainsIntersections extends RunIt {
        static final Intersection intersection;

        static {
            intersection = new Intersection.SmallestContainsIntersection();
        }

        public Set<Integer> Run(Set<Integer> set1, Set<Integer> set2) {
            return intersection.intersect(set1, set2);
        }
    }

    static class ByStream extends RunIt {
        static final Intersection intersection;

        static {
            intersection = new Intersection.SmallestContainsStreamIntersection();
        }

        public Set<Integer> Run(Set<Integer> set1, Set<Integer> set2) {
            return intersection.intersect(set1, set2);
        }
    }

    static class GuavaIntersections extends RunIt {
        static final Intersection intersection;

        static {
            intersection = new Intersection.GuavaIntersection();
        }

        public Set<Integer> Run(Set<Integer> set1, Set<Integer> set2) {
            return intersection.intersect(set1, set2);
        }
    }

    static class Bitset extends RunIt {
        public Set<Integer> Run(Set<Integer> set1, Set<Integer> set2) {
            boolean set1IsLarger = set1.size() > set2.size();
            BitSet bs1 = new BitSet();
            set1.forEach(bs1::set);
            BitSet bs2 = new BitSet();
            set2.forEach(bs2::set);
            Set<Integer> s = new HashSet<>();
            BitSet bs3 = set1IsLarger ? bs1 : bs2;
            bs3.and(set1IsLarger ? bs2 : bs1);
            for (int i = bs3.nextSetBit(0); i >= 0; i = bs3.nextSetBit(i+1)) {
                s.add(i);
            }
            return s;
        }
    }

    static Set<Integer> makeSet (int count, float load) {
        Set<Integer> s = new HashSet<>();
        for (int i = 0; i < count; i++) {
            s.add(rng.nextInt(Math.max(1, (int)(count * load))));
        }
        return s;
    }

    // really crummy ubench stuff
    public static void main(String[] args) {
        int[][] bounds = {
                {1, 1},
                {1, 10},
                {1, 100},
                {1, 1000},
                {10, 2},
                {10, 10},
                {10, 100},
                {10, 1000},
                {100, 1},
                {100, 10},
                {100, 100},
                {100, 1000},
        };
        int totalReps = 4;
        int cycleReps = 1000;
        int subReps = 1000;
        float load = 0.8f;
        for (int tc = 0; tc < totalReps; tc++) {
            for (int[] bound : bounds) {
                int set1size = bound[0];
                int set2size = bound[1];
                System.out.println("Running tests for " + set1size + "x" + set2size);
                ArrayList<RunIt> allRuns = new ArrayList<>(
                        Arrays.asList(
                                new RetainAllIntersections(),
                                new SmallestContainsIntersections(),
                                new ByStream(),
                                new GuavaIntersections(),
                                new Bitset()));
                for (int r = 0; r < cycleReps; r++) {
                    ArrayList<RunIt> runs = new ArrayList<>(allRuns);
                    Set<Integer> set1 = makeSet(set1size, load);
                    Set<Integer> set2 = makeSet(set2size, load);
                    while (runs.size() > 0) {
                        int runIdx = rng.nextInt(runs.size());
                        RunIt run = runs.remove(runIdx);
                        long start = System.nanoTime();
                        int count = 0;
                        for (int s = 0; s < subReps; s++) {
                            count += run.Run(set1, set2).size();
                        }
                        long time = System.nanoTime() - start;
                        run.nsTime += time;
                        run.count += count;
                    }
                }
                for (RunIt run : allRuns) {
                    double sec = run.nsTime / (10e6);
                    System.out.println(run + " took " + sec + " count=" + run.count);
                }
            }
        }
    }
}