package nl.tue.win.dbt.algorithms.TimeIndices;

import nl.tue.win.dbt.algorithms.TimeIndices.candidatefilters.CtinlaFilter;
import nl.tue.win.dbt.data.LabeledVersionGraph;

public class Ctinla implements TimeIndex {
    @Override
    public <V, E, L> CtinlaFilter<V, E, L> createCandidateFilter(LabeledVersionGraph<V, E, L> lvg) {
        return new CtinlaFilter<>(lvg);
    }
}
