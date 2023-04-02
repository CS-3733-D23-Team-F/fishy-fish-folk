package edu.wpi.fishfolk.database;


import java.util.ArrayList;

/**
 *
 *
 */


//TODO: charles work, check Node.csv
public class MicroNode extends TableEntry{

    public int nodeid;

    public double xcoord;
    public double ycoord;
    public String floor;

    public String building;

    @Override
    public TableEntry construct(ArrayList<String> data) {
        if(data.size() != 5){
            return null;
        }

        return new MicroNode(
                Integer.parseInt(data.get(0)),
                Double.parseDouble(data.get(1)),
                Double.parseDouble(data.get(2)),
                data.get(3),
                data.get(4)
        );

    }

    @Override
    public ArrayList<String> deconstruct() {
        ArrayList<String> data = new ArrayList<>();
        data.add(Integer.toString(this.nodeid));
        data.add(Double.toString(this.xcoord));
        data.add(Double.toString(this.ycoord));
        data.add(floor);
        data.add(building);

        return data;
    }

    public MicroNode(int nodeid, double xcoord, double ycoord, String floor, String building){
        this.nodeid = nodeid;
        this.xcoord = xcoord;
        this.ycoord = ycoord;
        this.floor = floor;
        this.building = building;
    }

}
