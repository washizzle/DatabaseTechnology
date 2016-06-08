package nl.tue.win.dbt.algorithms;

import nl.tue.win.dbt.data.GraphCreator;
import nl.tue.win.dbt.data.LabeledGraph;
import nl.tue.win.dbt.data.LabeledHistoryGraph;
import nl.tue.win.dbt.data.LabeledVertex;
import org.jgrapht.Graph;
import org.jgrapht.GraphMapping;
import org.jgrapht.alg.isomorphism.IsomorphismInspector;
import org.jgrapht.alg.isomorphism.VF2SubgraphIsomorphismInspector;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class LabeledVertexSubgraphFinder<V, E, L>
        implements IsomorphicSubgraphFinder<V, E, L>, Serializable {

    private final List<Graph<LabeledVertex<V, L>, E>> labeledGraphs;
    private final GraphCreator<LabeledGraph<V, E, L>, V, E> graphCreator;

    public LabeledVertexSubgraphFinder(
            final LabeledHistoryGraph<LabeledGraph<V, E, L>, V, E, L> lhg) {
        Objects.requireNonNull(lhg);
        this.graphCreator = lhg.getGraphCreator();
        this.labeledGraphs = lhg.stream()
                .map(LabeledGraph::createLabeledVertexGraph)
                .collect(Collectors.toList());
    }

    @Override
    public Set<LabeledGraph<V, E, L>> queryIsomorphicSubgraphs(
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
                matches.add(this.createLabeledGraph(
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

        LabeledGraph<V, E, L> match = this.graphCreator.create();
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
