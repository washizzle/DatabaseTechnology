package nl.tue.win.dbt.tests;

import nl.tue.win.dbt.Configuration;
import nl.tue.win.dbt.algorithms.BaselineAlgorithm;
import nl.tue.win.dbt.data.LabeledGraph;
import nl.tue.win.dbt.data.LabeledHistoryGraph;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Objects;

public class BaseSetupTime<V, E, L> {
    private final String filename;
    private final LabeledHistoryGraph<LabeledGraph<V, E, L>, V, E, L> lhg;
    private final Configuration config;

    private final BaselineAlgorithm<V, E, L> baselineAlgo;

    private final long startBase;
    private final long endBase;
    private final long startWrite;
    private final long endWrite;

    public BaseSetupTime(String filename, LabeledHistoryGraph<LabeledGraph<V, E, L>, V, E, L> lhg) {
        this(filename, lhg, null);
    }

    public BaseSetupTime(String filename, LabeledHistoryGraph<LabeledGraph<V, E, L>, V, E, L> lhg, Configuration config) {
        Objects.requireNonNull(filename);
        Objects.requireNonNull(lhg);
        this.filename = filename;
        this.lhg = lhg;
        this.config = config;
        this.startBase = System.currentTimeMillis();
        if(config == null) {
             this.baselineAlgo = new BaselineAlgorithm<>(this.lhg);
        } else {
            this.baselineAlgo = new BaselineAlgorithm<>(this.lhg, this.config);
        }
        this.endBase = this.startWrite  = System.currentTimeMillis();
        this.writeToFile(this.filename);
        this.endWrite = System.currentTimeMillis();
    }

    public String getFilename() {
        return this.filename;
    }

    public LabeledHistoryGraph<LabeledGraph<V, E, L>, V, E, L> getLhg() {
        return this.lhg;
    }

    public Configuration getConfig() {
        return this.config;
    }

    public BaselineAlgorithm<V, E, L> getBaselineAlgo() {
        return this.baselineAlgo;
    }

    public long getStartBase() {
        return this.startBase;
    }

    public long getEndBase() {
        return this.endBase;
    }

    public long getStartWrite() {
        return this.startWrite;
    }

    public long getEndWrite() {
        return this.endWrite;
    }

    public long calculateBaseDelta() {
        return this.endBase - this.startBase;
    }

    public long calculateWriteDelta() {
        return this.endWrite - this.startWrite;
    }

    private void writeToFile(String filename) {
        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(this.baselineAlgo);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
