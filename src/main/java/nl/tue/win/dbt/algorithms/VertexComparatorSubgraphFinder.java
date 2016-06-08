package nl.tue.win.dbt.algorithms;

import nl.tue.win.dbt.data.LabeledGraph;
import nl.tue.win.dbt.data.LabeledHistoryGraph;
import org.jgrapht.GraphMapping;
import org.jgrapht.alg.isomorphism.VF2SubgraphIsomorphismInspector;

import java.io.Serializable;
import java.util.*;

public class VertexComparatorSubgraphFinder<V, E, L>
        implements IsomorphicSubgraphFinder<V, E, L>, Serializable {

    private final LabeledHistoryGraph<LabeledGraph<V, E, L>, V, E, L> lhg;

    public VertexComparatorSubgraphFinder(
            final LabeledHistoryGraph<LabeledGraph<V, E, L>, V, E, L> lhg) {
        Objects.requireNonNull(lhg);
        this.lhg = lhg;
    }

    @Override
    public Set<LabeledGraph<V, E, L>> queryIsomorphicSubgraphs(
            final int timestamp,
            final LabeledGraph<V, E, L> pattern) {
        final LabeledGraph<V, E, L> graph = this.lhg.get(timestamp);
        Comparator<V> vertexComparator = (v1, v2) -> this.compareVertices(graph, pattern ,v1, v2);

        VF2SubgraphIsomorphismInspector<V, E> inspector;
        inspector = new VF2SubgraphIsomorphismInspector<>(graph, pattern, vertexComparator, null);

        Set<LabeledGraph<V, E, L>> matches;
        matches = new HashSet<>();

        if(inspector.isomorphismExists()) {
            Iterator<GraphMapping<V, E>> mappings;
            mappings = inspector.getMappings();
            while(mappings.hasNext()) {
                matches.add(this.createLabeledGraph(
                        graph,
                        pattern,
                        mappings.next()));
            }
        }
        return matches;
    }

    private int compareVertices(
            final LabeledGraph<V, E, L> graph,
            final LabeledGraph<V, E, L> pattern,
            final V graphVertex,
            final V patternVertex) {
        final int notEqual = Integer.MIN_VALUE;
        final int equal = 0;
        int rv;
        if(graphVertex.equals(patternVertex)
                && graph.getLabels(graphVertex).containsAll(pattern.getLabels(patternVertex))) {
            rv = equal;
        } else {
            rv = notEqual;
        }
        return rv;
    }

    private LabeledGraph<V, E, L> createLabeledGraph(
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
            edge = match.addEdge(
                    graph.getEdgeSource(edge),
                    graph.getEdgeTarget(edge));
            assert edge != null;
        }
        return match;
    }
}
