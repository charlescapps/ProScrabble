package capps.scrabble.acm; 

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import capps.scrabble.AIPlayer;
import capps.scrabble.BadStateException;
import capps.scrabble.MoveScore;
import capps.scrabble.Rack;
import capps.scrabble.ScrabbleBoard;
import capps.scrabble.ScrabbleDict;
import capps.scrabble.ScrabbleConstants;

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

	private static ScrabbleBoard sBoard; //The test board from file
	private static ScrabbleDict dict;   //dictionary object
	private static AIPlayer ai;        //AI to check noob's move against the optimal
    private static String initRack;    //Test case rack from file
	private static String noobMoveStr; //The move string input from stdin

	public static void main (String[] args) 
        throws IOException,  ScrabbleException {
        
        if (args.length != 3) {
            o.println("Error: need exactly 3 arguments."); 
            o.println(USAGE); 
            System.exit(1); 
        }
		
		o.println("TEST FILE: " + args[2]); 

		try {
			readInState(args);
		}
		catch (BadStateException e) {
			o.println(e.getMessage()); 
			System.exit(1); 
		}

		getMoveFromStdin(); //Get the noob's move for this test case

		o.println(); 
		o.println("YOUR MOVE: \"" + noobMoveStr + "\"" + NL);

		Rack testCaseRack = null; 

		try {
			testCaseRack = new Rack(initRack);
		}
		catch (ScrabbleException e) {
			o.println("INVALID TEST CASE: initial rack \"" + initRack + "\" was bad."); 
			System.exit(1); 
		}

		ScrabbleMove noobMove = null; 

		try {
			noobMove = new ScrabbleMove(noobMoveStr); 
		}
		catch (BadStateException e) {
			o.println(e.getMessage()); 
			System.exit(1); 
		}

		if (!ScrabbleConstants.containsLetters(noobMove.tilesUsed, initRack)) {
			o.println("ERROR: your move used tiles you don't actually have!");
			System.exit(1); 
		}

		//Check that reading in their move string, then spitting it out again
		//gives the same result
		//o.println("CHECK PARSE CODE (should be the same as your input move)> "); 
		//o.println(noobMove.toAcmString());

		if (!sBoard.isValidMove(noobMove)) {
			o.println("Your move is invalid! Placing move on board so you can see why!"); 
			sBoard.forceMove(noobMove); 

			o.println(); 
			o.println("****************AFTER YOUR INVALID MOVE********************"); 
			o.println(sBoard); 

			System.exit(1); 
		}

        int noobScore = sBoard.makeMove(noobMove); 

        o.println("*****************BOARD STATE AFTER MOVE****************");
        o.println(); 
        o.println(sBoard); 

		o.println("Congrats! You played a valid move!"); 
		o.println("YOUR SCORE = " + noobScore); 

		MoveScore best = ai.getBestMove(sBoard); 

		o.println(); 

		if (noobScore < best.score) {
			o.println(":-( Your score is less than the optimal score of " + best.score + "."); 
		}
		else if (noobScore == best.score) {
			o.println("Good job! You got the optimal score for this board!"); 
		}
		else {
			o.println("You got a better score than what Charles thought was the best score...!"); 
			o.println("His AI says that " + best.score + " is the best!"); 
			o.println("No one shold ever see this message..."); 
		}

	}

	private static void getMoveFromStdin() throws IOException{
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in)); 

		String line=null;
		while ( (line = in.readLine()) != null) {

			String[] tokens = line.split(":");
			if (tokens.length >= 2 && tokens[0].toUpperCase().equals(SCRBL_MOVE)) {
				noobMoveStr = line;
				break; 
			}
		}

		if (noobMoveStr == null) {
			o.println("ERROR: No scrabble move found in standard in.");
			o.println("FORMAT: " + ScrabbleMove.ACM_FORMAT); 
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
		o.println("Setting state from test file..."); 

		//Get the line with the rack 
		String firstLine = stateFile.readLine(); 
		String[] tokens = firstLine.split(":");
		if (tokens.length < 2 || !tokens[0].toUpperCase().equals(SCRBL_RACK))
			throw new BadStateException("INVALID TEST FILE: " + NL +
					"FORMAT: \"scrbl_rack: [A-Za-z\\*]+\""); 

		initRack = tokens[1].trim(); 

		//Grabbed rack from file, now initialize board from stream
		
		//First line in stream must be scrbl_board:
		firstLine = stateFile.readLine(); 
		tokens = firstLine.split(":"); 

		if (!tokens[0].toUpperCase().equals(SCRBL_BOARD)) {
			throw new BadStateException("INVALID TEST FILE: " + NL +
					"FORMAT: scrbl_board:\\n <board goes on 15 lines here>");
		}

        sBoard = new ScrabbleBoard(layoutFile, stateFile, dict); 

		o.println(); 

		o.println("TEST RACK> " + initRack); 
        o.println("************TEST BOARD************"); 
        o.println(sBoard); 

		//Make a new AI with this rack and dictionary.
	    ai = new AIPlayer(initRack, dict); 
    }

}
