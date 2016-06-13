package nl.tue.win.dbt.algorithms.TimeIndices.candidatefilters;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import nl.tue.win.dbt.data.LabeledGraph;
import nl.tue.win.dbt.data.LabeledVersionGraph;
import nl.tue.win.dbt.util.Graphs;

import java.io.Serializable;
import java.util.*;

// class CtinlaFilter implements CTINLA time index and uses generic data types for Vertices, Edges and Labels
public class CtinlaFilter<V, E, L> implements CandidateFilter<V, E, L>, Serializable {

    // variables for lvg, radius, nodes, labels and ctinla time index
    private final LabeledVersionGraph<V, E, L> lvg;
    private final int radius = 1;
    private ArrayList<V> nodes;
    private ArrayList<L> labels;
    private ArrayList<Table<V, L, ArrayList<Integer>>> ctinla;

    // constructor gets labeled version graph
    public CtinlaFilter(LabeledVersionGraph<V, E, L> lvg) {

        // initialize variables
        this.lvg = lvg;
        this.nodes = new ArrayList<>(lvg.vertexSet());
        this.labels = new ArrayList<>(lvg.labelSet());
        this.ctinla = new ArrayList<>();

        // print graph structure
        /*for (V node : this.nodes) {
            System.out.println("Node: " + node + ", Edges: " + Graphs.getNextNeighbors(lvg, node));
        }*/

        // from radius 0 to maximum radius
        for (int r = 0; r <= this.radius; r++) {

            // add new table for respective radius to ctinla index
            ctinla.add(HashBasedTable.create());

            // for each node/label combination
            for (V node : this.nodes) {
                for (L label : this.labels) {

                    // arraylist for temporarily saving label counters
                    ArrayList<Integer> counters = new ArrayList<>();

                    // if radius is 0, create label counters for pattern node itself
                    if (r == 0) {
                        if (lvg.labelLifespan(node, label) != null) {
                            for (int i = 0; i < this.lvg.getSize(); i++) {
                                if (lvg.labelLifespan(node, label).get(i))
                                    counters.add(1);
                                else
                                    counters.add(0);
                            }
                        } else {
                            for (int i = 0; i < this.lvg.getSize(); i++) {
                                counters.add(0);
                            }
                        }
                    }

                    // if radius is 1, create label counters for pattern neighbors of pattern node
                    if (r == 1) {
                        for (int i = 0; i < this.lvg.getSize(); i++) {
                            int sum = 0;
                            for (V v : Graphs.getNextNeighbors(lvg, node)) {
                                if (lvg.labelLifespan(v, label) != null) {
                                    if (lvg.labelLifespan(v, label).get(i)) {
                                        sum++;
                                    }
                                }
                            }
                            counters.add(sum);
                        }
                    }

                    // for higher radiuses: google "jgrapht closest first iterator"

                    // add entry to time index
                    ctinla.get(r).put(node, label, counters);
                }
            }
        }

//        // print ctinla time index
//        for (int i = 0; i <= this.radius; i++) {
//            System.out.println("Ctinla(" + i + "):");
//            for (Table.Cell<V, L, ArrayList<Integer>> t : ctinla.get(i).cellSet()) {
//                System.out.println("Node: " + t.getRowKey() + ", Label: " + t.getColumnKey() + ", Counters: " + t.getValue());
//            }
//        }
    }

    @Override
    // filter candidates method gets the query pattern, the current pattern node and a bitset of intervals
    public Set<V> filterCandidates(
            final LabeledGraph<V, E, L> pattern,
            final V patternVertex,
            final BitSet intervals) {

        // get label of pattern nodes (if there are more only consider first one)
        L c_label = pattern.getLabels(patternVertex).iterator().next();

        // get labels of neighbors of pattern node
        ArrayList<L> n_labels = new ArrayList<>();
        for (V node : Graphs.getNextNeighbors(pattern, patternVertex)) {
            n_labels.add(pattern.getLabels(node).iterator().next());
        }

        // save counters of label occurrences of pattern neighbors in hashmap
        HashMap<L, Integer> hm = new HashMap<>();
        for (L label : n_labels) {
            if (!hm.containsKey(label)) {
                hm.put(label, 1);
            } else {
                hm.replace(label, hm.get(label) + 1);
            }
        }
        //System.out.println("Hashmap Label Groups: " + hm);

        // candidate set
        Set<V> candidates = new HashSet<>();

        // for each distance radius
        for (int r = 0; r <= this.radius; r++) {

            // radius 0 => check for labels of pattern node itself
            if (r == 0) {

                // for each possible node
                for (V node : this.nodes) {

                    boolean match = false;
                    // check for all time instants of given interval if there is at least one occurrence of the current label
                    for (int i = intervals.nextSetBit(0); i >= 0 && i < this.lvg.getSize(); i = intervals.nextSetBit(i+1)) {
                        Table<V, L, ArrayList<Integer>> rTable = ctinla.get(r);
                        ArrayList<Integer> timestamps = rTable.get(node, c_label);
                        if(timestamps != null) {
                            Integer count = timestamps.get(i);
                            if (count != null && count > 0) {
                                match = true;
                                break;
                            }
                        }
                    }

                    if (match)
                        candidates.add(node);

                    /*// if sum of counters is greater than 0, there is at least one occurrence, therefore node is added to candidate set
                    if (ctinla.get(r).get(node, c_label).stream().mapToInt(Integer::intValue).sum() > 0) {
                        candidates.add(node);
                    }*/
                }
            }

            // radius 1 => check for neighbors of pattern node
            if (r == 1) {

                // create temporary candidate set to avoid runtime errors when removing nodes from the candidate set while iterating through it
                Set<V> temp = new HashSet<>(candidates);

                // for each candidate node
                for (V node : candidates) {

                    // create two dimensional array list for comparing counters
                    ArrayList<ArrayList<Integer>> cnt = new ArrayList<>();

                    // add counters of candidate node
                    cnt.add(ctinla.get(0).get(node, c_label));

                    // for each group of neighbor labels
                    for (Map.Entry<L, Integer> e : hm.entrySet()) {
                        // add counters of each group of neighbor labels
                        cnt.add(ctinla.get(r).get(node, e.getKey()));
                    }

                    //System.out.println("Counters Node " + node + ": " + cnt);

                    boolean total_match = false;
                    // check for all time instants of given interval
                    for (int i = intervals.nextSetBit(0); i >= 0 && i < this.lvg.getSize(); i = intervals.nextSetBit(i+1)) {

                        boolean match = true;
                        // check if pattern node and pattern neighbors exist at the same time instant
                        for (ArrayList<Integer> c : cnt) {
                            if (c.get(i) == 0) {
                                match = false;
                                break;
                            }
                        }

                        // if that is so, we also have to check for each label if there are enough respective neighbors according to the pattern
                        if (match) {
                            for (Map.Entry<L, Integer> e : hm.entrySet()) {
                                if (e.getValue() > ctinla.get(r).get(node, e.getKey()).get(i)) {
                                    match = false;
                                    break;
                                }
                            }

                            // if both conditions hold, we have a pattern match in at least one time instant
                            if (match) {
                                total_match = true;
                                break;
                            }
                        }
                    }

                    // if there was no pattern match, the current node is removed from the temporary candidate set
                    if (!total_match) {
                        temp.remove(node);
                        //System.out.println("candidates: " + candidates);
                        //System.out.println("temp: " + temp);
                    }
                }

                // copy temporary set back to candidate set
                candidates = temp;
            }
        }

//        // print candidate nodes
//        System.out.println("Pattern Node: " + pattern.getLabels(patternVertex) + ", Candidate Nodes: " + candidates);

        return candidates;
    }
}
