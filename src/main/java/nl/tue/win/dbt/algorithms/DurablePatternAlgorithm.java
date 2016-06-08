package nl.tue.win.dbt.algorithms;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.RangeSet;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.TreeRangeSet;
import nl.tue.win.dbt.Configuration;
import nl.tue.win.dbt.algorithms.TimeIndices.candidatefilters.CandidateFilter;
import nl.tue.win.dbt.data.*;
import nl.tue.win.dbt.util.Graphs;
import nl.tue.win.dbt.util.IntegerRangeSets;
import nl.tue.win.dbt.util.IntegerRanges;

import java.util.*;

public class DurablePatternAlgorithm<V, E, L> {

    private final LabeledVersionGraph<V, E, L> lvg;
    private final Configuration config;
    private final CandidateFilter<V, E, L> candidateFilter;
    private final GraphCreator<LabeledGraph<V, E, L>, V, E> graphCreator;

    private LabeledGraph<V, E, L> pattern;
    private BitSet intervals;
    private boolean collective;
    private List<V> vertices;
    private Set<Lifespan<LabeledGraph<V, E, L>>> matches;
    private int threshold;

    public DurablePatternAlgorithm(
            final LabeledHistoryGraph<LabeledGraph<V, E, L>, V, E, L> historyGraph) {
        this(historyGraph, new Configuration());
    }

    public DurablePatternAlgorithm(
            LabeledHistoryGraph<LabeledGraph<V, E, L>, V, E, L> historyGraph,
            Configuration config) {
        this(new LabeledVersionGraph<>(historyGraph),
                historyGraph.getGraphCreator(),
                config);
    }

    public DurablePatternAlgorithm(
            final LabeledVersionGraph<V, E, L> lvg,
            final GraphCreator<LabeledGraph<V, E, L>, V , E> graphCreator) {
        this(lvg, graphCreator, new Configuration());
    }

    public DurablePatternAlgorithm(
            final LabeledVersionGraph<V, E, L> lvg,
            final GraphCreator<LabeledGraph<V, E, L>, V, E> graphCreator,
            final Configuration config) {
        Objects.requireNonNull(lvg);
        Objects.requireNonNull(graphCreator);
        Objects.requireNonNull(config);

        this.lvg = lvg;
        this.graphCreator = graphCreator;
        this.config = config;
        this.candidateFilter = config.createCandidateFilter(this.lvg);

        this.threshold = 1;
    }

    public static <V, E, L> Set<Lifespan<LabeledGraph<V, E, L>>>
    queryMaximalCollectiveDurableGraphPattern(
            final LabeledHistoryGraph<LabeledGraph<V, E, L>, V, E, L> graph,
            final LabeledGraph<V, E, L> pattern,
            final RangeSet<Integer> intervals,
            final Configuration config) {
        return new DurablePatternAlgorithm<>(graph, config)
                .queryMaximalCollectiveDurableGraphPattern(pattern, intervals);
    }

    public static <V, E, L> Set<Lifespan<LabeledGraph<V, E, L>>>
    queryMaximalCollectiveDurableGraphPattern(
            final LabeledHistoryGraph<LabeledGraph<V, E, L>, V, E, L> graph,
            final LabeledGraph<V, E, L> pattern,
            final RangeSet<Integer> intervals) {
        return queryMaximalCollectiveDurableGraphPattern(
                graph,
                pattern,
                intervals,
                new Configuration());
    }

    public static <V, E, L> Set<Lifespan<LabeledGraph<V, E, L>>>
    queryMaximalCollectiveDurableGraphPattern(
            final LabeledHistoryGraph<LabeledGraph<V, E, L>, V, E, L> graph,
            final LabeledGraph<V, E, L> pattern) {
        return queryMaximalCollectiveDurableGraphPattern(
                graph,
                pattern,
                graph.lifespan());
    }

    public static <V, E, L> Set<Lifespan<LabeledGraph<V, E, L>>>
    queryMaximalContinuousDurableGraphPattern(
            final LabeledHistoryGraph<LabeledGraph<V, E, L>, V, E, L> graph,
            final LabeledGraph<V, E, L> pattern,
            final RangeSet<Integer> intervals,
            final Configuration config) {
        return new DurablePatternAlgorithm<>(graph, config)
                .queryMaximalContinuousDurableGraphPattern(pattern, intervals);
    }

    public static <V, E, L> Set<Lifespan<LabeledGraph<V, E, L>>>
    queryMaximalContinuousDurableGraphPattern(
            final LabeledHistoryGraph<LabeledGraph<V, E, L>, V, E, L> graph,
            final LabeledGraph<V, E, L> pattern,
            final RangeSet<Integer> intervals) {
        return queryMaximalContinuousDurableGraphPattern(
                graph,
                pattern,
                intervals,
                new Configuration());
    }

    public static <V, E, L> Set<Lifespan<LabeledGraph<V, E, L>>>
    queryMaximalContinuousDurableGraphPattern(
            final LabeledHistoryGraph<LabeledGraph<V, E, L>, V, E, L> graph,
            final LabeledGraph<V, E, L> pattern) {
        return queryMaximalContinuousDurableGraphPattern(
                graph,
                pattern,
                graph.lifespan());
    }

    public Set<Lifespan<LabeledGraph<V, E, L>>> queryMaximalCollectiveDurableGraphPattern(
            final LabeledGraph<V, E, L> pattern) {
        return this.query(pattern, new BitSet(this.lvg.getSize()), true);
    }

    public Set<Lifespan<LabeledGraph<V, E, L>>> queryMaximalCollectiveDurableGraphPattern(
            final LabeledGraph<V, E, L> pattern,
            final RangeSet<Integer> intervals) {
        return this.query(pattern, IntegerRangeSets.toBitSet(intervals), true);
    }

    public Set<Lifespan<LabeledGraph<V, E, L>>> queryMaximalContinuousDurableGraphPattern(
            final LabeledGraph<V, E, L> pattern) {
        return this.query(pattern, new BitSet(this.lvg.getSize()), false);
    }

    public Set<Lifespan<LabeledGraph<V, E, L>>> queryMaximalContinuousDurableGraphPattern(
            final LabeledGraph<V, E, L> pattern,
            final RangeSet<Integer> intervals) {
        return this.query(pattern, IntegerRangeSets.toBitSet(intervals), false);
    }

    private Set<Lifespan<LabeledGraph<V, E, L>>> query(
            final LabeledGraph<V, E, L> pattern,
            final BitSet intervals,
            final boolean collective) {
        this.configureAlgorithm(pattern, intervals, collective);
        SetMultimap<V, V> candidates = HashMultimap.create();
        for (V vertex : this.vertices) {
            candidates.putAll(vertex, filterCandidates(vertex));
            if (candidates.get(vertex).isEmpty()) {
                return this.matches;
            }
        }
        candidates = refineCandidates(candidates);
        durableGraphSearch(0, candidates);
        return this.matches;
    }

    private void configureAlgorithm(
            final LabeledGraph<V, E, L> pattern,
            final BitSet intervals,
            final boolean collective) {
        Objects.requireNonNull(pattern);
        Objects.requireNonNull(intervals);
        this.pattern = pattern;
        this.intervals = intervals;
        this.collective = collective;
        this.vertices = new ArrayList<>(this.pattern.vertexSet());
        this.matches = new HashSet<>();
    }

    private int calculateDuration(final BitSet intervals) {
        int duration;
        if(this.collective) {
            duration = intervals.cardinality();
        } else {
            duration = this.config.longestSetBits(intervals);
        }
        return duration;
    }

    private Set<V> filterCandidates(final V patternVertex) {
        return this.candidateFilter.filterCandidates(this.pattern, patternVertex, intervals);
    }

    private SetMultimap<V, V> refineCandidates(final SetMultimap<V, V> candidates) {
        Set<V> allCandidateNeighbors;
        Set<V> candidateNeighbors;
        Set<V> invalidCandidates;
        for(V patternVertex: this.vertices) {
            for(V patternNeighborVertex: Graphs.getNextNeighbors(this.pattern, patternVertex)) {
                allCandidateNeighbors = new HashSet<>();
                invalidCandidates = new HashSet<>();
                for(V candidateVertex: candidates.get(patternVertex)) {
                    candidateNeighbors = timeJoin(
                            patternVertex,
                            candidateVertex,
                            patternNeighborVertex,
                            candidates);
                    if(candidateNeighbors.isEmpty()) {
                        invalidCandidates.add(candidateVertex);
                    } else {
                        allCandidateNeighbors.addAll(candidateNeighbors);
                    }
                }
                if(allCandidateNeighbors.isEmpty()) {
                    return HashMultimap.create();
                }
                invalidCandidates.forEach(v -> candidates.remove(patternVertex, v));
                candidates.replaceValues(patternNeighborVertex, allCandidateNeighbors);
            }
        }
        return candidates;
    }

    private Set<V> timeJoin(
            final V patternVertex,
            final V candidateVertex,
            final V patternNeighborVertex,
            final SetMultimap<V, V> candidates) {
        Set<V> candidateNeighbors = new HashSet<>();
        BitSet candidateLabelsLifespan = calculateLabelSetLifespan(patternVertex, candidateVertex);
        if(calculateDuration(candidateLabelsLifespan) < this.threshold) {
            return candidateNeighbors;
        }
        Set<V> possibleNeighbors = this.config.intersect(
                candidates.get(patternNeighborVertex),
                Graphs.getNextNeighbors(this.lvg, candidateVertex));
        BitSet possibleNeighborLifespan;
        for(V possibleNeighbor: possibleNeighbors) {
            possibleNeighborLifespan = calculateLabelSetLifespan(patternNeighborVertex, possibleNeighbor);
            possibleNeighborLifespan.and(candidateLabelsLifespan);
            if(hasValidEdge(candidateVertex, possibleNeighbor, possibleNeighborLifespan)) {
                candidateNeighbors.add(possibleNeighbor);
            }
        }
        return candidateNeighbors;
    }

    private BitSet calculateLabelSetLifespan(final V patternVertex, final V lvgVertex) {
        Iterator<L> patternVertexLabels = this.pattern.getLabels(patternVertex).iterator();
        BitSet bs = new BitSet(this.lvg.getSize());
        bs.or(this.intervals);
        BitSet lvgLabelLifespan;
        while(!bs.isEmpty() && patternVertexLabels.hasNext()) {
            lvgLabelLifespan = this.lvg.labelLifespan(
                    lvgVertex, patternVertexLabels.next());
            if(lvgLabelLifespan == null) {
                bs.clear();
            } else {
                bs.and(lvgLabelLifespan);
            }
        }
        return bs;
    }

    private boolean hasValidEdge(
            final V vertex1,
            final V vertex2,
            final BitSet intervals) {
        BitSet bs;
        for(E edge: this.lvg.getAllEdges(vertex1, vertex2)) {
            bs = new BitSet(this.lvg.getSize());
            bs.or(intervals);
            bs.and(this.lvg.edgeLifespan(edge));
            if(calculateDuration(bs) >= this.threshold) {
                return true;
            }
        }
        return false;
    }

    private void durableGraphSearch(
            final int vertexIndex,
            final SetMultimap<V, V> candidates) {
        if (vertexIndex == this.vertices.size() - 1) {
            BitSet intervals = new BitSet(this.lvg.getSize());
            intervals.or(this.intervals);

            V candidateVertex;
            for(V patternVertex: this.vertices) {
                candidateVertex = candidates.get(patternVertex).iterator().next(); // TODO: is this always a single element for last vertex?
                intervals.and(calculateLabelSetLifespan(patternVertex, candidateVertex));
                if(intervals.isEmpty()) {
                    return;
                }
            }
            V source;
            V target;
            E candidateEdge;
            for (E patternEdge : this.pattern.edgeSet()) {
                // TODO: handle multiple edges for same pairs.

                source = this.pattern.getEdgeSource(patternEdge);
                target = this.pattern.getEdgeTarget(patternEdge);
                source = candidates.get(source).iterator().next(); // TODO: is this always a single element for last vertex?
                target = candidates.get(target).iterator().next(); // TODO: is this always a single element for last vertex?

                // TODO: Following line only works for unique edges between two vertices.
                candidateEdge = this.lvg.getEdge(source, target);
                intervals.and(this.lvg.edgeLifespan(candidateEdge));
                if(intervals.isEmpty()) {
                    return;
                }
            }
            int duration = calculateDuration(intervals);
            if (duration == threshold) {
                updateState(candidates, intervals);
            } else if(duration > threshold) {
                threshold = duration;
                this.matches.clear();
                updateState(candidates, intervals);
            }
        } else {
            V vertex = this.vertices.get(vertexIndex);
            SetMultimap<V, V> copiedCandidates;
            for(V u: candidates.get(vertex)) {
                if(isNewCandidate(vertexIndex, u, candidates)) {
                    copiedCandidates = HashMultimap.create(candidates);
                    copiedCandidates.removeAll(vertex);
                    copiedCandidates.put(vertex, u);
                    copiedCandidates = refineCandidates(copiedCandidates);
                    if (!copiedCandidates.isEmpty()) {
                        durableGraphSearch(vertexIndex + 1, copiedCandidates);
                    }
                }
            }
        }
    }

    private boolean isNewCandidate(
            final int vertexIndex,
            final V candidate,
            final SetMultimap<V, V> candidates) {
        for(int i=0; i < vertexIndex; i++) {
            if(candidates.get(this.vertices.get(i)).contains(candidate)) {
                return false;
            }
        }
        return true;
    }

    private void updateState(
            final SetMultimap<V, V> candidates, // TODO: is this always a single element for last vertex?
            final BitSet intervals) {
        // TODO: Following code assumes a single match.
        // TODO: Following code only works for unique edges between two vertices.
        LabeledGraph<V, E, L> match = this.graphCreator.create();

        // TODO: Following code looks like first case durableGraphSearch. Refactor???

        V vertex;
        for(V patternVertex: this.vertices) {
            vertex = candidates.get(patternVertex).iterator().next(); // TODO: is this always a single element for last vertex?
            match.addVertex(vertex);
            for (L label : this.pattern.getLabels(patternVertex)) {
                match.addLabel(vertex, label);
            }
        }

        V source;
        V target;
        for (E patternEdge : this.pattern.edgeSet()) {
            // TODO: handle multiple edges for same pairs.
            source = this.pattern.getEdgeSource(patternEdge);
            target = this.pattern.getEdgeTarget(patternEdge);
            source = candidates.get(source).iterator().next(); // TODO: is this always a single element for last vertex?
            target = candidates.get(target).iterator().next(); // TODO: is this always a single element for last vertex?
            match.addEdge(source, target);
        }

        RangeSet<Integer> rangeSet = this.calculateMatchRangeSet(intervals);
        this.matches.add(new Lifespan<>(match, rangeSet));
    }

    private RangeSet<Integer> calculateMatchRangeSet(BitSet intervals) {
        RangeSet<Integer> rangeSet = TreeRangeSet.create();
        int setIndex = intervals.nextSetBit(0);
        int unsetIndex;
        while(setIndex >= 0) {
            unsetIndex = intervals.nextClearBit(setIndex);
            if(this.collective || unsetIndex - setIndex == this.threshold) {
                rangeSet.add(IntegerRanges.closed(setIndex, unsetIndex -1));
            }
            setIndex = intervals.nextSetBit(unsetIndex + 1);
        }
        return rangeSet;
    }
}
