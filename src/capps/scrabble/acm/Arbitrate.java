package capps.scrabble.acm; 

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import capps.scrabble.AIPlayer;
import capps.scrabble.BadStateException;
import capps.scrabble.MoveScore;
import capps.scrabble.ScrabbleBoard;
import capps.scrabble.ScrabbleDict;

import static capps.scrabble.ScrabbleConstants.*;
import capps.scrabble.ScrabbleException;
import capps.scrabble.ScrabbleMove;

/**
 *
 * This class will take the test file name as an argument, and will read in
 * the n00b's move from standard in. 
 *
 * It will then output the test results. 
 *
 **/

public class Arbitrate {
	private final static String USAGE 
		= "java -jar bestmove.jar \"layout_file\" \"dict_text_file\" \"test_file\""; 

	private static BufferedReader layoutFile; 
	private static BufferedReader stateFile; 
	private static BufferedReader dictFile; 

	private static ScrabbleBoard sBoard; 
	private static ScrabbleDict dict; 
    private static String initRack;
    private static AIPlayer ai;
	private static String noobMoveStr; 

	public static void main (String[] args) 
        throws IOException, BadStateException, ScrabbleException {
        
        if (args.length != 3) {
            o.println("Error: need exactly 3 arguments."); 
            o.println(USAGE); 
            System.exit(1); 
        }

		o.println("TEST FILE: " + args[2]); 

        readInState(args); 
		getMoveFromStdin();

		o.println("YOUR MOVE STRING> " + noobMoveStr + NL);

		ScrabbleMove noobMove = new ScrabbleMove(noobMoveStr); 

		o.println("PARSED> ");
		o.println(noobMove.toAcmString()); 

        int noobScore = sBoard.makeMove(noobMove); 

        o.println("*****************BOARD STATE AFTER MOVE****************");
        o.println(); 
        o.println(sBoard); 

		o.println("YOUR SCORE> " + noobScore); 

	}

	private static void getMoveFromStdin() throws IOException{
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in)); 

		String line=null;
		while ( (line = in.readLine()) != null) {
			String[] tokens = line.split(" \t");
			if (tokens.length >= 2 && tokens[0].toUpperCase().equals("SCRBL_MOVE:"))
				noobMoveStr = tokens[1];
		}

		if (noobMoveStr == null) {
			o.println("ERROR: No scrabble move found in standard in.");
			System.exit(1); 
		}
	}

    private static void readInState(String[] args) 
        throws BadStateException, ScrabbleException, FileNotFoundException, IOException{
		layoutFile = new BufferedReader(new FileReader(args[0])); 
		stateFile = new BufferedReader(new FileReader(args[2])); 
		dictFile = new BufferedReader(new FileReader(args[1])); 

		o.println();
		o.println("Loading scrabble dictionary from \"" + args[1] + "\""); 
		dict = new ScrabbleDict(dictFile); 

		o.println("Loading scrabble layout from \"" + args[0] + "\""); 
		o.println("Setting state from standard in..."); 
        sBoard = new ScrabbleBoard(layoutFile, stateFile, dict); 

		o.println(); 

        o.println("*****************TEST BOARD*****************:"); 
        o.println(sBoard); 

	    ai = new AIPlayer(initRack, dict); 
    }

}
