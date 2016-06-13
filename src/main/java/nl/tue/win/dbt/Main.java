package nl.tue.win.dbt;

import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import nl.tue.win.dbt.algorithms.BaselineAlgorithm;
import nl.tue.win.dbt.algorithms.DurablePatternAlgorithm;
import nl.tue.win.dbt.data.Edge;
import nl.tue.win.dbt.data.LabeledGraph;
import nl.tue.win.dbt.data.LabeledHistoryGraph;
import nl.tue.win.dbt.data.Lifespan;
import nl.tue.win.dbt.util.IntegerRanges;
import org.jgrapht.graph.DefaultDirectedGraph;

import java.io.*;
import java.util.Set;

/**
 * Hello World!
 *
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("Hello World!");
        LabeledGraph<Integer, Edge, String> pattern;
        pattern = createGraph();
        addPattern(pattern, 100);

        LabeledHistoryGraph<
                LabeledGraph<Integer, Edge, String>,
                Integer,
                Edge,
                String> lhg;
        lhg = createLabeledHistoryGraph();

        writeToFile(lhg, "data/lhg.ser");
        lhg = readFromFile("data/lhg.ser");

        RangeSet<Integer> intervals = TreeRangeSet.create();
        intervals.add(IntegerRanges.closed(1, 5));

        BaselineAlgorithm<Integer, Edge, String> base = new BaselineAlgorithm<>(lhg);
        writeToFile(base, "data/base.ser");
        base = readFromFile("data/base.ser");

        DurablePatternAlgorithm<Integer, Edge, String> dur = new DurablePatternAlgorithm<>(lhg);
        writeToFile(dur, "data/dur.ser");
        dur = readFromFile("data/dur.ser");

        Set<Lifespan<LabeledGraph<Integer, Edge, String>>> baselineCon;
        baselineCon = base.queryMaximalContinuousDurableGraphPattern(pattern, intervals);
        Set<Lifespan<LabeledGraph<Integer, Edge, String>>> baselineCol;
        baselineCol = base.queryMaximalCollectiveDurableGraphPattern(pattern, intervals);

        Set<Lifespan<LabeledGraph<Integer, Edge, String>>> algoCon;
        algoCon = dur.queryMaximalContinuousDurableGraphPattern(pattern, intervals);
        Set<Lifespan<LabeledGraph<Integer, Edge, String>>> algoCol;
        algoCol = dur.queryMaximalCollectiveDurableGraphPattern(pattern, intervals);

        System.out.println("Pattern");
        System.out.println(pattern);

        System.out.println("Continuous");
        System.out.println(baselineCon);
        System.out.println(algoCon);

        System.out.println("Collective");
        System.out.println(baselineCol);
        System.out.println(algoCol);
    }

    private static LabeledHistoryGraph<
            LabeledGraph<Integer, Edge, String>,
            Integer,
            Edge,
            String> createLabeledHistoryGraph() {
        LabeledGraph<Integer, Edge, String> g0, g1, g2, g3, g4, g5;

        g0 = createGraph();
        g0.addVertex(666);
        g0.addVertex(42);
        g0.addLabel(42, "ABC");
        g0.addLabel(42, "X");

        g1 = createGraph();
        g1.addVertex(666);
        g1.addVertex(42);
        g1.addEdge(666, 42);
        g1.addLabel(666, "ABC");
        g1.addLabel(42, "ABC");
        addPattern(g1);

        g2 = createGraph();
        g2.addVertex(666);
        g2.addVertex(42);
        g2.addEdge(666, 42);
        g2.addLabel(666, "ABC");
        g2.addLabel(42, "ABC");
        addPattern(g2);

        g3 = createGraph();
        addPattern(g3);

        g4 = createGraph();
        g4.addVertex(666);
        g4.addVertex(42);
        g4.addEdge(666, 42);
        g4.addLabel(666, "ABC");
        g4.addLabel(42, "ABC");

        g5 = createGraph();
        g5.addVertex(666);
        g5.addVertex(42);
        g5.addLabel(42, "ABC");
        g5.addLabel(666, "X");
        addPattern(g5);

        LabeledHistoryGraph<
                LabeledGraph<Integer, Edge, String>,
                Integer,
                Edge,
                String> lhg;
        lhg = new LabeledHistoryGraph<>(Main::createGraph);

        lhg.addGraph(g0);
        lhg.addGraph(g1);
        lhg.addGraph(g2);
        lhg.addGraph(g3);
        lhg.addGraph(g4);
        lhg.addGraph(g5);

        return lhg;
    }

    private static LabeledGraph<Integer, Edge, String> createGraph() {
        return new LabeledGraph<>(
                new DefaultDirectedGraph<>(Edge.class),
                () -> new DefaultDirectedGraph<>(Edge.class));
    }

    private static void addPattern(LabeledGraph<Integer, Edge, String> graph, int offset) {
        for (int i = 1; i < 5; i++) {
            graph.addVertex(i + offset);
        }
        graph.addEdge(1 + offset, 2 + offset);
        graph.addEdge(2 + offset, 3 + offset);
        graph.addEdge(3 + offset, 1 + offset);
        graph.addEdge(4 + offset, 1 + offset);

        graph.addLabel(1 + offset, "A");
        graph.addLabel(2 + offset, "B");
        graph.addLabel(3 + offset, "C");
        graph.addLabel(4 + offset, "X");
        graph.addLabel(5 + offset, "Y");
    }

    private static void addPattern(LabeledGraph<Integer, Edge, String> graph) {
        addPattern(graph, 0);
    }

    private static <T> void writeToFile(T t, String filename) {
        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(t);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T readFromFile(String filename) {
        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            return (T) in.readObject();
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
