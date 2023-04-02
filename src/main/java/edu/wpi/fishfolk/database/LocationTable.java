package edu.wpi.fishfolk.database;

import edu.wpi.fishfolk.pathfinding.Locations;
import edu.wpi.fishfolk.pathfinding.NodeType;
import lombok.Getter;

import javax.xml.stream.Location;
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LocationTable {
    private final Connection db;
    @Getter
   private final String tableName;
    private final ArrayList<String> headers =
            new ArrayList<>(
                    List.of(
                            "locationid",
                            "longname",
                            "shortname",
                            "nodetype"));

    /**
     * Creates a new representation of a location table.
     *
     * @param db Database connection object for this table
     * @param tableName Name of the table
     */
    public LocationTable(Connection db, String tableName) {
        this.db = db;
        this.tableName = tableName.toLowerCase();
    }

    /**
     * For empty tables only, generates new column headers for the node table. TODO: Check if table is
     * empty before applying new headers
     */
    public void addHeaders() {
        Statement statement;
        try {
            String query =
                    "ALTER TABLE "
                            + tableName
                            + " ADD COLUMN "
                            + headers.get(0)
                            + " TEXT,"
                            + " ADD COLUMN "
                            + headers.get(1)
                            + " TEXT,"
                            + " ADD COLUMN "
                            + headers.get(2)
                            + " TEXT,"
                            + " ADD COLUMN "
                            + headers.get(3)
                            + "TEXT,";
            statement = db.createStatement();
            statement.executeUpdate(query);
            System.out.println(
                    "[LocationTable.addHeaders]: Column headers generated for table " + tableName + ".");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Returns a new location from a specified entry in the table.
     *
     * @param id Location id
     * @return New location object, returns null if specified location does not exist in table
     */
    public Location getLocation(String id) {
        Statement statement;
        try {
            String query =
                    "SELECT * FROM " + db.getSchema() + "." + tableName + " WHERE locationid = '" + id + "';";
            statement = db.createStatement();
            statement.execute(query);
            ResultSet results = statement.getResultSet();
            results.next();

            String locationid = results.getString(headers.get(0));
            String longname = results.getString(headers.get(1));
            String shortname = results.getString(headers.get(2));
            String nodetype = results.getString(headers.get(3));

            NodeType type = NodeType.valueOf(nodetype);
           // Point2D point = new Point2D(xcoord, ycoord);
            Locations newLocation = new Locations(locationid, longname, shortname, type);

            System.out.println(
                    "[LocationTable.getLocation]: Location " + id + " retrieved from table " + tableName + ".");

           // return newNode;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    /**
     * Inserts a location into the table if it does not exist.
     *
     * @param location location to insert
     * @return True if inserted, false if the location already exists and/or is not added
     */
    public boolean insertLocation(Locations location) {
        Statement statement;
        try {
            String exists =
                    "SELECT EXISTS (SELECT FROM "
                            + db.getSchema()
                            + "."
                            + tableName
                            + " WHERE locationid = '"
                            + location.locationid
                            + "');";

            statement = db.createStatement();
            statement.execute(exists);
            ResultSet results = statement.getResultSet();
            results.next();

            if (results.getBoolean("exists")) {
                System.out.println(
                        "[LocationTable.insertLocation]: Location "
                                + location.locationid
                                + " already exists in table "
                                + tableName
                                + ".");
                return false;
            }

            String query =
                    "INSERT INTO "
                            + db.getSchema()
                            + "."
                            + tableName
                            + " (locationid, longname, shortname, nodetype) "
                            + "VALUES ('"
                            + location.locationid
                            + "','"
                            + location.longname
                            + "','"
                            + location.shortname
                            + "','"
                            + location.nodetype.toString()
                            + "');";

            statement.executeUpdate(query);

            System.out.println(
                    "[Location.insertLocation]: Location "
                            + location.locationid
                            + " successfully inserted into table "
                            + tableName
                            + ".");
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    // true if updated, false if had to insert

    /**
     * Update the data for a specified location, if it doesn't exist, add it.
     *
     * @param location The location to update
     * @return True if the location is updated, false if an insertion was needed
     */
    public boolean updateLocation(Locations location) {
        Statement statement;
        try {
            String exists =
                    "SELECT EXISTS (SELECT FROM "
                            + db.getSchema()
                            + "."
                            + tableName
                            + " WHERE nodeid = '"
                            + location.locationid
                            + "');";

            statement = db.createStatement();
            statement.execute(exists);
            ResultSet results = statement.getResultSet();
            results.next();

            if (!results.getBoolean("exists")) {
                System.out.println(
                        "[LocationTable.updateLocation]: Location "
                                + location.locationid
                                + " doesn't exist in table "
                                + tableName
                                + ".");
                insertLocation(location);
                return false;
            }

            String query =
                    "UPDATE "
                            + db.getSchema()
                            + "."
                            + tableName
                            + " SET"
                            + " locationid = '"
                            + location.locationid
                            + "',nodetype = '"
                            + location.nodetype
                            + "',longname = '"
                            + location.longname
                            + "',shortname = '"
                            + location.shortname
                            + "' WHERE locationid = '"
                            + location.locationid
                            + "'";

            statement.executeUpdate(query);
            System.out.println(
                    "[LocationTable.updateNode]: Successfully updated node "
                            + location.locationid
                            + " in table "
                            + tableName
                            + ".");
            return true;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    /**
     * Remove a location from the table.
     *
     * @param location location to remove
     */
    public void removeNode(Locations location) {
        Statement statement;
        try {
            String query =
                    "DELETE FROM " + db.getSchema() + "." + tableName
                            + " WHERE locationid = '" + location.locationid + "'";
            statement = db.createStatement();
            statement.executeUpdate(query);
            System.out.println(
                    "[LocationTable.removeLocation]: Location "
                            + location.locationid
                            + " has been successfully removed from table "
                            + tableName
                            + ".");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Import a CSV as location in the table.
     *
     * @param filePath Path of the file to import from CSV
     */
    public void importCSV(String filePath) {

        System.out.println("[LocationTable.importCSV]: Importing CSV to table " + tableName + ".");

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String line = br.readLine();
            while ((line = br.readLine()) != null) {

                String[] values = line.split(",");

                String locationID = values[0];

                String longName = values[1];

                String shortName = values[2];

                NodeType type = NodeType.valueOf(values[3]);

                Locations location = new Locations(locationID, longName, shortName, type);

                insertLocation(location);
            }
            br.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Export location in the table as a CSV
     *
     * @param filePath Path of the file of the CSV to export to
     */
    public void exportCSV(String filePath) {

        System.out.println("[Location.exportCSV]: exporting CSV from table " + tableName + ".");

        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filePath)));
            String grabAll = "SELECT * FROM " + db.getSchema() + "." + tableName + ";";
            Statement statement = db.createStatement();
            statement.execute(grabAll);
            ResultSet results = statement.getResultSet();

            out.println(
                    headers.get(0)
                            + ","
                            + headers.get(1)
                            + ","
                            + headers.get(2)
                            + ","
                            + headers.get(3));

            while (results.next()) {
                // System.out.println(results.getRow());  // Removed for cleanliness, feel free to restore
                out.println(
                        results.getString(headers.get(0))
                                + ","
                                + results.getDouble(headers.get(1))
                                + ","
                                + results.getDouble(headers.get(2))
                                + ","
                                + results.getString(headers.get(3)));
            }

            out.close();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
