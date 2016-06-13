package nl.tue.win.dbt.data;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.RangeSet;
import com.google.common.collect.Table;
import nl.tue.win.dbt.util.IntegerRangeSets;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;

public class LabeledVersionGraph<V, E, L> extends LabeledGraph<V, E, L> implements Serializable {
    private final int size;

    private final Map<V, BitSet> vertexLifespans;
    private final Map<E, BitSet> edgeLifespans;
    private final Table<V, L, BitSet> labelLifespans;

    public LabeledVersionGraph(int size) {
        super(null); // TODO
        if(size <= 0) {
            throw new IllegalArgumentException("An LVG requires a positive size");
        }
        this.size = size;
        this.vertexLifespans = new HashMap<>();
        this.edgeLifespans = new HashMap<>();
        this.labelLifespans = HashBasedTable.create();
    }

    public <G extends LabeledGraph<V, E, L>> LabeledVersionGraph(
            final LabeledHistoryGraph<G, V, E, L> historyGraph) {
        super(historyGraph.flatten());
        this.size = historyGraph.size();

        this.vertexLifespans = new HashMap<>();
        this.initMapLifespans(
                this.vertexLifespans,
                super.vertexSet(),
                historyGraph::vertexLifespan);

        this.edgeLifespans = new HashMap<>();
        this.initMapLifespans(
                this.edgeLifespans,
                super.edgeSet(),
                historyGraph::edgeLifespan);

        this.labelLifespans = HashBasedTable.create();
        RangeSet<Integer> lifespan;
        BitSet bs;
        for(V vertex: this.vertexSet()) {
            for(L label: this.getLabels(vertex)) {
                lifespan = historyGraph.labelLifespan(vertex, label);
                bs = IntegerRangeSets.toBitSet(lifespan, this.size);
                this.labelLifespans.put(vertex, label, bs);
            }
        }
    }

    protected final <T> void initMapLifespans(
            final Map<T, BitSet> lifespans,
            final Set<T> data,
            final Function<T, RangeSet<Integer>> mapper) {
        RangeSet<Integer> lifespan;
        BitSet bs;
        for(T t: data) {
            lifespan = mapper.apply(t);
            bs = IntegerRangeSets.toBitSet(lifespan, this.size);
            lifespans.put(t, bs);
        }
    }

    public int getSize() {
        return this.size;
    }

    public BitSet vertexLifespan(V vertex) {
        return this.vertexLifespans.get(vertex);
    }

    public BitSet edgeLifespan(E edge) {
        return this.edgeLifespans.get(edge);
    }

    public BitSet labelLifespan(V vertex, L label) {
        return this.labelLifespans.get(vertex, label);
    }

    public BitSet changeVertexLifespan(V vertex, BitSet lifespan) {
        Objects.requireNonNull(lifespan);
        assertCorrectLifespanLength(lifespan);
        return this.vertexLifespans.put(vertex, lifespan);
    }

    public BitSet changeEdgeLifespan(E edge, BitSet lifespan) {
        Objects.requireNonNull(lifespan);
        assertCorrectLifespanLength(lifespan);
        return this.edgeLifespans.put(edge, lifespan);
    }

    public BitSet changeLabelLifespan(V vertex, L label, BitSet lifespan) {
        Objects.requireNonNull(lifespan);
        assertCorrectLifespanLength(lifespan);
        return this.labelLifespans.put(vertex, label, lifespan);
    }

    private void assertCorrectLifespanLength(BitSet lifespan) {
        if(lifespan.length() != this.size) {
            throw new IllegalArgumentException(
                    String.format("Expected a lifespan of length %s.", this.size));
        }
    }

    public E addEdge(V v, V v1, BitSet lifespan) {
        Objects.requireNonNull(lifespan);
        assertCorrectLifespanLength(lifespan);
        E edge = super.addEdge(v, v1);
        if(edge != null) {
            this.edgeLifespans.put(edge, lifespan);
        }
        return edge;
    }

    public boolean addEdge(V v, V v1, E e, BitSet lifespan) {
        Objects.requireNonNull(lifespan);
        assertCorrectLifespanLength(lifespan);
        boolean modified = super.addEdge(v, v1, e);
        if(modified) {
            this.edgeLifespans.put(e, lifespan);
        }
        return modified;
    }

    public boolean addVertex(V v, BitSet lifespan) {
        Objects.requireNonNull(lifespan);
        assertCorrectLifespanLength(lifespan);
        boolean modified = super.addVertex(v);
        if(modified) {
            this.vertexLifespans.put(v, lifespan);
        }
        return modified;
    }

    public boolean addLabel(V vertex, L label, BitSet lifespan) {
        Objects.requireNonNull(lifespan);
        assertCorrectLifespanLength(lifespan);
        boolean modified = super.addLabel(vertex, label);
        if(modified) {
            this.labelLifespans.put(vertex, label, new BitSet(this.size));
        }
        return modified;
    }

    public boolean addAllLabels(V vertex, Collection<? extends L> labels, BitSet lifespan) {
        Objects.requireNonNull(lifespan);
        assertCorrectLifespanLength(lifespan);
        boolean modified = super.addAllLabels(vertex, labels);
        if(modified) {
            labels.forEach(l -> this.labelLifespans.put(vertex, l, lifespan));
        }
        return modified;
    }

    @Override
    public boolean addLabel(V vertex, L label) {
        return this.addLabel(vertex, label, new BitSet(this.size));
    }

    @Override
    public boolean addAllLabels(V vertex, Collection<? extends L> labels) {
        return this.addAllLabels(vertex, labels, new BitSet(this.size));
    }

    @Override
    public boolean removeLabel(V vertex, L label) {
        this.labelLifespans.remove(vertex, label);
        return super.removeLabel(vertex, label);
    }

    @Override
    public Set<L> removeAllLabels(V vertex) {
        this.labelLifespans.row(vertex).clear();
        return super.removeAllLabels(vertex);
    }

    @Override
    public void removeAllLabels() {
        this.labelLifespans.clear();
        super.removeAllLabels();
    }

    @Override
    public E addEdge(V v, V v1) {
        return this.addEdge(v, v1, new BitSet(this.size));
    }

    @Override
    public boolean addEdge(V v, V v1, E e) {
        return this.addEdge(v, v1, e, new BitSet(this.size));
    }

    @Override
    public boolean addVertex(V v) {
        return this.addVertex(v, new BitSet(this.size));
    }

    @Override
    public boolean removeAllEdges(Collection<? extends E> collection) {
        collection.forEach(this.edgeLifespans::remove);
        return super.removeAllEdges(collection);
    }

    @Override
    public Set<E> removeAllEdges(V v, V v1) {
        this.getAllEdges(v, v1).forEach(this.edgeLifespans::remove);
        return super.removeAllEdges(v, v1);
    }

    @Override
    public boolean removeAllVertices(Collection<? extends V> collection) {
        for(V vertex: collection) {
            this.vertexLifespans.remove(vertex);
            this.edgesOf(vertex).forEach(this.edgeLifespans::remove);
            this.labelLifespans.row(vertex).clear();
        }
        return super.removeAllVertices(collection);
    }

    @Override
    public E removeEdge(V v, V v1) {
        this.edgeLifespans.remove(this.getEdge(v, v1));
        return super.removeEdge(v, v1);
    }

    @Override
    public boolean removeEdge(E e) {
        this.edgeLifespans.remove(e);
        return super.removeEdge(e);
    }

    @Override
    public boolean removeVertex(V v) {
        this.vertexLifespans.remove(v);
        this.edgesOf(v).forEach(this.edgeLifespans::remove);
        this.labelLifespans.row(v).clear();
        return super.removeVertex(v);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        LabeledVersionGraph<?, ?, ?> that = (LabeledVersionGraph<?, ?, ?>) o;

        if (size != that.size) return false;
        if (vertexLifespans != null ? !vertexLifespans.equals(that.vertexLifespans) : that.vertexLifespans != null)
            return false;
        if (edgeLifespans != null ? !edgeLifespans.equals(that.edgeLifespans) : that.edgeLifespans != null)
            return false;
        return labelLifespans != null ? labelLifespans.equals(that.labelLifespans) : that.labelLifespans == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + size;
        result = 31 * result + (vertexLifespans != null ? vertexLifespans.hashCode() : 0);
        result = 31 * result + (edgeLifespans != null ? edgeLifespans.hashCode() : 0);
        result = 31 * result + (labelLifespans != null ? labelLifespans.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "LabeledVersionGraph{" +
                "size=" + size +
                ", vertexLifespans=" + vertexLifespans +
                ", edgeLifespans=" + edgeLifespans +
                ", labelLifespans=" + labelLifespans +
                '}';
    }
}
