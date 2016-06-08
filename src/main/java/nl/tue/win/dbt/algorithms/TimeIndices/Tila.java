package nl.tue.win.dbt.algorithms.TimeIndices;

import nl.tue.win.dbt.algorithms.TimeIndices.candidatefilters.TilaFilter;
import nl.tue.win.dbt.data.LabeledVersionGraph;

public class Tila implements TimeIndex {
    @Override
    public <V, E, L> TilaFilter<V, E, L> createCandidateFilter(LabeledVersionGraph<V, E, L> lvg) {
        return new TilaFilter<>(lvg);
    }
}
