package nl.tue.win.dbt.parsers;

import nl.tue.win.dbt.data.LabeledGraph;
import nl.tue.win.dbt.data.LabeledHistoryGraph;
import org.jgrapht.graph.DefaultEdge;

public class DbplParser implements DatasetParser<String, DefaultEdge, String> {

    @Override
    public LabeledHistoryGraph<
            LabeledGraph<String, DefaultEdge, String>,
            String,
            DefaultEdge,
            String> convertToHistoryGraph(String file) {
        throw new UnsupportedOperationException("Not yet implemented."); // TODO
    }
}
