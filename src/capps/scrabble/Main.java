package capps.scrabble; 

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;

import static capps.scrabble.ScrabbleConstants.*; 
import java.io.BufferedReader;
import java.io.FileReader;

public class Main {
	private final static String USAGE 
		= "java -jar scrabble.jar \"layout_file\" \"dict_object_file\""; 

	private static BufferedReader layoutFile; 

	private static ScrabbleBoard sBoard; 
	private static ScrabbleDict dict; 

	public static void main (String [] args) 
		throws FileNotFoundException, IOException, 
						  ScrabbleException, ClassNotFoundException
	{
		if (args.length != 2) {
			o.println(USAGE); 
			System.exit(1); 
		}

		layoutFile = new BufferedReader(new FileReader(args[0])); 

		FileInputStream fis = new FileInputStream(args[1]); 
		ObjectInputStream ois = new ObjectInputStream(fis); 

		o.println("Loading scrabble dictionary from \"" + args[1] + "\""); 
		dict = (ScrabbleDict) ois.readObject();

		o.println("Loading scrabble layout from \"" + args[0] + "\""); 
		sBoard = new ScrabbleBoard(layoutFile, dict); 
		
		testMove(); 
		testBestScores(); 
	}

	public static void testMove() {
		ScrabbleMove t1 = new ScrabbleMove(7, 7, "HATES", "HATES", DIR.S); 
		o.println("Is valid move? " + sBoard.isValidMove(t1)); 
		int score = sBoard.makeMove(t1); 
		o.println("Score: " + score); 
		o.println(sBoard); 

		ScrabbleMove t2 = new ScrabbleMove(9, 7, "TEA", "TEA", DIR.E); 
		o.println("Is valid move? " + sBoard.isValidMove(t2)); 
		score = sBoard.makeMove(t2); 
		o.println("Score: " + score); 
		o.println(sBoard); 

		ScrabbleMove t3 = new ScrabbleMove(6, 8, "BA", "BA", DIR.S); 
		o.println("Is valid move? " + sBoard.isValidMove(t3)); 
		score = sBoard.makeMove(t3); 
		o.println("Score: " + score); 
		o.println(sBoard); 

		ScrabbleMove t4 = new ScrabbleMove(1, 2, "LABORATORY", "LABORATORY", DIR.S); 
		o.println("Is valid move? " + sBoard.isValidMove(t4)); 
		score = sBoard.makeMove(t4); 
		o.println("Score: " + score); 
		o.println(sBoard); 

		ScrabbleMove t5 = new ScrabbleMove(0, 3, "LIT", "LIT", DIR.S); 
		o.println("Is valid move? " + sBoard.isValidMove(t5)); 
		score = sBoard.makeMove(t5); 
		o.println("Score: " + score); 
		o.println(sBoard); 

		ScrabbleMove t6 = new ScrabbleMove(6, 8, "BATE", "BATE", DIR.S); 
		o.println("Is valid move? " + sBoard.isValidMove(t6)); 
		score = sBoard.makeMove(t6); 
		o.println("Score: " + score); 
		o.println(sBoard); 

		ScrabbleMove t7 = new ScrabbleMove(1, 1, "ELITE", "ELITE", DIR.E); 
		o.println("Is valid move? " + sBoard.isValidMove(t7)); 
		score = sBoard.makeMove(t7); 
		o.println("Score: " + score); 
		o.println(sBoard); 
	}

	public static void testBestScores() throws ScrabbleException {
		AIPlayer p1 = new AIPlayer("AAAAAAA",dict); 
		o.println("Computing best move with rack AAAAAAA"); 

		MoveScore best = p1.getBestMove(sBoard); 

		if (best == null) {
			o.println("NO MOVE FOUND!"); 
		}
		else {
			o.println("Best Score:" + best.score); 
			o.println("Best Move:"); 
			o.println(best.move); 
			p1.playMove(sBoard,best.move); 
			o.println(sBoard); 
		}

		AIPlayer p2 = new AIPlayer("ZPAAAAA",dict); 
		o.println("Computing best move with rack ZPAAAAA"); 

		best = p2.getBestMove(sBoard); 

		if (best == null) {
			o.println("NO MOVE FOUND!"); 
		}
		else {
			o.println("Best Score:" + best.score); 
			o.println("Best Move:"); 
			o.println(best.move); 
			p2.playMove(sBoard,best.move); 
			o.println(sBoard); 
		}

		AIPlayer p3 = new AIPlayer("YZYGIAL",dict); 
		o.println("Computing best move with rack YZYGIAL"); 

		best = p3.getBestMove(sBoard); 

		if (best == null) {
			o.println("NO MOVE FOUND!"); 
		}
		else {
			o.println("Best Score:" + best.score); 
			o.println("Best Move:"); 
			o.println(best.move); 
			p3.playMove(sBoard,best.move); 
			o.println(sBoard); 
		}
	}
}
