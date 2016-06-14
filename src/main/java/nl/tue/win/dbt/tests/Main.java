package nl.tue.win.dbt.tests;

import nl.tue.win.dbt.Configuration;
import nl.tue.win.dbt.algorithms.TimeIndices.Tila;
import nl.tue.win.dbt.data.DblpLabel;
import nl.tue.win.dbt.data.Edge;
import nl.tue.win.dbt.data.LabeledGraph;
import nl.tue.win.dbt.parsers.DblpParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Main {
    private final List<LabeledGraph<Integer, Edge, DblpLabel>> patterns;
    private final ReadTime<Integer, Edge, DblpLabel> readTime;
    private final LvgTime<Integer, Edge, DblpLabel> lvgTime;
    private final BaseSetupTime<Integer, Edge, DblpLabel> baseTime;
    private final QueryTime<Integer, Edge, DblpLabel> baseQueryTime;
    private final DurableSetupTime<Integer, Edge, DblpLabel> tilaTime;
    private final QueryTime<Integer, Edge, DblpLabel> tilaQueryTime;
    private final DurableSetupTime<Integer, Edge, DblpLabel> ctinlaTime;
    private final QueryTime<Integer, Edge, DblpLabel> ctinlaQueryTime;

    private Main(String filename, boolean base, boolean tila, boolean ctinla, int maxClique, DblpLabel label) {
        Objects.requireNonNull(filename);
        Objects.requireNonNull(label);
        System.out.println("Creating patterns");
        this.patterns = this.createCliquePatterns(maxClique, label);
        System.out.println("Creating history graph");
        this.readTime = new ReadTime<>(filename, new DblpParser());
        System.out.println("Creating LVG");
        this.lvgTime = new LvgTime<>(this.readTime.getLhg());

        if(base) {
            System.out.println("Creating baseline algorithm");
            this.baseTime = this.createBaseTime();
            System.out.println("Executing baseline queries");
            this.baseQueryTime = new QueryTime<>(this.baseTime.getFilename(), this.patterns);
        } else {
            this.baseTime = null;
            this.baseQueryTime = null;
        }

        if(tila) {
            System.out.println("Creating TiLa algorithm");
            this.tilaTime = this.createTilaTime();
            System.out.println("Executing TiLa queries");
            this.tilaQueryTime = new QueryTime<>(this.tilaTime.getFilename(), this.patterns);
        } else {
            this.tilaTime = null;
            this.tilaQueryTime = null;
        }

        if(ctinla) {
            System.out.println("Creating CTiNLa algorithm");
            this.ctinlaTime = this.createCtinlaTime();
            System.out.println("Executing CTiNLa queries");
            this.ctinlaQueryTime = new QueryTime<>(this.ctinlaTime.getFilename(), this.patterns);
        } else {
            this.ctinlaTime = null;
            this.ctinlaQueryTime = null;
        }
    }

    private BaseSetupTime<Integer, Edge, DblpLabel> createBaseTime() {
        return new BaseSetupTime<>(
                "data/base.ser", this.readTime.getLhg());
    }

    private DurableSetupTime<Integer, Edge, DblpLabel> createTilaTime() {
        Configuration config = new Configuration();
        config.setTi(new Tila());
        return new DurableSetupTime<>(
                "data/tila.ser",
                this.lvgTime.getLvg(),
                this.lvgTime.getLhg().getGraphCreator(),
                config);
    }

    private DurableSetupTime<Integer, Edge, DblpLabel> createCtinlaTime() {
        Configuration config = new Configuration();
        config.setTi(new Tila());
        return new DurableSetupTime<>(
                "data/ctinla.ser",
                this.lvgTime.getLvg(),
                this.lvgTime.getLhg().getGraphCreator(),
                config);
    }

    private List<LabeledGraph<Integer, Edge, DblpLabel>> createCliquePatterns(int max) {
        List<LabeledGraph<Integer, Edge, DblpLabel>> patterns = new ArrayList<>();
        for(int i = 1; i <= max; i++) {
            for(DblpLabel label: DblpLabel.values()) {
                patterns.add(createCliquePattern(i, label));
            }
        }
        return patterns;
    }

    private List<LabeledGraph<Integer, Edge, DblpLabel>> createCliquePatterns(int max, DblpLabel label) {
        List<LabeledGraph<Integer, Edge, DblpLabel>> patterns = new ArrayList<>();
        for(int i = 1; i <= max; i++) {
            patterns.add(createCliquePattern(i, label));
        }
        return patterns;
    }

    private LabeledGraph<Integer, Edge, DblpLabel> createCliquePattern(int nodes, DblpLabel label) {
        Objects.requireNonNull(label);
        if(nodes <= 0) {
            throw new IllegalArgumentException("Expected positive integer");
        }
        LabeledGraph<Integer, Edge, DblpLabel> graph = DblpParser.createGraph();
        for(int i = 0; i < nodes; i++) {
            graph.addVertex(i);
            graph.addLabel(i, label);
            for(int j = 0; j < i; j++) {
                graph.addEdge(i, j);
            }
        }
        return graph;
    }

    public static void main(String[] args) {
        String filename = "out.dblp_coauthor.001";
        boolean base = true;
        boolean tila = true;
        boolean ctinla = true;
        int maxClique = 5;
        DblpLabel label = DblpLabel.JUNIOR;

        Main m = new Main(filename, base, tila, ctinla, maxClique, label);

        // TODO: query the info.

        System.out.println("-------------------------------------------------------------------");
        System.out.println("Results");
        System.out.println("-------------------------------------------------------------------");

        System.out.println("Reading data took " + m.readTime.calculateReadDelta());

        System.out.println("Creating LVG took " + m.lvgTime.calculateLvgDelta());

        if(base) {
            System.out.println("Creating base took " + m.baseTime.calculateBaseDelta());
            System.out.println("Writing base took " + m.baseTime.calculateWriteDelta());
            printQueryTime(m.baseQueryTime, "base");
        }

        if(tila) {
            System.out.println("Creating tila took " + m.tilaTime.calculateDurableDelta());
            System.out.println("Writing tila took " + m.tilaTime.calculateWriteDelta());
            printQueryTime(m.tilaQueryTime, "TiLa");
        }

        if(ctinla) {
            System.out.println("Creating ctinla took " + m.ctinlaTime.calculateDurableDelta());
            System.out.println("Writing ctinla took " + m.ctinlaTime.calculateWriteDelta());
            printQueryTime(m.ctinlaQueryTime, "CTiNLa");
        }
    }

    private static void printQueryTime(QueryTime<?, ?, ?> qt, String description) {
        List<Long> col = qt.calculateCollectiveTimeDeltas();
        printList(col, "Collective " + description);
        List<Long> con = qt.calculateCollectiveTimeDeltas();
        printList(con, "Continuous " + description);
    }

    private static void printList(List<Long> list, String description) {
        for(int i = 0; i < list.size(); i++) {
            System.out.println(description + ' ' + (i+1) + "-clique query took " + list.get(i));
        }
    }

}
