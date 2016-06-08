package nl.tue.win.dbt.algorithms.TimeIndices;

import nl.tue.win.dbt.algorithms.TimeIndices.candidatefilters.TinlaFilter;
import nl.tue.win.dbt.data.LabeledVersionGraph;

import java.io.Serializable;

public class Tinla implements TimeIndex, Serializable {
    @Override
    public <V, E, L> TinlaFilter<V, E, L> createCandidateFilter(LabeledVersionGraph<V, E, L> lvg) {
        throw new UnsupportedOperationException("Not yet implemented."); // TODO
    }
}
