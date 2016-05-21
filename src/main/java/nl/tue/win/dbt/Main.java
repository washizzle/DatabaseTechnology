package nl.tue.win.dbt;

import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import nl.tue.win.dbt.algorithms.BaselineAlgorithm;
import nl.tue.win.dbt.algorithms.DurablePatternAlgorithm;
import nl.tue.win.dbt.data.LabeledGraph;
import nl.tue.win.dbt.data.LabeledHistoryGraph;
import nl.tue.win.dbt.data.Lifespan;
import nl.tue.win.dbt.util.IntegerRanges;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.Set;

/**
 * Hello World!
 *
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("Hello World!");
        LabeledGraph<Integer, DefaultEdge, String> pattern;
        pattern = createGraph();
        addPattern(pattern);

        LabeledHistoryGraph<
                LabeledGraph<Integer, DefaultEdge, String>,
                Integer,
                DefaultEdge,
                String> lhg;
        lhg = createLabeledHistoryGraph();

        RangeSet<Integer> intervals = TreeRangeSet.create();
        intervals.add(IntegerRanges.closed(1, 5));

        Set<Lifespan<LabeledGraph<Integer, DefaultEdge, String>>> baselineCon;
        baselineCon = BaselineAlgorithm.queryMaximalContinuousDurableGraphPattern(lhg, pattern, intervals);
        Set<Lifespan<LabeledGraph<Integer, DefaultEdge, String>>> baselineCol;
        baselineCol = BaselineAlgorithm.queryMaximalCollectiveDurableGraphPattern(lhg, pattern, intervals);

        Set<Lifespan<LabeledGraph<Integer, DefaultEdge, String>>> algoCon;
        algoCon = DurablePatternAlgorithm.queryMaximalContinuousDurableGraphPattern(lhg, pattern, intervals);
        Set<Lifespan<LabeledGraph<Integer, DefaultEdge, String>>> algoCol;
        algoCol = DurablePatternAlgorithm.queryMaximalCollectiveDurableGraphPattern(lhg, pattern, intervals);

        System.out.println("Continuous");
        System.out.println(baselineCon);
        System.out.println(algoCon);

        System.out.println("Collective");
        System.out.println(baselineCol);
        System.out.println(algoCol);
    }

    private static LabeledHistoryGraph<
            LabeledGraph<Integer, DefaultEdge, String>,
            Integer,
            DefaultEdge,
            String> createLabeledHistoryGraph() {
        LabeledGraph<Integer, DefaultEdge, String> g0, g1, g2, g3, g4, g5;

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
                LabeledGraph<Integer, DefaultEdge, String>,
                Integer,
                DefaultEdge,
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

    private static LabeledGraph<Integer, DefaultEdge, String> createGraph() {
        return new LabeledGraph<>(
                new DefaultDirectedGraph<>(DefaultEdge.class),
                () -> new DefaultDirectedGraph<>(DefaultEdge.class));
    }

    private static void addPattern(LabeledGraph<Integer, DefaultEdge, String> graph) {
        for (int i = 1; i < 5; i++) {
            graph.addVertex(i);
        }
        graph.addEdge(1, 2);
        graph.addEdge(2, 3);
        graph.addEdge(3, 1);
        graph.addEdge(4, 1);

        graph.addLabel(1, "A");
        graph.addLabel(2, "B");
        graph.addLabel(3, "C");
        graph.addLabel(4, "X");
        graph.addLabel(5, "Y");
    }
}
