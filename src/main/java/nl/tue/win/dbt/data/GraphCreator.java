package nl.tue.win.dbt.data;

import org.jgrapht.Graph;

public interface GraphCreator<E> {
    <G extends Graph<?, E>> G create();
}
