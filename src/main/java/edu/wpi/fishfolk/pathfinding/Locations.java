package edu.wpi.fishfolk.pathfinding;

import edu.wpi.fishfolk.database.TableEntry;

import java.util.ArrayList;

public class Locations{
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

}
