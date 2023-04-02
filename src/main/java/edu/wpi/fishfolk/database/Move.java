package edu.wpi.fishfolk.database;

import java.util.ArrayList;

public class Move extends TableEntry {

    public int nodeid;
    public String longName;
    public String date;
    @Override
    public TableEntry construct(ArrayList<String> data) {
        if(data.size() != 8){
            return null;
        }

        return new Move(
                Integer.parseInt(data.get(0)),
                data.get(1),
                data.get(2));
    }

    @Override
    public ArrayList<String> deconstruct() {
        ArrayList<String> data = new ArrayList<>();
        data.add(Integer.toString(this.nodeid));
        data.add(this.longName);
        data.add(this.date);


        return data;

    }

    public Move(int nodeid, String longName, String date){
        this.nodeid = nodeid;
        this.longName = longName;
        this.date = date;
    }




}
