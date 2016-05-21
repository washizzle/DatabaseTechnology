package nl.tue.win.dbt.data;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import org.jgrapht.Graph;

import java.util.*;
import java.util.stream.Collectors;

public class LabeledGraph<V, E, L> extends GraphDecorator<V, E> implements Graph<V, E> {
    private final SetMultimap<V, L> labelsMap;
    private final GraphCreator<Graph<LabeledVertex<V, L>, E>, LabeledVertex<V, L>, E> graphCreator;

    public LabeledGraph(
            Graph<V, E> graph,
            GraphCreator<Graph<LabeledVertex<V, L>, E>, LabeledVertex<V, L>, E> graphCreator) {
        super(graph);
        // graphCreator should create graphs with exact same type as graph.
        // It is not possible to check this at compile time.
        Objects.requireNonNull(graphCreator);
        this.graphCreator = graphCreator;
        this.labelsMap = HashMultimap.create();
    }

    protected LabeledGraph(LabeledGraph<V, E, L> labeledGraph) {
        super(labeledGraph.getImpl());
        this.graphCreator = labeledGraph.graphCreator;
        this.labelsMap = HashMultimap.create(labeledGraph.labelsMap);
    }

    public boolean addLabel(V vertex, L label) {
        boolean modified = false;
        if(this.containsVertex(vertex)) {
            modified = this.labelsMap.put(vertex, label);
        }
        return modified;
    }

    public boolean addAllLabels(V vertex, Collection<? extends L> labels) {
        Objects.requireNonNull(labels);
        boolean modified = false;
        if(this.containsVertex(vertex)) {
            modified = this.labelsMap.putAll(vertex, labels);
        }
        return modified;
    }

    public boolean removeLabel(V vertex, L label) {
        return this.labelsMap.remove(vertex, label);
    }

    public Set<L> removeAllLabels(V vertex) {
        return this.labelsMap.removeAll(vertex);
    }

    public void removeAllLabels() {
        this.labelsMap.clear();
    }

    public Set<L> getLabels(V vertex) {
        return this.labelsMap.get(vertex);
    }

    public Set<L> getAllLabels() {
        return this.labelsMap.values()
                .stream()
                .collect(Collectors.toSet());
    }

    public Set<L> labelSet() {
        return getAllLabels();
    }

    public boolean hasLabel(V vertex, L label) {
        return this.labelsMap.containsEntry(vertex, label);
    }

    public boolean hasLabel(L label) {
        return this.labelsMap.containsValue(label);
    }

    public boolean hasLabels() {
        return this.labelsMap.isEmpty();
    }

    public Graph<LabeledVertex<V, L>, E> createLabeledVertexGraph() {
        // If graphCreator creates a graph of a different type than this.getImpl(),
        // then the return value might represent a different graph.
        // It is not possible to do a check at compile time.
        Graph<LabeledVertex<V, L>, E> labeledVertexGraph = this.graphCreator.create();

        Map<V, LabeledVertex<V, L>> lvs = new HashMap<>();

        LabeledVertex<V, L> lv;
        for(V vertex: this.vertexSet()) {
            lv = new LabeledVertex<>(vertex, this.getLabels(vertex));
            lvs.put(vertex, lv);
            labeledVertexGraph.addVertex(lv);
        }
        V source;
        V target;
        for(E edge: this.edgeSet()) {
            source = this.getEdgeSource(edge);
            target = this.getEdgeTarget(edge);
            labeledVertexGraph.addEdge(lvs.get(source), lvs.get(target));
        }
        return labeledVertexGraph;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LabeledGraph<?, ?, ?> that = (LabeledGraph<?, ?, ?>) o;

        if (labelsMap != null ? !labelsMap.equals(that.labelsMap) : that.labelsMap != null) return false;
        return graphCreator != null ? graphCreator.equals(that.graphCreator) : that.graphCreator == null;

    }

    @Override
    public int hashCode() {
        int result = labelsMap != null ? labelsMap.hashCode() : 0;
        result = 31 * result + (graphCreator != null ? graphCreator.hashCode() : 0);
        return result;
    }
}
