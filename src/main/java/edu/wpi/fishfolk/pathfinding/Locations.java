package edu.wpi.fishfolk.pathfinding;

import edu.wpi.fishfolk.database.TableEntry;

import java.util.ArrayList;

public class Locations extends TableEntry{
        public String locationid;
        public String longname;
        public String shortname;
        public NodeType nodetype;

        public Locations(String locationid, String longname, String shortname, NodeType nodetype){
            this.locationid = locationid;
            this.longname = longname;
            this.shortname = shortname;
            this.nodetype = nodetype;
        }

    @Override
    public TableEntry construct(ArrayList<String> data) {
        if(data.size() < 4 || data.size() > 4){
            return null;
        }
        if (nodetype.isItNodeType(data.get(3))){
            return new Locations(
                    data.get(0),
                    data.get(1),
                    data.get(2),
                    nodetype.makeStringIntoNodeType(data.get(3)));
        }
        System.out.println("Incorrect Input, NodeType not given");
        return null;
    }


    @Override
    public ArrayList<String> deconstruct() {
        return null;
    }
}
