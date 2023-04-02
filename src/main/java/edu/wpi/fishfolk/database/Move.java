package edu.wpi.fishfolk.database;

import java.util.ArrayList;

public class Move extends TableEntry {
    public String longName;
    public String date;
    @Override
    public boolean construct(ArrayList<String> data) {
        if(data.size() != 8){
            return false;
        }

        this.id = data.get(0);
        this.longName = data.get(1);
        this.date = data.get(2);
        return true;
    }

    @Override
    public ArrayList<String> deconstruct() {
        ArrayList<String> data = new ArrayList<>();
        data.add(this.id);
        data.add(this.longName);
        data.add(this.date);

        return data;
    }

    public Move(int nodeId, String longName, String date){
        this.id = Integer.toString(nodeId);
        this.longName = longName;
        this.date = date;
    }

}
