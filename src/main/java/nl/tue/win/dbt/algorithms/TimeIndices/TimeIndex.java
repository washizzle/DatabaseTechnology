package nl.tue.win.dbt.algorithms.TimeIndices;

import nl.tue.win.dbt.data.LabeledGraph;
import nl.tue.win.dbt.data.LabeledVersionGraph;

import java.util.BitSet;
import java.util.Set;

public interface TimeIndex {
    <V, E, L> Set<V> filterCandidates(
            LabeledVersionGraph<V, E, L> lvg,
            LabeledGraph<V, E, L> pattern,
            V patternVertex,
            BitSet intervals);
}
