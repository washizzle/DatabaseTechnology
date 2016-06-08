package nl.tue.win.dbt.data;

import org.jgrapht.EdgeFactory;
import org.jgrapht.Graph;

import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

public class GraphDecorator<V, E> implements Graph<V,E>, Serializable {
    private final Graph<V, E> impl;

    public GraphDecorator(Graph<V, E> impl) {
        Objects.requireNonNull(impl);
        this.impl = impl;
    }

    public Graph<V, E> getImpl() {
        return this.impl;
    }

    @Override
    public Set<E> getAllEdges(V v, V v1) {
        return impl.getAllEdges(v, v1);
    }

    @Override
    public E getEdge(V v, V v1) {
        return impl.getEdge(v, v1);
    }

    @Override
    public EdgeFactory<V, E> getEdgeFactory() {
        return impl.getEdgeFactory();
    }

    @Override
    public E addEdge(V v, V v1) {
        return impl.addEdge(v, v1);
    }

    @Override
    public boolean addEdge(V v, V v1, E e) {
        return impl.addEdge(v, v1, e);
    }

    @Override
    public boolean addVertex(V v) {
        return impl.addVertex(v);
    }

    @Override
    public boolean containsEdge(V v, V v1) {
        return impl.containsEdge(v, v1);
    }

    @Override
    public boolean containsEdge(E e) {
        return impl.containsEdge(e);
    }

    @Override
    public boolean containsVertex(V v) {
        return impl.containsVertex(v);
    }

    @Override
    public Set<E> edgeSet() {
        return impl.edgeSet();
    }

    @Override
    public Set<E> edgesOf(V v) {
        return impl.edgesOf(v);
    }

    @Override
    public boolean removeAllEdges(Collection<? extends E> collection) {
        return impl.removeAllEdges(collection);
    }

    @Override
    public Set<E> removeAllEdges(V v, V v1) {
        return impl.removeAllEdges(v, v1);
    }

    @Override
    public boolean removeAllVertices(Collection<? extends V> collection) {
        return impl.removeAllVertices(collection);
    }

    @Override
    public E removeEdge(V v, V v1) {
        return impl.removeEdge(v, v1);
    }

    @Override
    public boolean removeEdge(E e) {
        return impl.removeEdge(e);
    }

    @Override
    public boolean removeVertex(V v) {
        return impl.removeVertex(v);
    }

    @Override
    public Set<V> vertexSet() {
        return impl.vertexSet();
    }

    @Override
    public V getEdgeSource(E e) {
        return impl.getEdgeSource(e);
    }

    @Override
    public V getEdgeTarget(E e) {
        return impl.getEdgeTarget(e);
    }

    @Override
    public double getEdgeWeight(E e) {
        return impl.getEdgeWeight(e);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GraphDecorator<?, ?> that = (GraphDecorator<?, ?>) o;

        return impl != null ? impl.equals(that.impl) : that.impl == null;
    }

    @Override
    public int hashCode() {
        return impl != null ? impl.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "GraphDecorator{" +
                "impl=" + impl +
                '}';
    }
}
