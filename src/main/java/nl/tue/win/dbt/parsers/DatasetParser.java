package nl.tue.win.dbt.parsers;

import nl.tue.win.dbt.data.LabeledGraph;
import nl.tue.win.dbt.data.LabeledHistoryGraph;

public interface DatasetParser<V, E, L> {
     LabeledHistoryGraph<LabeledGraph<V, E, L>, V, E, L> convertToHistoryGraph(String file);
}
