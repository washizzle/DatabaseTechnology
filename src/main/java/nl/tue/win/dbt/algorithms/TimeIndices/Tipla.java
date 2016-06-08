package nl.tue.win.dbt.algorithms.TimeIndices;

import nl.tue.win.dbt.algorithms.TimeIndices.candidatefilters.TiplaFilter;
import nl.tue.win.dbt.data.LabeledVersionGraph;

import java.io.Serializable;

public class Tipla implements TimeIndex, Serializable {
    @Override
    public <V, E, L> TiplaFilter<V, E, L> createCandidateFilter(LabeledVersionGraph<V, E, L> lvg) {
        throw new UnsupportedOperationException("Not yet implemented."); // TODO
    }
}
