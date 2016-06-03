package nl.tue.win.dbt.parsers;

import nl.tue.win.dbt.data.Edge;
import nl.tue.win.dbt.data.LabeledGraph;
import nl.tue.win.dbt.data.LabeledHistoryGraph;
import java.io.File;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

public class DblpParser implements DatasetParser<String, Edge, String> {

    @Override
    public LabeledHistoryGraph<
            LabeledGraph<String, Edge, String>,
            String,
            Edge,
            String> convertToHistoryGraph(String file) {
        throw new UnsupportedOperationException("Not yet implemented."); // TODO
    }
    public static void main(String[] args) {
        try {
            File inputFile = new File("input.txt");
            SAXReader reader = new SAXReader();
            Document document = reader.read( inputFile );

            System.out.println("Root element :"
                    + document.getRootElement().getName());

            Element classElement = document.getRootElement();

            List<Node> nodes = document.selectNodes("/dblp/article" );
            System.out.println("----------------------------");
            for (Node node : nodes) {
                System.out.println("\nCurrent Element :"
                        + node.getName());
                System.out.println("date : "
                        + node.valueOf("@mdate") );
                System.out.println("key : "
                        + node.valueOf("@key") );
                System.out.println("First Name : " + node.selectSingleNode("firstname").getText());
                System.out.println("Last Name : " + node.selectSingleNode("lastname").getText());
                System.out.println("First Name : " + node.selectSingleNode("nickname").getText());
                System.out.println("Marks : " + node.selectSingleNode("marks").getText());
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }
}
