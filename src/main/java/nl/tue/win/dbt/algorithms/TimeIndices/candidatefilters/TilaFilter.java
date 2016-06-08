package nl.tue.win.dbt.algorithms.TimeIndices.candidatefilters;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import nl.tue.win.dbt.data.LabeledGraph;
import nl.tue.win.dbt.data.LabeledVersionGraph;

import java.util.*;

public class TilaFilter<V, E, L> implements CandidateFilter<V, E, L> {
    private final LabeledVersionGraph<V, E, L> lvg;
    private final List<SetMultimap<L, V>> tila;

    public TilaFilter(LabeledVersionGraph<V, E, L> lvg) {
        Objects.requireNonNull(lvg);
        this.lvg = lvg;
        this.tila = new ArrayList<>(this.lvg.getSize());
        fillTila();
    }

    private void fillTila() {
        for(int i = 0; i < this.lvg.getSize(); i++) {
            this.tila.add(HashMultimap.create());
            for (V vertex : this.lvg.vertexSet()) {
                for (L label : this.lvg.getLabels(vertex)) {
                    if (this.lvg.labelLifespan(vertex, label).get(i)) {
                        this.tila.get(i).put(label, vertex);
                    }
                }
            }
        }
    }

    @Override
    public Set<V> filterCandidates(
            final LabeledGraph<V, E, L> pattern,
            final V patternVertex,
            final BitSet intervals) {
        Set<V> candidates = new HashSet<>();
        Set<L> labels = pattern.getLabels(patternVertex);
        for (int i = intervals.nextSetBit(0); i >= 0; i = intervals.nextSetBit(i+1)) {
            this.updateCandidates(i, labels, candidates);
        }
        return candidates;
    }

    private void updateCandidates(int index, Set<L> labels, Set<V> candidates) {
        SetMultimap<L, V> labelVertices = this.tila.get(index);
        if(labelVertices.keySet().containsAll(labels)) {
            Set<V> moreCandidates = new HashSet<>(this.lvg.vertexSet());
            for(L label: labels) {
                moreCandidates.retainAll(labelVertices.get(label));
            }
            candidates.addAll(moreCandidates);
        }
    }
}
