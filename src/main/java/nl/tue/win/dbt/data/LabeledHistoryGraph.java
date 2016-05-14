package nl.tue.win.dbt.data;

import com.google.common.collect.RangeSet;

import java.util.Set;
import java.util.stream.Collectors;

public class LabeledHistoryGraph <G extends LabeledGraph<V, E, L>, V, E, L> extends HistoryGraph<G, V, E> {
    public LabeledHistoryGraph(GraphCreator<E> graphCreator) {
        super(graphCreator);
    }

    public RangeSet<Integer> labelLifespan(L label) {
        return this.createLifespan(i -> this.getGraph(i).hasLabel(label));
    }

    public RangeSet<Integer> labelLifespan(V vertex, L label) {
        return this.createLifespan(i -> this.getGraph(i).hasLabel(vertex, label));
    }

    public Set<L> labelSet() {
        return this.stream()
                .flatMap(g -> g.labelSet().stream())
                .collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        return "LabeledHistoryGraph{}";
    }
}
