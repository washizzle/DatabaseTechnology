package nl.tue.win.dbt.parsers;

import nl.tue.win.dbt.data.Edge;
import nl.tue.win.dbt.data.LabeledGraph;
import nl.tue.win.dbt.data.LabeledHistoryGraph;

public class DbplParser implements DatasetParser<String, Edge, String> {

    @Override
    public LabeledHistoryGraph<
            LabeledGraph<String, Edge, String>,
            String,
            Edge,
            String> convertToHistoryGraph(String file) {
        throw new UnsupportedOperationException("Not yet implemented."); // TODO
    }
}
