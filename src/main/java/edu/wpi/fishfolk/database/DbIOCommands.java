package edu.wpi.fishfolk.database;
/**
 * @author: Charles Anderson @Updated: 3/26/2023
 * @desc: The Database CLI interface.
 */

// TODO: make this work with bash or WinCMD prompt
// TODO: In GIT PUT THIS INTO YOUR OWN FEATURE BRANCH along with the other features you are working
// TODO: This program assumes the user has all of the knowledge of the nodes.
// TODO: this assumes that node and edge IDs are unique
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
    displayPrompt();
    lineChk();

    //TODO: there might need to be a third function to flush out the cli
  }

  // TODO: display prompt method which appears upon enterance and when a command is complete.
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
   * @desc: waits and parses
   * @param: none
   */
  public void lineChk() {
    // TODO: Parser
    Scanner sc = new Scanner(System.in);
    String userInput = sc.nextLine();

    // TODO: Decision
    switch (userInput) {
      case "exit":
        exitCmd();
        break;
      case "help":
        helpCmd();
        break;
      case "remove":
        // code block
        break;
      case "insert":
        // code block
        break;
      case "update":
        //code block
        break;
      case "csv":
        //code block
        break;
      case "show":
        //code block
        break;
      default:
        System.out.println("Could not recognize command, please try again\n");
    }

  }

  //TODO: element specifier, this will take in and read for subcommands


  //TODO: filepath parser
  /**
   * fpChk
   *
   * @desc: parses a filepath and is called after certian other method for pipeline that require FPs like import and export CSV.
   * @param: none
   */
  public void fpChk(){

  }

  //=============================================HARDCODING CMD METHODS


  // TODO: exit method
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
    System.out.println("    Commands:\n");
    // TODO: insert entries for commands.
    System.out.println("\n");
  }

  //TODO: insert method
  /**
   * insert
   *
   * @param
   * @desc can be used before node or edge methods
   */


  //TODO: remove method
  /**
   * remove
   *
   * @param
   * @desc can be used before node or edge methods
   */

  //TODO: show method
  /**
   * show
   *
   * @desc: displays the information in the database
   * @param: (none)
   */

  //TODO: node method (id only)
  /**
   * nodeid
   *
   * @param
   * @desc
   */

  //TODO: node method (full attr)
  /**
   * nodeattr
   *
   * @param
   * @desc
   */

  //TODO: edge method (id only)
  /**
   * edgeid
   *
   * @param
   * @desc
   */

  //TODO: edge method (full attr)
  /**
   * edgeattr
   *
   * @param
   * @desc
   */

  //TODO: csv export method
  /**
   * exportcsv
   *
   * @param
   * @desc
   */


  //TODO: csv import method
  /**
   * importcsv
   *
   * @param
   * @desc
   */

  //TODO: input filepath for input CSV
  /**
   * filepath
   *
   * @desc: directs to the file where they would want to import a CSV from an already existing
   * @param: (none)
   */



}
