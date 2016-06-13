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

    private Main(String filename, boolean base, boolean tila, boolean ctinla, int maxClique) {
        Objects.requireNonNull(filename);
        this.patterns = this.createCliquePatterns(maxClique);
        this.readTime = new ReadTime<>(filename, new DblpParser());
        this.lvgTime = new LvgTime<>(this.readTime.getLhg());

        if(base) {
            this.baseTime = this.createBaseTime();
            this.baseQueryTime = new QueryTime<>(this.baseTime.getFilename(), this.patterns);
        } else {
            this.baseTime = null;
            this.baseQueryTime = null;
        }

        if(tila) {
            this.tilaTime = this.createTilaTime();
            this.tilaQueryTime = new QueryTime<>(this.tilaTime.getFilename(), this.patterns);
        } else {
            this.tilaTime = null;
            this.tilaQueryTime = null;
        }

        if(ctinla) {
            this.ctinlaTime = this.createCtinlaTime();
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
            patterns.add(createCliquePattern(i, DblpLabel.JUNIOR));
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
        int maxClique = 1;

        Main m = new Main(filename, base, tila, ctinla, maxClique);

        // TODO: query the info.

        System.out.println("Reading data took " + m.readTime.calculateReadDelta());

        System.out.println("Creating LVG took " + m.lvgTime.calculateLvgDelta());

        if(base) {
            System.out.println("Creating base took " + m.baseTime.calculateBaseDelta());
            System.out.println("Writing base took " + m.baseTime.calculateWriteDelta());
            System.out.println("Collective query 0 took " + m.baseQueryTime.calculateCollectiveTimeDeltas().get(0));
            System.out.println("Continuous query 0 took " + m.baseQueryTime.calculateContinuousTimeDeltas().get(0));
        }

        if(tila) {
            System.out.println("Creating tila took " + m.tilaTime.calculateDurableDelta());
            System.out.println("Writing tila took " + m.tilaTime.calculateWriteDelta());
            System.out.println("Collective query 0 took " + m.tilaQueryTime.calculateCollectiveTimeDeltas().get(0));
            System.out.println("Continuous query 0 took " + m.tilaQueryTime.calculateContinuousTimeDeltas().get(0));
        }

        if(ctinla) {
            System.out.println("Creating ctinla took " + m.ctinlaTime.calculateDurableDelta());
            System.out.println("Writing ctinla took " + m.ctinlaTime.calculateWriteDelta());
            System.out.println("Collective query 0 took " + m.ctinlaQueryTime.calculateCollectiveTimeDeltas().get(0));
            System.out.println("Continuous query 0 took " + m.ctinlaQueryTime.calculateContinuousTimeDeltas().get(0));
        }
    }

}
