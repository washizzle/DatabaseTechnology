package nl.tue.win.dbt.data;

import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import nl.tue.win.dbt.util.IntegerRanges;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;

public class HistoryGraph<G extends Graph<V, E>, V, E> extends ArrayList<G> implements Serializable{

    private final GraphCreator<G, V, E> graphCreator;

    public HistoryGraph(GraphCreator<G, V, E> graphCreator) {
        Objects.requireNonNull(graphCreator);
        this.graphCreator = graphCreator;
    }

    public boolean containsEdge(E edge) {
        return this.stream()
                .anyMatch(g -> g.containsEdge(edge));
    }

    public boolean containsEdge(V sourceVertex, V targetVertex) {
        return this.stream()
                .anyMatch(g -> g.containsEdge(sourceVertex, targetVertex));
    }

    public boolean containsVertex(V vertex) {
        return this.stream()
                .anyMatch(g -> g.containsVertex(vertex));
    }

    public Set<E> edgeSet() {
        return this.stream()
                .flatMap(g -> g.edgeSet().stream())
                .collect(Collectors.toSet());
    }

    public Set<E> edgesOf(V vertex) {
        return this.stream()
                .flatMap(g -> g.edgesOf(vertex).stream())
                .collect(Collectors.toSet());
    }

    public Set<E> getAllEdges(V sourceVertex, V targetVertex) {
        return this.stream()
                .flatMap(g -> g.getAllEdges(sourceVertex, targetVertex).stream())
                .collect(Collectors.toSet());
    }

    public Set<V> vertexSet() {
        return this.stream()
                .flatMap(g -> g.vertexSet().stream())
                .collect(Collectors.toSet());
    }

    public E getEdge(V sourceVertex, V targetVertex) {
        return this.stream()
                .map(g -> g.getEdge(sourceVertex, targetVertex))
                .filter(e -> e != null)
                .findAny()
                .orElse(null);
    }

    public V getEdgeSource(E edge) {
        return this.stream()
                .map(g -> g.getEdgeSource(edge))
                .filter(e -> e != null)
                .findAny()
                .orElse(null);
    }

    public V getEdgeTarget(E edge) {
        return this.stream()
                .map(g -> g.getEdgeTarget(edge))
                .filter(e -> e != null)
                .findAny()
                .orElse(null);
    }

    public G addGraph() {
        G graph = this.graphCreator.create();
        boolean modified = this.add(graph);
        if(!modified) {
            graph = null;
        }
        return graph;
    }

    public void addGraph(int index) {
        this.add(index, this.graphCreator.create());
    }

    public boolean addGraph(G graph) {
        return this.add(graph);
    }

    public void addGraph(int index, G graph) {
        this.add(index, graph);
    }

    public G getGraph(int index) {
        return this.get(index);
    }

    public boolean removeGraph(G graph) {
        return this.remove(graph);
    }

    public G removeGraph(int index) {
        return this.remove(index);
    }

    public RangeSet<Integer> lifespan() {
        RangeSet<Integer> range = TreeRangeSet.create();
        if (this.size() > 0) {
            range.add(IntegerRanges.closed(0, this.size() - 1));
        }
        return range;
    }

    public RangeSet<Integer> vertexLifespan(V vertex) {
        return createLifespan(i -> this.getGraph(i).containsVertex(vertex));
    }

    public RangeSet<Integer> edgeLifespan(E edge) {
        return createLifespan(i -> this.getGraph(i).containsEdge(
                this.getGraph(i).getEdgeSource(edge),
                this.getGraph(i).getEdgeTarget(edge)));
    }

    public RangeSet<Integer> edgeLifespan(V sourceVertex, V targetVertex) {
        return createLifespan(i -> this.getGraph(i).containsEdge(sourceVertex, targetVertex));
    }

    public final RangeSet<Integer> createLifespan(IntPredicate intPredicate) {
        RangeSet<Integer> ranges = TreeRangeSet.create();
        int start = size();
        boolean constructingRange = false;
        boolean alive;
        for(int i = 0; i < size(); i++) {
            alive = intPredicate.test(i);
            if(!constructingRange && alive) {
                // Start new range.
                start = i;
                constructingRange = true;
            } else if(constructingRange && !alive) {
                // Found a maximum range.
                ranges.add(IntegerRanges.closed(start, i-1));
                constructingRange = false;
            }
        }
        if(constructingRange && start < size()) {
            // Close the last range.
            ranges.add(IntegerRanges.closed(start, size() - 1));
        }
        return ranges;
    }

    public GraphCreator<G, V, E> getGraphCreator() {
        return this.graphCreator;
    }

    public G flatten() {
        G graph = this.graphCreator.create();
        this.stream()
                .forEach(g -> Graphs.addGraph(graph, g));
        return graph;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        HistoryGraph<?, ?, ?> that = (HistoryGraph<?, ?, ?>) o;

        return graphCreator != null ? graphCreator.equals(that.graphCreator) : that.graphCreator == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (graphCreator != null ? graphCreator.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "HistoryGraph{" +
                "graphCreator=" + graphCreator +
                '}';
    }
}
