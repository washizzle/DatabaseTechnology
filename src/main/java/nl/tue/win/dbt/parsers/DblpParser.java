package nl.tue.win.dbt.parsers;

import com.google.common.collect.RangeSet;
import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import com.google.common.collect.TreeRangeSet;
import nl.tue.win.dbt.algorithms.BaselineAlgorithm;
import nl.tue.win.dbt.algorithms.DurablePatternAlgorithm;
import nl.tue.win.dbt.data.*;

import java.io.*;
import java.util.*;

import nl.tue.win.dbt.util.IntegerRanges;
import org.jgrapht.graph.SimpleGraph;

import static java.util.Calendar.YEAR;

public class DblpParser implements DatasetParser<Integer, Edge, DblpLabel> {

    private final Map<Integer, LabeledGraph<Integer, Edge, DblpLabel>> yearToLgMap;
    private final Table<Integer, Integer, Integer> yearAuthorCount;
    private final LabeledHistoryGraph<
            LabeledGraph<Integer, Edge, DblpLabel>,
            Integer,
            Edge,
            DblpLabel> lhg;

    public DblpParser() {
        this.yearToLgMap = new HashMap<>();
        this.yearAuthorCount = TreeBasedTable.create();
        this.lhg = new LabeledHistoryGraph<>(DblpParser::createGraph);
    }

    @Override
    public LabeledHistoryGraph<
            LabeledGraph<Integer, Edge, DblpLabel>,
            Integer,
            Edge,
            DblpLabel> convertToHistoryGraph(String file) {

        FileReader inputFile = null;//sample_dblp_coauthor.txt
        try(BufferedReader bufRead = new BufferedReader(new FileReader(file))) {
            String myLine = null;
            while ( (myLine = bufRead.readLine()) != null) {
                String[] words = myLine.split(" +");
                if (this.isValidLine(words)) {
                    this.parse(words);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        addLabels();
        yearToLgMap.keySet().stream().sorted();
        for(Integer year : yearToLgMap.keySet()) {
            lhg.add(this.yearToLgMap.get(year));
        }
        return this.lhg;
    }

    private boolean isValidLine(String[] words) {
        return isDigit(words[0]) && isDigit(words[1]) && isDigit(words[3]);
    }

    private boolean isDigit(String word) {
        return word.matches("\\d*");
    }

    private void addLabels() {
        DblpLabel label;
        int year, author, count;
        for(Table.Cell<Integer, Integer, Integer> cell: this.yearAuthorCount.cellSet()) {
            year = cell.getRowKey();
            author = cell.getColumnKey();
            count = cell.getValue();
            label = DblpLabel.calculateLabel(count);
            this.yearToLgMap.get(year).addLabel(author, label);
        }
    }

    public void parse(String[] array) {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(Long.parseLong(array[3])*1000);
        int year = date.get(YEAR);

        LabeledGraph<Integer, Edge, DblpLabel> graph;
        if(!this.yearToLgMap.containsKey(year)) {
            graph = this.createGraph();
            this.yearToLgMap.put(year, graph);
        } else {
            graph = this.yearToLgMap.get(year);
        }

        int author0 = Integer.parseInt(array[0]);
        int author1 = Integer.parseInt(array[1]);

        graph.addVertex(author0);
        graph.addVertex(author1);
        Edge e = graph.addEdge(author0, author1);

        if(e!=null) {
            incrementYearAuthorCount(year, author0);
            incrementYearAuthorCount(year, author1);
        }
    }

    private void incrementYearAuthorCount(int year, int author) {
        if(!this.yearAuthorCount.contains(year, author)) {
            this.yearAuthorCount.put(year, author, 0);
        }
        this.yearAuthorCount.put(year, author, this.yearAuthorCount.get(year, author) + 1); // TODO: get++
    }

    private static LabeledGraph<Integer, Edge, DblpLabel> createGraph() {
        return new LabeledGraph<>(
                new SimpleGraph<>(Edge.class),
                () -> new SimpleGraph<>(Edge.class));
    }

    public static void main(String[] args) {
        System.out.println("The parser way :)");

        String[] filenames = new String[] {
                "out.dblp_coauthor.001",
                "out.dblp_coauthor.002",
                "sample2.txt",
                "sample3.txt",
                "sample4.txt",
        };
        boolean success = true;
        for(String filename: filenames) {
            success &= doTest(filename);
        }
        System.out.println("Test " + (success ? "passed" : "failed"));
    }

    private static boolean doTest(String filename) {
        DblpParser dblpParser = new DblpParser();
        LabeledHistoryGraph<LabeledGraph<Integer, Edge, DblpLabel>, Integer, Edge, DblpLabel> lhg;
        lhg = dblpParser.convertToHistoryGraph(filename);

        System.out.println("Timestamps: " + lhg.size());
        System.out.println("Vertices: " + lhg.vertexSet().size());
        System.out.println("Edges: " + lhg.edgeSet().size());

        BaselineAlgorithm<Integer, Edge, DblpLabel> base = new BaselineAlgorithm<>(lhg);
        DurablePatternAlgorithm<Integer, Edge, DblpLabel> dur = new DurablePatternAlgorithm<>(lhg);

        LabeledGraph<Integer, Edge, DblpLabel> pattern;
        pattern = createGraph();
        pattern.addVertex(Integer.MIN_VALUE);
        pattern.addLabel(Integer.MIN_VALUE, DblpLabel.PROFESSOR);
        pattern.addVertex(-100);
        pattern.addLabel(-100, DblpLabel.JUNIOR);
        pattern.addEdge(-100, Integer.MIN_VALUE);
        pattern.addVertex(-200);
        pattern.addLabel(-200, DblpLabel.JUNIOR);
        pattern.addEdge(-100, -200);
        pattern.addEdge(-200, Integer.MIN_VALUE);

        RangeSet<Integer> intervals = TreeRangeSet.create();
//        intervals.add(IntegerRanges.closed(1, 5));
        intervals.add(IntegerRanges.closed(0, lhg.size() - 1));

        Set<Lifespan<LabeledGraph<Integer, Edge, DblpLabel>>> baselineCon;
        baselineCon = base.queryMaximalContinuousDurableGraphPattern(pattern, intervals);
        Set<Lifespan<LabeledGraph<Integer, Edge, DblpLabel>>> baselineCol;
        baselineCol = base.queryMaximalCollectiveDurableGraphPattern(pattern, intervals);

        Set<Lifespan<LabeledGraph<Integer, Edge, DblpLabel>>> algoCon;
        algoCon = dur.queryMaximalContinuousDurableGraphPattern(pattern, intervals);
        Set<Lifespan<LabeledGraph<Integer, Edge, DblpLabel>>> algoCol;
        algoCol = dur.queryMaximalCollectiveDurableGraphPattern(pattern, intervals);

        boolean con = baselineCon.equals(algoCon);
        boolean col = baselineCol.equals(algoCol);

        System.out.println("Continuous");
        System.out.println(baselineCon);
        System.out.println(algoCon);
        System.out.println(con);

        System.out.println("Collective");
        System.out.println(baselineCol);
        System.out.println(algoCol);
        System.out.println(col);

        System.out.println();
        return con && col;
    }
}
