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

public class BestMoveMain {
	private final static String USAGE 
		= "java -jar bestmove.jar \"layout_file\" \"dict_text_file\""; 

	private static BufferedReader layoutFile; 
	private static BufferedReader stateFile; 
	private static BufferedReader dictFile; 

	private static ScrabbleBoard sBoard; 
	private static ScrabbleDict dict; 
    private static String initRack;
    private static AIPlayer ai;

	public static void main (String[] args) 
        throws IOException, BadStateException, ScrabbleException {
        
        if (args.length != 2) {
            o.println("Error: need exactly 2 arguments."); 
            o.println(USAGE); 
            System.exit(1); 
        }

        readInState(args); 

        o.println("************TEST BOARD************"); 
        o.println(sBoard); 

        MoveScore bestMove = ai.getBestMove(sBoard); 

		o.println("BEST MOVE:"); 
        o.println(bestMove.move.toAcmString()); 
		o.println(); 
        o.println("SCORE: " + bestMove.score); 
        o.println(); 

        sBoard.makeMove(bestMove.move); 

        o.println("********BOARD AFTER MOVE**********");
        o.println(sBoard); 

	}

    private static void readInState(String[] args) 
        throws BadStateException, ScrabbleException, FileNotFoundException, IOException{
		layoutFile = new BufferedReader(new FileReader(args[0])); 
		stateFile = new BufferedReader(new InputStreamReader(System.in)); 
		dictFile = new BufferedReader(new FileReader(args[1])); 

		//Get the line with the rack 
		String firstLine = stateFile.readLine(); 
		String[] tokens = firstLine.split(" ");
		if (tokens.length < 2 || !tokens[0].toUpperCase().equals("SCRBL_RACK:"))
			throw new BadStateException("First line must be like this:" + NL +
					"RACK: <rack_tiles>"); 

		initRack = tokens[1]; 

		o.println();
		o.println("Loading scrabble dictionary from \"" + args[1] + "\""); 
		dict = new ScrabbleDict(dictFile); 

		o.println("Loading scrabble layout from \"" + args[0] + "\""); 
		o.println("Setting board state from standard in..."); 
		//First line in stream must be scrbl_board:
		firstLine = stateFile.readLine(); 
		tokens = firstLine.split(":"); 

		if (!tokens[0].toUpperCase().equals(SCRBL_BOARD)) {
			throw new BadStateException("INVALID TEST FILE: " + NL +
					"FORMAT: scrbl_board:\\n <board goes on 15 lines here>");
		}

        sBoard = new ScrabbleBoard(layoutFile, stateFile, dict); 

		o.println(); 


	    ai = new AIPlayer(initRack, dict); 
    }

}
