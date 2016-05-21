package nl.tue.win.dbt.data;

import org.jgrapht.Graph;

public interface GraphCreator<G extends Graph<V, E>, V, E> {
    G create();
}
