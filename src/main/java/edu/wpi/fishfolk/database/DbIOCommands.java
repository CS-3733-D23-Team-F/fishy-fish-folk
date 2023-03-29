package edu.wpi.fishfolk.database;
/**
 * @author: Charles Anderson
 * @Updated: 3/26/2023
 * @desc: The Database CLI interface.
 */

//TODO: make this work with bash or WinCMD prompt
//TODO: In GIT PUT THIS INTO YOUR OWN FEATURE BRANCH along with the other features you are working on
/*TODO: Implement these commands
    "help" - This should display all the commands and their options, do this second
    "exit" - This should exit to the CLI, do this first,
    "remove"
    "insert"
*/

//Import Statements
import java.sql.*;
import java.util.Scanner;
import edu.wpi.fishfolk.database.Fdb;

public class DbIOCommands {

    /**
     * @usage: generic constructor
     */
    public DbIOCommands(){

    }

    //TODO: cycleCLI method which calls display prompt, then call to the line check, then reset the CLI interface, baically orchestrates user process
    /**
     * cycleCLI
     *
     * @desc: this is supposed to encapsulate the whole process of the CLI interface
     * @param: (none)
     */
    public void cycleCLI(){
        displayPrompt();
        lineChk();
    }

    //TODO: display prompt method which appears upon enterance and when a command is complete.
    /**
     * displayPrompt
     *
     * @desc: this is supposed to display a special prompt
     * @param: (none)
     */
    public void displayPrompt(){
        System.out.println("FFF$");
    }

    //TODO: linechecker method (from Main.java) that waits for a user to type into a command, parses that command and passes it to one of the other methods
    /**
     * @desc: waits and parses
     * @param: none
     */
    public void lineChk(){
        //TODO: Parser
        Scanner sc = new Scanner(System.in);
        String userInput = sc.nextLine();

        //TODO: Decision
        switch(userInput) {
            case "exit":
                exitCmd();
                break;
            case "help":
                helpCmd();
                break;
            case "remove":
                //code block
                break;
            case "insert":
                //code block
                break;
            default:
                System.out.println("Could not recognize command, please try again\n");
        }
    }

    //TODO: exit method
    /**
     * exit
     *
     * @desc: the program's CLI exits to OS CLI
     * @param: (none)
     *
     */
    public void exitCmd(){
        System.exit(0);
    }

    //TODO: help method
    /**
     * help
     *
     * @desc: displays the help table for the rest of the commands
     * @param: (none)
     */
    public void helpCmd(){
        System.out.println("Help Menu");
        System.out.println("    Commands:\n");
        //TODO: insert entries for commands.
        System.out.println("\n");

    }

}
