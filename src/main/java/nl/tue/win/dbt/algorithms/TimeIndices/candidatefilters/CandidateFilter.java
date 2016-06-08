package nl.tue.win.dbt.algorithms.TimeIndices.candidatefilters;

import nl.tue.win.dbt.data.LabeledGraph;

import java.io.Serializable;
import java.util.BitSet;
import java.util.Set;

public interface CandidateFilter<V, E, L> extends Serializable {
    Set<V> filterCandidates(
            LabeledGraph<V, E, L> pattern,
            V patternVertex,
            BitSet intervals);
}
