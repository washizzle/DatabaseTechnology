package nl.tue.win.dbt.algorithms.TimeIndices;

import nl.tue.win.dbt.algorithms.TimeIndices.candidatefilters.CandidateFilter;
import nl.tue.win.dbt.data.LabeledVersionGraph;

import java.io.Serializable;

public interface TimeIndex extends Serializable {
    <V, E, L> CandidateFilter<V, E, L> createCandidateFilter(LabeledVersionGraph<V, E, L> lvg);
}
