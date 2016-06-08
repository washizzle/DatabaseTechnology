package nl.tue.win.dbt.parsers;

import nl.tue.win.dbt.data.Edge;
import nl.tue.win.dbt.data.LabeledGraph;
import nl.tue.win.dbt.data.LabeledHistoryGraph;

import java.io.*;
import java.util.*;
import org.jgrapht.graph.DefaultDirectedGraph;

import static java.util.Calendar.YEAR;

public class DblpParser implements DatasetParser<String, Edge, String> {

    private static Map<Integer, ArrayList<LabeledGraph<Integer, Edge, String>>> yearlhgmap =
            new HashMap<Integer, ArrayList<LabeledGraph<Integer, Edge, String>>>();
    private static Map<String, Integer> yearauthorcount = new HashMap<String, Integer>();
    private static ArrayList<LabeledHistoryGraph<
            LabeledGraph<Integer, Edge, String>,
            Integer,
            Edge,
            String>> lhglist = new ArrayList<>();

    @Override
    public LabeledHistoryGraph<
            LabeledGraph<String, Edge, String>,
            String,
            Edge,
            String> convertToHistoryGraph(String file) {

        FileReader inputFile = null;//sample_dblp_coauthor.txt
        try {
            inputFile = new FileReader(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader bufRead = new BufferedReader(inputFile);
        String myLine = null;
        try {
            while ( (myLine = bufRead.readLine()) != null)
            {
                String[] array1 = myLine.split(" +");
                if (array1[0].matches("\\d*")) {
                    parse(array1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(Integer year : yearlhgmap.keySet()) {
            LabeledHistoryGraph<
                    LabeledGraph<Integer, Edge, String>,
                    Integer,
                    Edge,
                    String> lhg = new LabeledHistoryGraph<>(DblpParser::createGraph);
            lhglist.add(year,lhg);
            for (LabeledGraph g : yearlhgmap.get(year)) {
                lhglist.get(year).addGraph(g);
            }
        }
        return lhglist.get(1999);
    }

//    public static void main(String[] args) throws IOException {
//        FileReader inputFile = new FileReader("out.dblp_coauthor");//sample_dblp_coauthor.txt
//        BufferedReader bufRead = new BufferedReader(inputFile);
//        String myLine = null;
//        while ( (myLine = bufRead.readLine()) != null)
//        {
//            String[] array1 = myLine.split(" +");
//            if (array1[0].matches("\\d*")) {
//                parse(array1);
//            }
//        }
//        for(Integer year : yearlhgmap.keySet()) {
//            LabeledHistoryGraph<
//                    LabeledGraph<Integer, Edge, String>,
//                    Integer,
//                    Edge,
//                    String> lhg = new LabeledHistoryGraph<>(Main::createGraph);
//            lhglist.add(year,lhg);
//            for (LabeledGraph g : yearlhgmap.get(year)) {
//                lhglist.get(year).addGraph(g);
//
//
//            }
//        }
//    }

    public static void parse(String[] array) {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(Long.parseLong(array[3])*1000);
        int year = date.get(YEAR);
        LabeledGraph<Integer, Edge, String> g0;
        g0 = createGraph();
        g0.addVertex(Integer.parseInt(array[0]));
        g0.addVertex(Integer.parseInt(array[1]));
        g0.addEdge(Integer.parseInt(array[0]), Integer.parseInt(array[1]));
        String yearPlusAuthor = Integer.toString(year) + array[0];
        yearauthorcount.putIfAbsent(yearPlusAuthor, 0);
        yearauthorcount.put(yearPlusAuthor, yearauthorcount.get(yearPlusAuthor)+1);
        String label = Integer.toString(yearauthorcount.get(yearPlusAuthor));
        g0.addLabel(Integer.parseInt(array[0]), label);
        yearlhgmap.putIfAbsent(year, new ArrayList<LabeledGraph<Integer, Edge, String>>());
        yearlhgmap.get(year).add(g0);
    }
    private static LabeledGraph<Integer, Edge, String> createGraph() {
        return new LabeledGraph<>(
                new DefaultDirectedGraph<>(Edge.class),
                () -> new DefaultDirectedGraph<>(Edge.class));
    }
}
