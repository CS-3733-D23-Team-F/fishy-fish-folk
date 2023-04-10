package edu.wpi.fishfolk.database;

import javafx.scene.text.Text;

public class NodeText extends Text {

    String NodeTextID;

    public String getNodeTextID() {
        return NodeTextID;
    }

    public NodeText(String nodeId, double x, double y, String text) {
        super(x, y, text);
        this.NodeTextID = nodeId;
    }
}
