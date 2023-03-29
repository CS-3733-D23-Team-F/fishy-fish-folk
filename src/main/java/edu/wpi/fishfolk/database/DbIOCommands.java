package edu.wpi.fishfolk.database;
/**
 * @author: Charles Anderson @Updated: 3/26/2023
 * @desc: The Database CLI interface.
 */

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
import edu.wpi.fishfolk.database.EdgeTable;
import edu.wpi.fishfolk.database.NodeTable;
import java.sql.*;
import java.util.Scanner;


public class DbIOCommands {

  /** @usage: generic constructor */
  public DbIOCommands() {}

  // TODO: cycleCLI method which calls display prompt, then call to the line check, then reset the
  // CLI interface, baically orchestrates user process
  /**
   * cycleCLI
   *
   * @desc: this is supposed to encapsulate the whole process of the CLI interface
   * @param: (none)
   */
  public void cycleCLI() {
    while(true) {
      displayPrompt();
      lineChk();
      //TODO: there might need to be a third function to flush out the cli
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

  // TODO: linechecker method (from Main.java) that waits for a user to type into a command, parses
  // that command and passes it to one of the other methods
  /**
   * lineChk
   *
   * @desc: waits and parses the first layer of commands
   * @param: none
   */
  public boolean lineChk() {
    Scanner sc1 = new Scanner(System.in);
    String userInput1 = sc1.nextLine();

    switch (userInput1) {
      case "exit":
        exitCmd();
        break;
      case "help":
        helpCmd();
        break;
      case "remove":
        eChk("remove");
        break;
      case "insert":
        eChk("insert");
        break;
      case "update":
        eChk("update");
        break;
      case "csv":
        importExportChk();
        break;
      case "show":
        eChk("show");
        break;
      default:
        System.out.println("Could not recognize command, please try again\n"); //Could be expanded with handling an exception.
        return false;
    }
    return false;
  }

  //TODO: element specifier (node or edge) layer, this will take in and read for subcommands
  /**
   * eChk
   *
   * @desc: parses a node or an edge input
   * @param: previous command specified
   * @return: false if invalid input
   */
  public boolean eChk(String prevCmd){
    // TODO: Parser
    Scanner sc2 = new Scanner(System.in);
    Scanner sc3 = new Scanner(System.in); //to be used inside the conditionals
    String userInput2 = sc2.nextLine();
    String userInput3; //to be referred to inside the conditionals

    switch(userInput2){
      case "node":
        if (prevCmd =="remove"){
          System.out.println("give node id");
          displayPrompt();
          userInput3 = sc3.nextLine();
          removeNode(userInput3);
          return true;
        } else if (prevCmd == "insert"){
          System.out.println("give node details in this format without the brackets: {xcoord,ycoord,floor,building,nodetype,longname,shortname}");
          displayPrompt();
          userInput3 = sc3.nextLine();
          insertNode(userInput3);
          return true;
        } else if (prevCmd == "update"){
          System.out.println("give node details in this format without the brackets: {xcoord,ycoord,floor,building,nodetype,longname,shortname}");
          displayPrompt();
          userInput3 = sc3.nextLine();
          updateNode(userInput3);
          return true;
        } else if (prevCmd == "show"){
          System.out.println("give node id");
          displayPrompt();
          userInput3 = sc3.nextLine();
          showNode(userInput3);
          return true;
        }
        break;
      case "edge":
        if (prevCmd =="remove"){

          System.out.println("give edge id");
          displayPrompt();
          userInput3 = sc3.nextLine();
          removeEdge(userInput3);
        } else if (prevCmd == "insert"){
          System.out.println("give edge");

        } else if (prevCmd == "update"){

        } else if (prevCmd == "show"){
          System.out.println("give edge id");
          displayPrompt();

        }
        break;
      default:
        System.out.println("Could not recognize command, please try again\n"); //Could be expanded with handling an exception.
        return false;
    }

    return false;
  }

  //TODO: CSV import export decision layer
  /**
   * importExportChk
   *
   * @desc: parses a node or an edge input, this layer after this one is the node/edge decision layer
   * @param: previous command specified
   * @return false if fails
   */
  public boolean importExportChk(){
    // TODO: Parser
    Scanner sc = new Scanner(System.in);
    String userInput = sc.nextLine();

    switch(userInput){
      case "import":
        break;
      case "export":
        break;
      case "filepath":
        break;
      default:
        System.out.println("Could not recognize command, please try again\n"); //Could be expanded with handling an exception.
        return false;
    }

    return false;
  }

  //TODO: filepath parser
  /**
   * fpChk
   *
   * @desc: parses a filepath and is called after certian other method for pipeline that require FPs like import and export CSV.
   * @param: none
   */
  public boolean fpChk(){
    // TODO: Parser
    Scanner sc = new Scanner(System.in);
    String userInput = sc.nextLine();

    //TODO: if it is not a valid filepath, reject it.
    /*
    if(userInput){

      return true;
    }else{
      System.out.println("This filepath cannot be reached");
      return false;
    }
    */
    return false;
  }

  //=============================================HARDCODING CMD METHODS

  /**
   * exit
   *
   * @desc: the program's CLI exits to OS CLI
   * @param: (none)
   */
  public void exitCmd() {
    System.exit(0);
  }

  // TODO: help method
  /**
   * help
   *
   * @desc: displays the help table for the rest of the commands
   * @param: (none)
   */
  public void helpCmd() {
    System.out.println("Help Menu");
    System.out.println("    Commands: First you choose an initial command, just type in the name of the command and it will bring you to a further prompt\n");
    System.out.println("  show\n");
    System.out.println("  csv\n");
    System.out.println("     export     -Typed in after entering csv, will be used to export a CSV,\n");
    System.out.println("     import     -Typed in after entering csv, will be used to import a CSV,\n");
    System.out.println("  insert        -Insert a single node or edge\n");
    System.out.println("  update\n");
    System.out.println("  remove\n");
    System.out.println("  help\n");
    System.out.println("  exit\n");
    System.out.println("    Commands: Then you choose an second command, just type in the name of the command and it will bring you to a further\n");
    System.out.println("  node          -specifies a node option for the previous command\n");
    System.out.println("  edge          -specifies an edge option for the previous command\n");
    System.out.println("    Commands: Third commands will most likely ask you to input a specific amount of data in a certain format. Just insert the data in the way it prompts and you should be set. Data should be separated by commas ',' and be noted without the brackets \n");
    System.out.println("  {filepath}    -Just type in the filepath to where you would want to import or export, this should include the name to the .csv file");
    System.out.println("  {startnode,endnode} -This is prompt for when you want to update or insert a new edge");
    System.out.println("  {xcoord,ycoord,floor,building,nodetype,longname,shortname} -This is prompt for when you want to update or insert a new node");
    // TODO: insert entries for commands. Consult photo for reference


    System.out.println("\n");
  }

  //TODO: insertNode method
  /**
   * insertNode
   *
   * @param line, parses the long, comma separated string and breaks it down.
   * @desc can be used before node or edge methods, return false if fails
   */
  public boolean insertNode(String line){
    //TODO: parses the long input from the CMD line


    return false;
  }

  //TODO: insertEdge method
  /**
   * insertEdge
   *
   * @param line, parses the long comma separated string and breaks it down.
   * @desc can be used before node or edge methods, return false if fails
   */
  public boolean insertEdge(String line){
    //parses the long input from the CMD line

    return false;
  }

  //TODO: removeNode method
  /**
   * removeNode
   *
   * @param, element tells you what command wa srun before it
   * @desc can be used before node or edge methods, will automatically decide in an if statement if a node or
   */
  public boolean removeNode(String nodeid){
    NodeTable.removeNode(NodeTable.getNode(nodeid));
    return false;
  }

  //TODO: removeEdge method
  /**
   * removeEdge
   *
   * @param, element tells you what command wa srun before it
   * @desc can be used before node or edge methods, will automatically decide in an if statement if a node or
   */
  public boolean removeEdge(String edgeid){
    EdgeTable.removeEdge(EdgeTable.getEdge(edgeid));
    return false;
  }

  //TODO: showNode method
  /**
   * showNode
   *
   * @desc: displays the information in the database (specific node or edge), takes you to a prompt where you specify the node or edge id, returns false if you cannot show.
   * @param: id
   * @return: false if fails
   */
  public boolean showNode(String nodeid){
    Node n = NodeTable.getNode(nodeid);

    //printout node statistics
    System.out.println("Node Statistics\n");
    System.out.println("nodeid: " + n.id);
    System.out.println("xcoord: " + n.point.getX());
    System.out.println("ycoord: " + n.point.getY());
    System.out.println("floor: " + n.floor);
    System.out.println("building: " + n.building);
    System.out.println("nodetype: " + n.type);
    System.out.println("longname: " + n.longName);
    System.out.println("shortname: " + n.shortName);

    return false;
  }

  //TODO: showEdge method
  /**
   * showEdge
   *
   * @desc: displays the information in the database (specific node or edge), takes you to a prompt where you specify the node or edge id, returns false if you cannot show.
   * @param: id
   * @return: false if fails
   */
  public boolean showEdge(String edgeid){
    Edge e = EdgeTable.getEdge(edgeid);

    //prinout edge statistics
    System.out.println();

    return false;
  }

  //TODO: updateNode method
  /**
   * updateNode
   *
   * @desc: will update an node in the database
   * @param:
   * @return: false if fails
   */
  public boolean updateNode(String nodeid){
    NodeTable.updateNode(NodeTable.getNode(nodeid));
    return false;
  }

  //TODO: updateEdge method
  /**
   * updateEdge
   *
   * @desc: will update an edge in the database
   * @param:
   * @return: false if fails
   */
  public boolean updateEdge(String edgeid){
    EdgeTable.updateEdge(EdgeTable.getEdge(edgeid));
    return false;
  }

  //======================================CSV craziness

  //TODO: csv exportNode method
  /**
   * exportNodeCSV
   *
   * @param
   * @desc
   * @return: false if the operation failed, true if it passed
   */
  public boolean exportNodeCSV(String filepath){
    NodeTable.exportCSV(filepath);
    return false;
  }

  //TODO: csv exportEdge method
  /**
   * exportEdgeCSV
   *
   * @param filepath,
   * @desc
   * @return: false if it fails
   */
  public void exportEdgeCSV(String filepath){
    EdgeTable.exportCSV(filepath);
  }

  //TODO: csv importNode method
  /**
   * importNodeCSV
   *
   * @param filepath, needs to insert the st
   * @desc
   * @return: false if it fails
   */
  public void importNodeCSV(String filepath){
    NodeTable.importCSV(filepath);
  }

  //TODO: csv importEdge method
  /**
   * importEdgeCSV
   *
   * @param filepath
   * @desc
   * @return: false if it fails
   */
  public void importEdgeCSV(String filepath){
    EdgeTable.importCSV(filepath);
  }


}
