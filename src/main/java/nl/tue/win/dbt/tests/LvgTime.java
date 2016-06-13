package nl.tue.win.dbt.tests;

import nl.tue.win.dbt.data.GraphCreator;
import nl.tue.win.dbt.data.LabeledGraph;
import nl.tue.win.dbt.data.LabeledHistoryGraph;
import nl.tue.win.dbt.data.LabeledVersionGraph;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Objects;

public class LvgTime<V, E, L> {
    private final LabeledHistoryGraph<LabeledGraph<V, E, L>, V, E, L> lhg;

    private final LabeledVersionGraph<V, E, L> lvg;
    private final GraphCreator<LabeledGraph<V, E, L>, V, E> graphCreator;

    private final long startLvg;
    private final long endLvg;
    private final long startWrite;
    private final long endWrite;

    public LvgTime(LabeledHistoryGraph<LabeledGraph<V, E, L>, V, E, L> lhg) {
        Objects.requireNonNull(lhg);
        this.lhg = lhg;
        this.graphCreator = lhg.getGraphCreator();

        this.startLvg = System.currentTimeMillis();
        this.lvg = new LabeledVersionGraph<>(this.lhg);
        this.endLvg = this.startWrite  = System.currentTimeMillis();
//        this.writeToFile("data/lvg.ser");
        this.endWrite = System.currentTimeMillis();
    }

    public LabeledHistoryGraph<LabeledGraph<V, E, L>, V, E, L> getLhg() {
        return this.lhg;
    }

    public LabeledVersionGraph<V, E, L> getLvg() {
        return this.lvg;
    }

    public long getStartLvg() {
        return this.startLvg;
    }

    public long getEndLvg() {
        return this.endLvg;
    }

    public long getStartWrite() {
        return this.startWrite;
    }

    public long getEndWrite() {
        return this.endWrite;
    }

    public long calculateLvgDelta() {
        return this.endLvg - this.startLvg;
    }

    public long calculateWriteDelta() {
        return this.endWrite - this.startWrite;
    }

    private void writeToFile(String filename) {
        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(this.lvg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
