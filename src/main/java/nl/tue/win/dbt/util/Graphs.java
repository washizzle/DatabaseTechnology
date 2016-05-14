package nl.tue.win.dbt.util;

import nl.tue.win.dbt.data.GraphDecorator;
import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;

import java.util.Set;
import java.util.stream.Collectors;

public class Graphs {
    private Graphs() {
        // Do not construct utility class.
    }

    public static <V, E> Set<E> getTraversableEdges(Graph<V, E> graph, V vertex) {
        // Code smell due to bad design JGraphT. See CrossComponentIterator.createGraphSpecifics
        // https://github.com/jgrapht/jgrapht/blob/master/jgrapht-core/src/main/java/org/jgrapht/traverse/CrossComponentIterator.java#L339
        while(graph instanceof GraphDecorator) {
            graph = ((GraphDecorator<V, E>) graph).getImpl();
        }
        Set<E> outgoingEdges;
        if(graph instanceof DirectedGraph) {
            outgoingEdges = ((DirectedGraph<V, E>) graph).outgoingEdgesOf(vertex);
        } else {
            outgoingEdges = graph.edgesOf(vertex);
        }
        return outgoingEdges;
    }

    public static <V, E> Set<V> getNextNeighbors(Graph<V, E> graph, V vertex) {
        return getTraversableEdges(graph, vertex).stream()
                .map(e -> org.jgrapht.Graphs.getOppositeVertex(graph, e, vertex))
                .collect(Collectors.toSet());
    }
}
