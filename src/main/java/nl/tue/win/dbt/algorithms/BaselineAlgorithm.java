package nl.tue.win.dbt.algorithms;

import com.google.common.collect.RangeSet;
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
    private final List<Graph<LabeledVertex<V, L>, E>> labeledGraphs;

    public BaselineAlgorithm(
            final LabeledHistoryGraph<LabeledGraph<V, E, L>, V, E, L> lhg) {
        Objects.requireNonNull(lhg);
        this.lhg = lhg;
        this.labeledGraphs = this.lhg.stream()
                .map(LabeledGraph::createLabeledVertexGraph)
                .collect(Collectors.toList());
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
                subgraphs = queryIsomorphicSubgraphs(t, pattern);
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

    private Set<LabeledGraph<V, E, L>> queryIsomorphicSubgraphs2(
            final int timestamp,
            final LabeledGraph<V, E, L> pattern) {
        final LabeledGraph<V, E, L> graph = this.lhg.get(timestamp);
        Comparator<E> edgeComparator = null;
        Comparator<V> vertexComparator = new Comparator<V>() {
            @Override
            public int compare(V v1, V v2) {
                final int neq = Integer.MIN_VALUE;
                if(!v1.equals(v2)) {
                    return neq;
                }
                // At least one vertex in graph and at least one vertex in pattern.
                // The table below crosses all invalid states.
                // TODO: What to determine in state "???"
                // +---------+-------+---------+------+------+
                // |   V1\V2 | Graph | Pattern | Both | None |
                // +---------+-------+---------+------+------+
                // |   Graph |   x   |         |      |   x  |
                // +---------+-------+---------+------+------+
                // | Pattern |       |    x    |      |   x  |
                // +---------+-------+---------+------+------+
                // |    Both |       |         |  ??? |   x  |
                // +---------+-------+---------+------+------+
                // |    None |   x   |    x    |   x  |   x  |
                // +---------+-------+---------+------+------+

                Set<L> g1 = graph.getLabels(v1);
                Set<L> p1 = pattern.getLabels(v1);
                Set<L> g2 = graph.getLabels(v2);
                Set<L> p2 = pattern.getLabels(v2);
                throw new UnsupportedOperationException("Not yet implemented.");
            }
        };

        VF2SubgraphIsomorphismInspector<V, E> inspector;
        inspector = new VF2SubgraphIsomorphismInspector<>(graph, pattern, vertexComparator, edgeComparator);

        Set<LabeledGraph<V, E, L>> matches;
        matches = new HashSet<>();

        if(inspector.isomorphismExists()) {
            Iterator<GraphMapping<V, E>> mappings;
            mappings = inspector.getMappings();
            while(mappings.hasNext()) {
                matches.add(createLabeledGraph2(graph, pattern, mappings.next()));
            }
        }
        return matches;
    }

    private LabeledGraph<V, E, L> createLabeledGraph2(
            final LabeledGraph<V, E, L> graph,
            final LabeledGraph<V, E, L> pattern,
            final GraphMapping<V, E> mapping) {
        // TODO: check if direction is correct
        final boolean direction = false; // From pattern to graph

        LabeledGraph<V, E, L> match = this.lhg.getGraphCreator().create();
        boolean added;
        for(V vertex: pattern.vertexSet()) {
            vertex = mapping.getVertexCorrespondence(vertex, direction);
            match.addVertex(vertex);
            for(L label: graph.getLabels(vertex)) {
                added = match.addLabel(vertex, label);
                assert added;
            }
        }
        for(E edge: pattern.edgeSet()) {
            edge = mapping.getEdgeCorrespondence(edge, direction);
            added = match.addEdge(
                    graph.getEdgeSource(edge),
                    graph.getEdgeTarget(edge),
                    edge);
            // TODO: check if edges are added correctly. I.e. e == match.addEdge(v, v2) and does match.addEdge(v, v2, e) change e?
            assert added;
        }
        return match;
    }

    private Set<LabeledGraph<V, E, L>> queryIsomorphicSubgraphs(
            final int timestamp,
            final LabeledGraph<V, E, L> pattern) {
        Graph<LabeledVertex<V, L>, E> labeledGraph;
        labeledGraph = this.labeledGraphs.get(timestamp);
        Graph<LabeledVertex<V, L>, E> labeledPatternGraph;
        labeledPatternGraph = pattern.createLabeledVertexGraph();

        IsomorphismInspector<LabeledVertex<V, L>, E> inspector;
        inspector = new VF2SubgraphIsomorphismInspector<>(labeledGraph, labeledPatternGraph);

        Set<LabeledGraph<V, E, L>> matches;
        matches = new HashSet<>();

        if(inspector.isomorphismExists()) {
            Iterator<GraphMapping<LabeledVertex<V, L>, E>> mappings;
            mappings = inspector.getMappings();
            while(mappings.hasNext()) {
                matches.add(createLabeledGraph(
                        labeledGraph,
                        labeledPatternGraph,
                        mappings.next()));
            }
        }
        return matches;
    }

    private LabeledGraph<V, E, L> createLabeledGraph(
            final Graph<LabeledVertex<V, L>, E> labeledGraph,
            final Graph<LabeledVertex<V, L>, E> labeledPatternGraph,
            final GraphMapping<LabeledVertex<V, L>, E> mapping) {
        // TODO: check if direction is correct
        final boolean direction = false; // From pattern to graph

        LabeledGraph<V, E, L> match = this.lhg.getGraphCreator().create();
        V v;
        boolean added;
        for(LabeledVertex<V, L> lv: labeledPatternGraph.vertexSet()) {
            lv = mapping.getVertexCorrespondence(lv, direction);
            v = lv.getData();
            match.addVertex(v);
            for(L label: lv.getLabels()) {
                added = match.addLabel(v, label);
                assert added;
            }
        }
        V v2;
        for(E e: labeledPatternGraph.edgeSet()) {
            e = mapping.getEdgeCorrespondence(e, direction);
            v = labeledGraph.getEdgeSource(e).getData();
            v2 = labeledGraph.getEdgeTarget(e).getData();
            e = match.addEdge(v, v2);
            assert e != null;
        }
        return match;
    }
}
