package nl.tue.win.dbt.tests;

import nl.tue.win.dbt.data.LabeledGraph;
import nl.tue.win.dbt.data.LabeledHistoryGraph;
import nl.tue.win.dbt.parsers.DatasetParser;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Objects;

public class ReadTime<V, E, L> {
    private final String filename;
    private final DatasetParser<V, E, L> parser;

    private long startRead;
    private long endRead;

    private long startWrite;
    private long endWrite;

    private final LabeledHistoryGraph<LabeledGraph<V, E, L>, V, E, L> lhg;

    public ReadTime(String filename, DatasetParser<V, E, L> parser) {
        Objects.requireNonNull(filename);
        Objects.requireNonNull(parser);
        this.filename = filename;
        this.parser = parser;

        this.startRead= System.currentTimeMillis();
        this.lhg = this.parser.convertToHistoryGraph(this.filename);
        this.endRead = System.currentTimeMillis();

        this.startWrite = System.currentTimeMillis();
//        this.writeToFile("data/lhg.ser");
        this.endWrite = System.currentTimeMillis();
    }

    public String getFilename() {
        return this.filename;
    }

    public DatasetParser<V, E, L> getParser() {
        return this.parser;
    }

    public long getStartRead() {
        return this.startRead;
    }

    public long getEndRead() {
        return this.endRead;
    }

    public long getStartWrite() {
        return this.startWrite;
    }

    public long getEndWrite() {
        return this.endWrite;
    }

    public LabeledHistoryGraph<LabeledGraph<V, E, L>, V, E, L> getLhg() {
        return this.lhg;
    }

    public long calculateReadDelta() {
        return this.endRead - this.startRead;
    }

    public long calculateWriteDelta() {
        return this.endWrite - this.startWrite;
    }

    private void writeToFile(String filename) {
        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(this.lhg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
