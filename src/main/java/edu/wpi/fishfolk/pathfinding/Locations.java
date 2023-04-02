package edu.wpi.fishfolk.pathfinding;

public class Locations {
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
