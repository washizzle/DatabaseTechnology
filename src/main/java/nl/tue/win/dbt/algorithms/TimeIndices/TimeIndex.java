package nl.tue.win.dbt.algorithms.TimeIndices;

import nl.tue.win.dbt.algorithms.TimeIndices.candidatefilters.CandidateFilter;
import nl.tue.win.dbt.data.LabeledVersionGraph;

public interface TimeIndex {
    <V, E, L> CandidateFilter<V, E, L> createCandidateFilter(LabeledVersionGraph<V, E, L> lvg);
}
