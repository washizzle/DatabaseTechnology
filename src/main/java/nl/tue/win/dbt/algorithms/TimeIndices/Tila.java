package nl.tue.win.dbt.algorithms.TimeIndices;

import nl.tue.win.dbt.data.LabeledGraph;
import nl.tue.win.dbt.data.LabeledVersionGraph;

import java.util.BitSet;
import java.util.Set;

public class Tila implements TimeIndex {
    @Override
    public <V, E, L> Set<V> filterCandidates(
            final LabeledVersionGraph<V, E, L> lvg,
            final LabeledGraph<V, E, L> pattern,
            final V patternVertex,
            final BitSet intervals) {
        throw new UnsupportedOperationException("Not yet implemented."); // TODO
    }
}
