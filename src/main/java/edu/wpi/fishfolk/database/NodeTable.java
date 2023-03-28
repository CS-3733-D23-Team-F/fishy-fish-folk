package edu.wpi.fishfolk.database;

import edu.wpi.fishfolk.pathfinding.Node;
import java.util.ArrayList;
import java.util.LinkedList;

public class NodeTable {

    public ArrayList<String> headers;

    public Node getNode(String id) {
        return null;
    }

    //true if inserted, false if duplicate or otherwise not added
    public boolean insertNode(Node node) {
        return false;
    }

    //true if updated (maybe inserted new node) , false
    public boolean updateNode(Node node) {
        return false;
    }

    public void removeNode(Node node) {

    }

    public void importCSV(String fileName) {

    }

    public void exportCSV(String filename) {

    }
}
