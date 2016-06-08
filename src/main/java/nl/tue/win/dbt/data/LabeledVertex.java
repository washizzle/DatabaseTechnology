package nl.tue.win.dbt.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class LabeledVertex<V, L> implements Serializable {
    private final V data;
    private final Set<L> labels;

    public LabeledVertex(V data, Collection<? extends L> labels) {
        this.data = data;
        this.labels = new HashSet<>(labels);
    }

    public LabeledVertex(V data) {
        this.data = data;
        this.labels = new HashSet<>();
    }

    public V getData() {
        return data;
    }

    public Set<L> getLabels() {
        return labels;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LabeledVertex<?, ?> that = (LabeledVertex<?, ?>) o;

        if (data != null ? !data.equals(that.data) : that.data != null) return false;
        return labels != null ? labels.equals(that.labels) : that.labels == null;

    }

    @Override
    public int hashCode() {
        int result = data != null ? data.hashCode() : 0;
        result = 31 * result + (labels != null ? labels.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "LabeledVertex{" +
                "data=" + data +
                ", labels=" + labels +
                '}';
    }
}
