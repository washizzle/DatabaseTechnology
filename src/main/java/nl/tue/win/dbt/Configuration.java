package nl.tue.win.dbt;

import nl.tue.win.dbt.algorithms.Intersection;
import nl.tue.win.dbt.algorithms.IsomorphicSubgraphFinder;
import nl.tue.win.dbt.algorithms.LabeledVertexSubgraphFinder;
import nl.tue.win.dbt.algorithms.LongestBitSequence;
import nl.tue.win.dbt.algorithms.TimeIndices.Ctinla;
import nl.tue.win.dbt.algorithms.IsomorphicSubgraphFinderCreator;
import nl.tue.win.dbt.algorithms.TimeIndices.TimeIndex;
import nl.tue.win.dbt.algorithms.TimeIndices.candidatefilters.CandidateFilter;
import nl.tue.win.dbt.data.LabeledGraph;
import nl.tue.win.dbt.data.LabeledHistoryGraph;
import nl.tue.win.dbt.data.LabeledVersionGraph;

import java.io.Serializable;
import java.util.BitSet;
import java.util.Objects;
import java.util.Set;

public class Configuration implements Intersection, IsomorphicSubgraphFinderCreator,
        LongestBitSequence, TimeIndex, Serializable {
    private Intersection intersection;
    private IsomorphicSubgraphFinderCreator isfCreator;
    private LongestBitSequence lbs;
    private TimeIndex ti;

    public Configuration() {
        this.intersection = new Intersection.SmallestContainsIntersection();
        this.ti = new Ctinla();
        this.lbs = new SimpleLongestBitSequence();
        this.isfCreator = LabeledVertexSubgraphFinder::new;
    }

    public Intersection getIntersection() {
        return intersection;
    }

    public void setIntersection(Intersection intersection) {
        Objects.requireNonNull(intersection);
        this.intersection = intersection;
    }

    public IsomorphicSubgraphFinderCreator getIsfCreator() {
        return this.isfCreator;
    }

    public void setIsfCreator(IsomorphicSubgraphFinderCreator isfCreator) {
        Objects.requireNonNull(isfCreator);
        this.isfCreator = isfCreator;
    }

    public LongestBitSequence getLbs() {
        return lbs;
    }

    public void setLbs(LongestBitSequence lbs) {
        Objects.requireNonNull(lbs);
        this.lbs = lbs;
    }

    public TimeIndex getTi() {
        return ti;
    }

    public void setTi(TimeIndex ti) {
        Objects.requireNonNull(ti);
        this.ti = ti;
    }

    @Override
    public <T> Set<T> intersect(Set<T> set1, Set<T> set2) {
        return intersection.intersect(set1, set2);
    }

    @Override
    public int longestSetBits(BitSet bs) {
        return this.lbs.longestSetBits(bs);
    }

    @Override
    public int longestUnsetBits(BitSet bs) {
        return this.lbs.longestUnsetBits(bs);
    }

    @Override
    public <V, E, L> CandidateFilter<V, E, L> createCandidateFilter(
            final LabeledVersionGraph<V, E, L> lvg) {
        return this.ti.createCandidateFilter(lvg);
    }

    @Override
    public <V, E, L> IsomorphicSubgraphFinder<V, E, L> createIsomorphicSubgraphFinder(
            final LabeledHistoryGraph<LabeledGraph<V, E, L>, V, E, L> lhg) {
        return isfCreator.createIsomorphicSubgraphFinder(lhg);
    }
}
