package nl.tue.win.dbt.tests;

import com.google.common.collect.RangeSet;
import nl.tue.win.dbt.algorithms.DurablePattern;
import nl.tue.win.dbt.data.LabeledGraph;
import nl.tue.win.dbt.data.Lifespan;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class QueryTime<V, E, L> {
    private final String filename;
    private final List<LabeledGraph<V, E, L>> patterns;
    private final List<RangeSet<Integer>> intervals;

    private final long startRead;
    private final long endRead;
    private final List<Long> collectiveRunningTimes; // Mod 2, start, end
    private final List<Long> continuousRunningTimes; // Mod 2, start, end

    private final List<Set<Lifespan<LabeledGraph<V, E, L>>>> collectiveResults;
    private final List<Set<Lifespan<LabeledGraph<V, E, L>>>> continuousResults;

    private final DurablePattern<V, E, L> dp;

    public QueryTime(String filename, List<LabeledGraph<V, E, L>> patterns) {
        this(filename, patterns, null);
    }

    public QueryTime(String filename, List<LabeledGraph<V, E, L>> patterns, List<RangeSet<Integer>> intervals) {
        Objects.requireNonNull(filename);
        Objects.requireNonNull(patterns);
        if(intervals != null && patterns.size() != intervals.size()) {
            throw new IllegalArgumentException("Expect same number of intervals, one for each pattern");
        }
        this.filename = filename;
        this.patterns = patterns;
        this.intervals = intervals;
        this.collectiveRunningTimes = new ArrayList<>();
        this.continuousRunningTimes = new ArrayList<>();
        this.collectiveResults = new ArrayList<>();
        this.continuousResults = new ArrayList<>();
        this.startRead = System.currentTimeMillis();
        this.dp = readFromFile(this.filename);
        this.endRead = System.currentTimeMillis();
        for(int i=0; i < this.patterns.size(); i++) {
            this.collectiveTime(i);
            this.continuousTime(i);
        }
    }

    public String getFilename() {
        return this.filename;
    }

    public List<LabeledGraph<V, E, L>> getPatterns() {
        return this.patterns;
    }

    public List<RangeSet<Integer>> getIntervals() {
        return this.intervals;
    }

    public long getStartRead() {
        return this.startRead;
    }

    public long getEndRead() {
        return this.endRead;
    }

    public List<Long> getCollectiveRunningTimes() {
        return this.collectiveRunningTimes;
    }

    public List<Long> getContinuousRunningTimes() {
        return this.continuousRunningTimes;
    }

    public List<Set<Lifespan<LabeledGraph<V, E, L>>>> getCollectiveResults() {
        return this.collectiveResults;
    }

    public List<Set<Lifespan<LabeledGraph<V, E, L>>>> getContinuousResults() {
        return this.continuousResults;
    }

    public DurablePattern<V, E, L> getDp() {
        return this.dp;
    }

    public long calculateReadDelta() {
        return this.endRead - this.startRead;
    }

    public List<Long> calculateCollectiveTimeDeltas() {
        return this.calculateTimeDeltas(this.collectiveRunningTimes);
    }

    public List<Long> calculateContinuousTimeDeltas() {
        return this.calculateTimeDeltas(this.continuousRunningTimes);
    }

    private List<Long> calculateTimeDeltas(List<Long> timestamps) {
        List<Long> timeDeltas = new ArrayList<>();
        for(int i=0; i < this.patterns.size(); i++) {
            timeDeltas.add(timestamps.get(i * 2 + 1) - timestamps.get(i * 2));
        }
        return timeDeltas;
    }

    private void collectiveTime(int index) {
        LabeledGraph<V, E, L> pattern = this.patterns.get(index);
        Set<Lifespan<LabeledGraph<V, E, L>>> results;
        this.collectiveRunningTimes.add(System.currentTimeMillis());
        if(this.intervals == null) {
            results = this.dp.queryMaximalCollectiveDurableGraphPattern(pattern);
        } else {
            results = this.dp.queryMaximalCollectiveDurableGraphPattern(pattern, this.intervals.get(index));
        }
        this.collectiveRunningTimes.add(System.currentTimeMillis());
        this.collectiveResults.add(results);
    }

    private void continuousTime(int index) {
        LabeledGraph<V, E, L> pattern = this.patterns.get(index);
        Set<Lifespan<LabeledGraph<V, E, L>>> results;
        this.continuousRunningTimes.add(System.currentTimeMillis());
        if(this.intervals == null) {
            results = this.dp.queryMaximalContinuousDurableGraphPattern(pattern);
        } else {
            results = this.dp.queryMaximalContinuousDurableGraphPattern(pattern, this.intervals.get(index));
        }
        this.continuousRunningTimes.add(System.currentTimeMillis());
        this.continuousResults.add(results);
    }

    @SuppressWarnings("unchecked")
    private <T> T readFromFile(String filename) {
        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            return (T) in.readObject();
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
