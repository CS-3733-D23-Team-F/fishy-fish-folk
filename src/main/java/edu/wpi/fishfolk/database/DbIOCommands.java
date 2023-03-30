package edu.wpi.fishfolk.database;
/**
 * @author: Charles Anderson @Updated: 3/26/2023
 * @desc: The Database CLI interface.
 */

// TODO: method that handles null cases.
// TODO: make this work with bash or WinCMD prompt
// TODO: In GIT PUT THIS INTO YOUR OWN FEATURE BRANCH along with the other features you are working
// TODO: This program assumes the user has all of the knowledge of the nodes.
// TODO: this assumes that node and edge IDs are unique
// TODO: this assumes that node and edge are stored in two separate CSVs and two separate tables
/*TODO: Implement these commands
    "help" - This should display all the commands and their options, do this second
    "exit" - This should exit to the CLI, do this first,
    "remove" - this should remove an entry from the database (follows the same path as the insert), this just requires the ID
          "node"
          "edge"
    "insert" - this should insert and entry into the database (follows the same path as remove), this requires the whole thing with attributes
          "node"
          "edge"
     "update" - this should update the database this requires the whole thing with attributes
          "node"
          "edge"
    "csv" - be able to insert or export CSVs
           "import"
                  -Await for filepath
           "export"
                  -Await for filepath
    "show" - this should show something from the database, given an ID
          "node"
          "edge"



    TODO: my idea was that for each command was that they would open into a sub-prompt and then you would type in more specified parameters from
*/

// Import Statements
import edu.wpi.fishfolk.pathfinding.Edge;
import edu.wpi.fishfolk.pathfinding.Node;
import edu.wpi.fishfolk.pathfinding.NodeType;
import java.sql.*;
import java.util.Scanner;
import java.util.StringTokenizer;
import javafx.geometry.Point2D;
import lombok.Setter;

public class DbIOCommands {

  @Setter public NodeTable nt;

  @Setter public EdgeTable et;
  /** @usage: generic constructor */
  public DbIOCommands() {}

  // CLI interface, baically orchestrates user process
  /**
   * cycleCLI
   *
   * @desc: this is supposed to encapsulate the whole process of the CLI interface
   * @param: (none)
   */
  public void cycleCLI() {
    while (true) {
      lineChk();
    }
  }

  /**
   * displayPrompt
   *
   * @desc: this is supposed to display a special prompt
   * @param: (none)
   */
  public void displayPrompt() {
    System.out.println("FFF$");
  }

  // that command and passes it to one of the other methods
  /**
   * lineChk
   *
   * @desc: waits and parses the first layer of commands
   * @param: none
   */
  public boolean lineChk() {
    displayPrompt();
    Scanner sc1 = new Scanner(System.in);
    String userInput1 = sc1.nextLine();

    switch (userInput1) {
      case "exit":
        exitCmd();
        break;
      case "help":
        helpCmd();
        return true;
      case "remove":
        eChk("remove");
        return true;
      case "insert":
        eChk("insert");
        return true;
      case "update":
        eChk("update");
        return true;
      case "csv":
        importExportChk();
        return true;
      case "show":
        eChk("show");
        return true;
      case "reset":
        lineChk();
        break;
      default:
        System.out.println(
            "Could not recognize command, please try again\n"); // Could be expanded with handling
        // an exception.
        return false;
    }
    return false;
  }

  /**
   * eChk
   *
   * @desc: parses a node or an edge input
   * @param: previous command specified
   * @return: false if invalid input
   */
  public boolean eChk(String prevCmd) {
    displayPrompt();
    // Parser
    Scanner sc2 = new Scanner(System.in);
    Scanner sc3 = new Scanner(System.in); // to be used inside the conditionals
    String userInput2 = sc2.nextLine();
    String userInput3; // to be referred to inside the conditionals

    switch (userInput2) {
      case "node":
        if (prevCmd.equals("remove")) {
          System.out.println("give node id");
          displayPrompt();
          userInput3 = sc3.nextLine();
          removeNode(userInput3);
          return true;
        } else if (prevCmd.equals("insert")) {
          System.out.println(
              "give node details in this format without the brackets: {xcoord,ycoord,floor,building,nodetype,longname,shortname}");
          displayPrompt();
          userInput3 = sc3.nextLine();
          insertNode(userInput3);
          return true;
        } else if (prevCmd.equals("update")) {
          System.out.println(
              "give node details in this format without the brackets: {xcoord,ycoord,floor,building,nodetype,longname,shortname}");
          displayPrompt();
          userInput3 = sc3.nextLine();
          updateNode(userInput3);
          return true;
        } else if (prevCmd.equals("show")) {
          System.out.println("give node id");
          displayPrompt();
          userInput3 = sc3.nextLine();
          showNode(userInput3);
          return true;
        }
        break;
      case "edge":
        if (prevCmd.equals("remove")) {
          System.out.println("give edge id");
          displayPrompt();
          userInput3 = sc3.nextLine();
          removeEdge(userInput3);
          return true;
        } else if (prevCmd.equals("insert")) {
          System.out.println(
              "give edge {edgeid,startnode,endnode} in that format without the brackets");
          displayPrompt();
          userInput3 = sc3.nextLine();
          insertEdge(userInput3);
          return true;
        } else if (prevCmd.equals("update")) {
          System.out.println(
              "give edge {edgeid,startnode,endnode} in that format without the brackets");
          displayPrompt();
          userInput3 = sc3.nextLine();
          updateEdge(userInput3);
          return true;
        } else if (prevCmd.equals("show")) {
          System.out.println("give edge id");
          displayPrompt();
          userInput3 = sc3.nextLine();
          showEdge(userInput3);
          return true;
        }
        break;
      case "reset":
        lineChk();
        break;
      default:
        System.out.println(
            "Could not recognize command, please try again\n"); // Could be expanded with handling
        // an exception.
        return false;
    }

    return false;
  }

  /**
   * importExportChk
   *
   * @desc: parses a node or an edge input, this layer after this one is the node/edge decision
   *     layer
   * @return false if fails
   */
  public boolean importExportChk() {
    displayPrompt();
    // parser
    Scanner sc1 = new Scanner(System.in);
    Scanner sc2 = new Scanner(System.in);
    Scanner sc3 = new Scanner(System.in);
    String userInput1 = sc1.nextLine(); // for import or export
    String userInput2; // for "node or edge"
    String userInput3; // for filepath

    switch (userInput1) {
      case "import":
        displayPrompt();
        userInput2 = sc2.nextLine();
        if (userInput2.equals("node")) {
          // ask for file path, check if it is valid, and put it into the specific function
          System.out.println("Desired filepath?");
          displayPrompt();
          userInput3 = sc3.nextLine();
          // Specified function execution
          importNodeCSV(userInput3);
        } else if (userInput2.equals("edge")) {
          // ask for file path, check if it is valid, and put it into the specific function
          System.out.println("Desired filepath?");
          displayPrompt();
          userInput3 = sc3.nextLine();
          // Specified function execution
          importEdgeCSV(userInput3);
        }
        break;
      case "export":
        displayPrompt();
        userInput2 = sc2.nextLine();
        if (userInput2.equals("node")) {
          // ask for file path, check if it is valid, and put it into the specific function
          System.out.println("Desired filepath?");
          displayPrompt();
          userInput3 = sc3.nextLine();
          // Specified function execution
          exportNodeCSV(userInput3);
        } else if (userInput2.equals("edge")) {
          // ask for file path, check if it is valid, and put it into the specific function
          System.out.println("Desired filepath?");
          displayPrompt();
          userInput3 = sc3.nextLine();
          // Specified function execution
          exportEdgeCSV(userInput3);
        }
        break;
      case "reset":
        lineChk();
        break;
      default:
        System.out.println(
            "Could not recognize command, please try again\n"); // Could be expanded with handling
        // an exception.
        return false;
    }

    return false;
  }

  // =============================================HARDCODING CMD METHODS

  /**
   * exitCmd
   *
   * @desc: the program's CLI exits to OS CLI
   * @param: (none)
   */
  public void exitCmd() {
    System.exit(0);
  }

  /**
   * help
   *
   * @desc: displays the help table for the rest of the commands
   * @param: (none)
   */
  public void helpCmd() {
    System.out.println("Help Menu");
    System.out.println(
        "    Commands: FIRST you choose an initial command, just type in the name of the command and it will bring you to a further prompt\n");
    System.out.println(
        "  show          -with an an ID of an edge or a node it can display the statistics\n");
    System.out.println("  csv\n");
    System.out.println(
        "     export     -Typed in after entering csv, will be used to export a CSV, node or edge must be specified after typing this command,\n");
    System.out.println(
        "     import     -Typed in after entering csv, will be used to import a CSV, node or edge must be specified after typing this command\n");
    System.out.println("  insert        -Insert a single node or edge\n");
    System.out.println("  update        -Updates an already existing node or edge\n");
    System.out.println("  remove        -Remove an already existing node or edge\n");
    System.out.println("  help          -brings up the help menu\n");
    System.out.println("  reset         -brings up to the FIRST level of commands.\n");
    System.out.println("  exit          -exits the program\n");
    System.out.println(
        "    Commands: SECOND, Then you choose an second command, just type in the name of the command and it will bring you to a further\n");
    System.out.println("  node          -specifies a node option for the previous command\n");
    System.out.println("  edge          -specifies an edge option for the previous command\n");
    System.out.println(
        "    Commands: THIRD commands will most likely ask you to input a specific amount of data in a certain format. Just insert the data in the way it prompts and you should be set. Data should be separated by commas ',' and be noted without the brackets \n");
    System.out.println(
        "  {filepath}    -Just type in the filepath to where you would want to import or export, this should include the name to the .csv file");
    System.out.println(
        "  {edgeid,startnode,endnode} -This is prompt for when you want to update or insert a new edge, without the brackets");
    System.out.println(
        "  {nodeid,xcoord,ycoord,floor,building,nodetype,longname,shortname} -This is prompt for when you want to update or insert a new node, without the brackets");
    System.out.println("  {edgeid}      -prompt to just enter in the edgeid");
    System.out.println("  {nodeid}      -prompt to enter in just the nodeid");

    System.out.println("\n");
  }

  /**
   * nodeParser
   *
   * @desc
   * @param
   */
  public Node nodeParser(String line) {
    // parses the long input from the CMD line, delimeter is a comma
    StringTokenizer temp = new StringTokenizer(line, ",");

    // {nodeid,xcoord,ycoord,floor,building,nodetype,longname,shortname}
    String id = temp.nextToken();
    String xcoord = temp.nextToken();
    String ycoord = temp.nextToken();
    Point2D point = new Point2D(Double.valueOf(xcoord), Double.valueOf(ycoord)); // x first, then y
    String floor = temp.nextToken();
    String building = temp.nextToken();
    NodeType type = NodeType.valueOf(temp.nextToken()); // this SHOULD work
    String longname = temp.nextToken();
    String shortname = temp.nextToken();

    Node n = new Node(id, point, floor, building, type, longname, shortname);
    return n;
  }

  /**
   * edgeParser
   *
   * @desc
   * @param
   */
  public Edge edgeParser(String line) {
    // parses the long input from the CMD line, delimiter is comma
    StringTokenizer temp = new StringTokenizer(line, ",");

    // {edgeid,startnode,endnode}
    String edgeid = temp.nextToken();
    String startnode = temp.nextToken();
    String endnode = temp.nextToken();

    Edge e = new Edge(edgeid, startnode, endnode);

    return e;
  }

  // TODO: insertNode method
  /**
   * insertNode
   *
   * @param line, parses the long, comma separated string and breaks it down.
   * @desc can be used before node or edge methods, return false if fails
   */
  public void insertNode(String line) {
    Node n = nodeParser(line);
    nt.insertNode(n);
  }

  // TODO: insertEdge method
  /**
   * insertEdge
   *
   * @param line, parses the long comma separated string and breaks it down.
   * @desc can be used before node or edge methods, return false if fails
   */
  public void insertEdge(String line) {
    Edge e = edgeParser(line);

    et.insertEdge(e);
  }

  /**
   * removeNode
   *
   * @param, nodeid is what is used to indicate the removed node
   * @desc can be used before node or edge methods, will automatically decide in an if statement if
   *     a node or
   */
  public void removeNode(String nodeid) {
    nt.removeNode(nt.getNode(nodeid));
  }

  /**
   * removeEdge
   *
   * @param, edgeid is what is used to indicate the removed edge
   * @desc can be used before node or edge methods, will automatically decide in an if statement if
   *     a node or
   */
  public void removeEdge(String edgeid) {
    et.removeEdge(et.getEdge(edgeid));
  }

  /**
   * showNode
   *
   * @desc: displays the information in the database (specific node or edge), takes you to a prompt
   *     where you specify the node or edge id, returns false if you cannot show.
   * @param: nodeid
   */
  public void showNode(String nodeid) {
    Node n = nt.getNode(nodeid);

    // printout node statistics
    System.out.println("Node Statistics\n");
    System.out.println("nodeid: " + n.id);
    System.out.println("xcoord: " + n.point.getX());
    System.out.println("ycoord: " + n.point.getY());
    System.out.println("floor: " + n.floor);
    System.out.println("building: " + n.building);
    System.out.println("nodetype: " + n.type.toString());
    System.out.println("longname: " + n.longName);
    System.out.println("shortname: " + n.shortName);
  }

  /**
   * showEdge
   *
   * @desc: displays the information in the database (specific node or edge), takes you to a prompt
   *     where you specify the node or edge id, returns false if you cannot show.
   * @param: id
   */
  public void showEdge(String edgeid) {
    Edge e = et.getEdge(edgeid);

    // prinout edge statistics
    System.out.println("Edge Statistics\n");
    System.out.println("edgeid: " + e.edgeid);
    System.out.println("startnode: " + e.startnode);
    System.out.println("endnode: " + e.endnode);
  }

  /**
   * updateNode
   *
   * @desc: will update an node in the database
   * @param:
   * @return: false if fails
   */
  public void updateNode(String line) {
    Node n = nodeParser(line);
    nt.updateNode(n);
  }

  /**
   * updateEdge
   *
   * @desc: will update an edge in the database
   * @param:
   * @return: false if fails
   */
  public void updateEdge(String line) {

    Edge e = edgeParser(line);
    et.updateEdge(e);
  }

  // ======================================CSV craziness

  /**
   * exportNodeCSV
   *
   * @param
   * @desc
   * @return: false if the operation failed, true if it passed
   */
  public boolean exportNodeCSV(String filepath) {
    nt.exportCSV(filepath);
    return false;
  }

  /**
   * exportEdgeCSV
   *
   * @param filepath,
   * @desc
   * @return: false if it fails
   */
  public void exportEdgeCSV(String filepath) {
    et.exportCSV(filepath);
  }

  /**
   * importNodeCSV
   *
   * @param filepath, needs to insert the st
   * @desc
   * @return: false if it fails
   */
  public void importNodeCSV(String filepath) {
    nt.importCSV(filepath);
  }

  /**
   * importEdgeCSV
   *
   * @param filepath
   * @desc
   * @return: false if it fails
   */
  public void importEdgeCSV(String filepath) {
    et.importCSV(filepath);
  }
}
