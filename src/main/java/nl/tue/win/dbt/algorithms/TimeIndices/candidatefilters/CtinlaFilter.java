package nl.tue.win.dbt.algorithms.TimeIndices.candidatefilters;

import nl.tue.win.dbt.data.LabeledGraph;
import nl.tue.win.dbt.data.LabeledVersionGraph;

import java.util.BitSet;
import java.util.Set;

public class CtinlaFilter<V, E, L> implements CandidateFilter<V, E, L> {
    private final LabeledGraph<V, E, L> lvg;

    public CtinlaFilter(LabeledVersionGraph<V, E, L> lvg) {
        this.lvg = lvg;
    }

    @Override
    public Set<V> filterCandidates(
            final LabeledGraph<V, E, L> pattern,
            final V patternVertex,
            final BitSet intervals) {
        return this.lvg.vertexSet();
    }
}
