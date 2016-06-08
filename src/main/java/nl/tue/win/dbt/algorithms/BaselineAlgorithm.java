package nl.tue.win.dbt.algorithms;

import com.google.common.collect.RangeSet;
import nl.tue.win.dbt.Configuration;
import nl.tue.win.dbt.data.*;
import nl.tue.win.dbt.util.IntegerRangeSets;
import nl.tue.win.dbt.util.IntegerRanges;
import nl.tue.win.dbt.util.Iterators;
import org.jgrapht.Graph;
import org.jgrapht.GraphMapping;
import org.jgrapht.alg.isomorphism.IsomorphismInspector;
import org.jgrapht.alg.isomorphism.VF2SubgraphIsomorphismInspector;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class BaselineAlgorithm<V, E, L> implements DurablePattern<V, E, L>, Serializable {
    private final LabeledHistoryGraph<LabeledGraph<V, E, L>, V, E, L> lhg;
    private final IsomorphicSubgraphFinder<V, E, L> isf;

    public BaselineAlgorithm(
            final LabeledHistoryGraph<LabeledGraph<V, E, L>, V, E, L> lhg,
            final Configuration config) {
        Objects.requireNonNull(lhg);
        Objects.requireNonNull(config);
        this.lhg = lhg;
        this.isf = config.createIsomorphicSubgraphFinder(this.lhg);

    }

    public BaselineAlgorithm(
            final LabeledHistoryGraph<LabeledGraph<V, E, L>, V, E, L> lhg) {
        this(lhg, new Configuration());
    }

    public static <V, E, L> Set<Lifespan<LabeledGraph<V, E, L>>>
    queryMaximalCollectiveDurableGraphPattern(
            final LabeledHistoryGraph<LabeledGraph<V, E, L>, V, E, L> graph,
            final LabeledGraph<V, E, L> pattern,
            final RangeSet<Integer> intervals) {
        return new BaselineAlgorithm<>(graph)
                .queryMaximalCollectiveDurableGraphPattern(pattern, intervals);
    }

    public static <V, E, L> Set<Lifespan<LabeledGraph<V, E, L>>>
    queryMaximalCollectiveDurableGraphPattern(
            final LabeledHistoryGraph<LabeledGraph<V, E, L>, V, E, L> graph,
            final LabeledGraph<V, E, L> pattern) {
        return new BaselineAlgorithm<>(graph)
                .queryMaximalCollectiveDurableGraphPattern(pattern);
    }

    public static <V, E, L> Set<Lifespan<LabeledGraph<V, E, L>>>
    queryMinimalCollectiveDurableGraphPattern(
            final LabeledHistoryGraph<LabeledGraph<V, E, L>, V, E, L> graph,
            final LabeledGraph<V, E, L> pattern,
            final RangeSet<Integer> intervals) {
        return new BaselineAlgorithm<>(graph)
                .queryMinimalCollectiveDurableGraphPattern(pattern, intervals);
    }

    public static <V, E, L> Set<Lifespan<LabeledGraph<V, E, L>>>
    queryMinimalCollectiveDurableGraphPattern(
            final LabeledHistoryGraph<LabeledGraph<V, E, L>, V, E, L> graph,
            final LabeledGraph<V, E, L> pattern) {
        return new BaselineAlgorithm<>(graph)
                .queryMinimalCollectiveDurableGraphPattern(pattern);
    }

    public static <V, E, L> Set<Lifespan<LabeledGraph<V, E, L>>>
    queryMaximalContinuousDurableGraphPattern(
            final LabeledHistoryGraph<LabeledGraph<V, E, L>, V, E, L> graph,
            final LabeledGraph<V, E, L> pattern,
            final RangeSet<Integer> intervals) {
        return new BaselineAlgorithm<>(graph)
                .queryMaximalContinuousDurableGraphPattern(pattern, intervals);
    }

    public static <V, E, L> Set<Lifespan<LabeledGraph<V, E, L>>>
    queryMaximalContinuousDurableGraphPattern(
            final LabeledHistoryGraph<LabeledGraph<V, E, L>, V, E, L> graph,
            final LabeledGraph<V, E, L> pattern) {
        return new BaselineAlgorithm<>(graph)
                .queryMaximalContinuousDurableGraphPattern(pattern);
    }

    public static <V, E, L> Set<Lifespan<LabeledGraph<V, E, L>>>
    queryMinimalContinuousDurableGraphPattern(
            final LabeledHistoryGraph<LabeledGraph<V, E, L>, V, E, L> graph,
            final LabeledGraph<V, E, L> pattern,
            final RangeSet<Integer> intervals) {
        return new BaselineAlgorithm<>(graph)
                .queryMinimalContinuousDurableGraphPattern(pattern, intervals);
    }

    public static <V, E, L> Set<Lifespan<LabeledGraph<V, E, L>>>
    queryMinimalContinuousDurableGraphPattern(
            final LabeledHistoryGraph<LabeledGraph<V, E, L>, V, E, L> graph,
            final LabeledGraph<V, E, L> pattern) {
        return new BaselineAlgorithm<>(graph)
                .queryMinimalContinuousDurableGraphPattern(pattern);
    }

    @Override
    public Set<Lifespan<LabeledGraph<V, E, L>>> queryMaximalCollectiveDurableGraphPattern(
            final LabeledGraph<V, E, L> pattern,
            final RangeSet<Integer> intervals) {
        return Iterators.maximalElements(
                this.queryDurableGraphPattern(pattern, intervals),
                l -> IntegerRangeSets.totalSize(l.getRangeSet()),
                Comparator.comparing(i -> i),
                Integer.MIN_VALUE,
                HashSet::new);
    }

    @Override
    public Set<Lifespan<LabeledGraph<V, E, L>>> queryMaximalCollectiveDurableGraphPattern(
            final LabeledGraph<V, E, L> pattern) {
        return this.queryMaximalCollectiveDurableGraphPattern(pattern, this.lhg.lifespan());
    }

    @Override
    public Set<Lifespan<LabeledGraph<V, E, L>>> queryMaximalContinuousDurableGraphPattern(
            final LabeledGraph<V, E, L> pattern,
            final RangeSet<Integer> intervals) {
        return Iterators.maximalElements(
                this.queryDurableGraphPattern(pattern, intervals),
                l -> IntegerRangeSets.maximumSize(l.getRangeSet()),
                Comparator.comparing(i -> i),
                Integer.MIN_VALUE,
                HashSet::new
        )
                .stream()
                .map(l -> new Lifespan<>(
                        l.getData(),
                        IntegerRangeSets.intersect(
                                l.getRangeSet(),
                                IntegerRangeSets.maximalRanges(
                                        l.getRangeSet()))))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Lifespan<LabeledGraph<V, E, L>>> queryMaximalContinuousDurableGraphPattern(
            final LabeledGraph<V, E, L> pattern) {
        return this.queryMaximalContinuousDurableGraphPattern(pattern, this.lhg.lifespan());
    }

    public Set<Lifespan<LabeledGraph<V, E, L>>> queryMinimalCollectiveDurableGraphPattern(
            final LabeledGraph<V, E, L> pattern,
            final RangeSet<Integer> intervals) {
        return Iterators.minimalElements(
                this.queryDurableGraphPattern(pattern, intervals),
                l -> IntegerRangeSets.totalSize(l.getRangeSet()),
                Comparator.comparing(i -> i),
                Integer.MAX_VALUE,
                HashSet::new);
    }

    public Set<Lifespan<LabeledGraph<V, E, L>>> queryMinimalCollectiveDurableGraphPattern(
            final LabeledGraph<V, E, L> pattern) {
        return this.queryMinimalCollectiveDurableGraphPattern(pattern, this.lhg.lifespan());
    }

    public Set<Lifespan<LabeledGraph<V, E, L>>> queryMinimalContinuousDurableGraphPattern(
            final LabeledGraph<V, E, L> pattern,
            final RangeSet<Integer> intervals) {
        return Iterators.minimalElements(
                this.queryDurableGraphPattern(pattern, intervals),
                l -> IntegerRangeSets.maximumSize(l.getRangeSet()),
                Comparator.comparing(i -> i),
                Integer.MAX_VALUE,
                HashSet::new
        )
                .stream()
                .map(l -> new Lifespan<>(
                        l.getData(),
                        IntegerRangeSets.intersect(
                                l.getRangeSet(),
                                IntegerRangeSets.minimalRanges(
                                        l.getRangeSet()))))
                .collect(Collectors.toSet());
    }

    public Set<Lifespan<LabeledGraph<V, E, L>>> queryMinimalContinuousDurableGraphPattern(
            final LabeledGraph<V, E, L> pattern) {
        return this.queryMinimalContinuousDurableGraphPattern(pattern, this.lhg.lifespan());
    }

    public Set<Lifespan<LabeledGraph<V, E, L>>> queryDurableGraphPattern(
            final LabeledGraph<V, E, L> pattern,
            final RangeSet<Integer> intervals) {
        Map<LabeledGraph<V, E, L>, Lifespan<LabeledGraph<V, E, L>>> matches;
        matches = new HashMap<>();
        if(!this.lhg.isEmpty()) {
            Set<Integer> timestamps;
            timestamps = IntegerRangeSets.toTimestamps(
                    IntegerRangeSets.intersect(this.lhg.lifespan(), intervals));
            Set<LabeledGraph<V, E, L>> subgraphs;
            Lifespan<LabeledGraph<V,E,L>> lifespan;
            for(int t: timestamps) {
                subgraphs = this.isf.queryIsomorphicSubgraphs(t, pattern);
                for(LabeledGraph<V, E, L> subgraph: subgraphs) {
                    lifespan = matches.get(subgraph);
                    if(lifespan == null) {
                        matches.put(subgraph, new Lifespan<>(subgraph, t));
                    } else {
                        lifespan.getRangeSet().add(IntegerRanges.closed(t, t));
                    }
                }
            }
        }
        return new HashSet<>(matches.values());
    }

    public Set<Lifespan<LabeledGraph<V, E, L>>> queryDurableGraphPattern(
            final LabeledGraph<V, E, L> pattern) {
        return this.queryDurableGraphPattern(pattern, this.lhg.lifespan());
    }
}
