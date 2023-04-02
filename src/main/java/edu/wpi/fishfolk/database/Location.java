package edu.wpi.fishfolk.database;

import edu.wpi.fishfolk.database.TableEntry;import edu.wpi.fishfolk.pathfinding.NodeType;

import java.util.ArrayList;

public class Location extends TableEntry{
        public String longname;
        public String shortname;
        public NodeType nodetype;

        public Location(String longname, String shortname, NodeType nodetype){
            this.longname = longname;
            this.shortname = shortname;
            this.nodetype = nodetype;
        }

    @Override
    public TableEntry construct(ArrayList<String> data) {
        if(data.size() != 3){
            return null;
        }
        if (nodetype.isItNodeType(data.get(3))){
            return new Location(
                    data.get(0),
                    data.get(1),
                    nodetype.makeStringIntoNodeType(data.get(2)));
        }
        System.out.println("Incorrect Input, NodeType not given");
        return null;
    }


    @Override
    public ArrayList<String> deconstruct() {
            ArrayList<String> data = new ArrayList<>();
            data.add(longname);
            data.add(shortname);
            data.add(nodetype.toString());
            return data;
    }
}
