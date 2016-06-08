package nl.tue.win.dbt.algorithms.TimeIndices.candidatefilters;

import nl.tue.win.dbt.data.LabeledGraph;

import java.io.Serializable;
import java.util.BitSet;
import java.util.Set;

public class TinlaFilter<V, E, L> implements CandidateFilter<V, E, L>, Serializable {
    @Override
    public Set<V> filterCandidates(
            final LabeledGraph<V, E, L> pattern,
            final V patternVertex,
            final BitSet intervals) {
        throw new UnsupportedOperationException("Not yet implemented."); // TODO
    }
}
