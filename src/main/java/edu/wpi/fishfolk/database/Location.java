package edu.wpi.fishfolk.database;

import edu.wpi.fishfolk.pathfinding.Node;import edu.wpi.fishfolk.pathfinding.NodeType;

import java.util.ArrayList;

public class Location extends TableEntry{
        public String longname;
        public String shortname;
        public NodeType type;

        public Location(String longname, String shortname, NodeType nodeType){
            this.longname = longname;
            this.shortname = shortname;
            this.type = nodeType;
        }

    @Override
    public boolean construct(ArrayList<String> data) {
        if(data.size() != 3){
            return false;
        }

        this.longname = data.get(0);
        this.shortname = data.get(1);
        this.type = NodeType.valueOf(data.get(2)); //throws error if string is not valie type
        return true;
    }


    @Override
    public ArrayList<String> deconstruct() {
            ArrayList<String> data = new ArrayList<>();
            data.add(longname);
            data.add(shortname);
            data.add(type.toString());
            return data;
    }
}
