package nl.tue.win.dbt.data;

import org.jgrapht.graph.DefaultEdge;

public class Edge extends DefaultEdge {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Edge edge = (Edge) o;
        Object source = getSource();
        Object target = getTarget();

        if (source != null ? !source.equals(edge.getSource()) : edge.getSource() != null) return false;
        return target != null ? target.equals(edge.getTarget()) : edge.getTarget() == null;
    }

    @Override
    public int hashCode() {
        int result = getSource() != null ? getSource().hashCode() : 0;
        result = 31 * result + (getTarget() != null ? getTarget().hashCode() : 0);
        return result;
    }
}
