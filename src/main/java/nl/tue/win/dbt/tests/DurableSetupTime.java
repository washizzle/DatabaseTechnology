package nl.tue.win.dbt.tests;

import nl.tue.win.dbt.Configuration;
import nl.tue.win.dbt.algorithms.DurablePatternAlgorithm;
import nl.tue.win.dbt.data.GraphCreator;
import nl.tue.win.dbt.data.LabeledGraph;
import nl.tue.win.dbt.data.LabeledVersionGraph;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Objects;

public class DurableSetupTime<V, E, L> {
    private final String filename;
    private final Configuration config;

    private final LabeledVersionGraph<V, E, L> lvg;
    private final GraphCreator<LabeledGraph<V, E, L>, V, E> graphCreator;
    private final DurablePatternAlgorithm<V, E, L> durableAlgo;

    private final long startDurable; // Includes time index
    private final long endDurable; // Includes time index
    private final long startWrite;
    private final long endWrite;

    public DurableSetupTime(String filename, LabeledVersionGraph<V, E, L> lvg, GraphCreator<LabeledGraph<V, E, L>, V, E> graphCreator) {
        this(filename, lvg, graphCreator, null);
    }

    public DurableSetupTime(String filename, LabeledVersionGraph<V, E, L> lvg, GraphCreator<LabeledGraph<V, E, L>, V, E> graphCreator, Configuration config) {
        Objects.requireNonNull(filename);
        Objects.requireNonNull(lvg);
        Objects.requireNonNull(graphCreator);
        this.filename = filename;
        this.lvg = lvg;
        this.graphCreator = graphCreator;
        this.config = config;

        this.startDurable = System.currentTimeMillis();
        if(config == null) {
            this.durableAlgo = new DurablePatternAlgorithm<>(
                    this.lvg, this.graphCreator);
        } else {
            this.durableAlgo = new DurablePatternAlgorithm<>(
                    this.lvg, this.graphCreator, this.config);
        }
        this.endDurable = this.startWrite  = System.currentTimeMillis();
        this.writeToFile(this.filename);
        this.endWrite = System.currentTimeMillis();
    }

    public String getFilename() {
        return this.filename;
    }

    public Configuration getConfig() {
        return this.config;
    }

    public LabeledVersionGraph<V, E, L> getLvg() {
        return this.lvg;
    }

    public GraphCreator<LabeledGraph<V, E, L>, V, E> getGraphCreator() {
        return graphCreator;
    }

    public DurablePatternAlgorithm<V, E, L> getDurableAlgo() {
        return this.durableAlgo;
    }

    public long getStartDurable() {
        return this.startDurable;
    }

    public long getEndDurable() {
        return this.endDurable;
    }

    public long getStartWrite() {
        return this.startWrite;
    }

    public long getEndWrite() {
        return this.endWrite;
    }

    public long calculateDurableDelta() {
        return this.endDurable - this.startDurable;
    }

    public long calculateWriteDelta() {
        return this.endWrite - this.startWrite;
    }

    private void writeToFile(String filename) {
        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(this.durableAlgo);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
