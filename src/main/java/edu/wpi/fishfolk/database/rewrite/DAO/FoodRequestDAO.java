package edu.wpi.fishfolk.database.rewrite.DAO;

import edu.wpi.fishfolk.database.rewrite.DataEdit;
import edu.wpi.fishfolk.database.rewrite.IDAO;
import edu.wpi.fishfolk.database.rewrite.TableEntry.FoodRequest;
import edu.wpi.fishfolk.database.rewrite.TableEntry.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FoodRequestDAO implements IDAO<FoodRequest> {

    private final ArrayList<String> headers;

    private HashMap<Integer, FoodRequest> tableMap;
    private ArrayList<DataEdit<FoodRequest>> dataEdits;

    /** DAO for Food Request table in PostgreSQL database. */
    public FoodRequestDAO() {
        this.headers = new ArrayList<>(List.of("id", "assignee", "status", "items", "time", "payer", "location", "totalprice"));
    }

    @Override
    public boolean insertEntry(FoodRequest entry) {
        return false;
    }

    @Override
    public boolean updateEntry(FoodRequest entry) {
        return false;
    }

    @Override
    public boolean removeEntry(FoodRequest entry) {
        return false;
    }

    @Override
    public FoodRequest getEntry(String identifier) {
        return null;
    }

    @Override
    public ArrayList<FoodRequest> getAllEntries() {
        return null;
    }

    @Override
    public boolean updateDatabase() {
        return false;
    }
}
