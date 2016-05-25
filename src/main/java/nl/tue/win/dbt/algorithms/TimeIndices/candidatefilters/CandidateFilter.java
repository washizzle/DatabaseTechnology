package nl.tue.win.dbt.algorithms.TimeIndices.candidatefilters;

import nl.tue.win.dbt.data.LabeledGraph;

import java.util.BitSet;
import java.util.Set;

public interface CandidateFilter<V, E, L> {
    Set<V> filterCandidates(
            LabeledGraph<V, E, L> pattern,
            V patternVertex,
            BitSet intervals);
}
