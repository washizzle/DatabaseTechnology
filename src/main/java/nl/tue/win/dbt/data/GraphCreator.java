package nl.tue.win.dbt.data;

import org.jgrapht.Graph;

import java.io.Serializable;

public interface GraphCreator<G extends Graph<V, E>, V, E> extends Serializable {
    G create();
}
