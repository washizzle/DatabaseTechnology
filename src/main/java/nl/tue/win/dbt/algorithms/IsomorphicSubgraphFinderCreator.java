package nl.tue.win.dbt.algorithms;

import nl.tue.win.dbt.data.LabeledGraph;
import nl.tue.win.dbt.data.LabeledHistoryGraph;

import java.io.Serializable;

public interface IsomorphicSubgraphFinderCreator extends Serializable {
    <V, E, L> IsomorphicSubgraphFinder<V, E, L>
    createIsomorphicSubgraphFinder(
            LabeledHistoryGraph<LabeledGraph<V, E, L>, V, E, L> lhg);
}
