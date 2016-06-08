package nl.tue.win.dbt.algorithms;

import nl.tue.win.dbt.data.LabeledGraph;

import java.io.Serializable;
import java.util.Set;

public interface IsomorphicSubgraphFinder<V, E, L> extends Serializable {
    Set<LabeledGraph<V, E, L>> queryIsomorphicSubgraphs(
            final int timestamp,
            final LabeledGraph<V, E, L> pattern);
}
