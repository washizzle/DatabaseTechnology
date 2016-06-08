package nl.tue.win.dbt.algorithms;

import com.google.common.collect.RangeSet;
import nl.tue.win.dbt.data.LabeledGraph;
import nl.tue.win.dbt.data.Lifespan;

import java.util.Set;

public interface DurablePattern<V, E, L> {
    Set<Lifespan<LabeledGraph<V, E, L>>> queryMaximalCollectiveDurableGraphPattern(
            LabeledGraph<V, E, L> pattern);

    Set<Lifespan<LabeledGraph<V, E, L>>> queryMaximalCollectiveDurableGraphPattern(
            LabeledGraph<V, E, L> pattern,
            RangeSet<Integer> intervals);

    Set<Lifespan<LabeledGraph<V, E, L>>> queryMaximalContinuousDurableGraphPattern(
            LabeledGraph<V, E, L> pattern);

    Set<Lifespan<LabeledGraph<V, E, L>>> queryMaximalContinuousDurableGraphPattern(
            LabeledGraph<V, E, L> pattern,
            RangeSet<Integer> intervals);
}
