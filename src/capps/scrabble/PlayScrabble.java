package capps.scrabble; 

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static capps.scrabble.ScrabbleConstants.*; 

public class PlayScrabble {
	private static enum MENU_CHOICE 
		{PLAY_OPPONENT_MOVE, MANUAL_MOVE, GET_BEST_MOVE, GIVE_TILES, DISPLAY, END_GAME}; 

	private AIPlayer ai; 
	private ScrabbleBoard sb; 
	private ScrabbleDict dict;
	private static final BufferedReader in 
		= new BufferedReader(new InputStreamReader(System.in)); 

	public PlayScrabble(ScrabbleBoard sb, ScrabbleDict dict) throws IOException {
		this.sb = sb; 
		this.dict = dict; 

		getInitialPlayer(); 

	}

	public void startGame() {
		gameLoop();
	}

	private void getInitialPlayer() throws IOException{
		o.println("ENTER INITIAL RACK:"); 
		try {
			ai = new AIPlayer(in.readLine(), dict); 
		}
		catch (ScrabbleException e) {
			o.println("Invalid initial rack. Try again!"); 
			o.println(e.getMessage()); 
			getInitialPlayer(); 
		}
	}

	private void gameLoop() {

		MENU_CHOICE mc=null; 
		do {
			try {
				mc = getMenuChoice(); 
				switch (mc) {
					case PLAY_OPPONENT_MOVE: 
						playOpponentMove();
						break; 
					case MANUAL_MOVE: 
						manualMove(); 
						break; 
					case GET_BEST_MOVE: 
						getBestMove(); 
						break; 
					case GIVE_TILES: 
						giveTiles(); 
						break; 
					case DISPLAY: 
						o.println(sb); 
						o.println("Your rack: \"" + ai.getRack().toString() + "\""); 
						o.println(); 
						break;
					case END_GAME: 
						endGame(); 
						break; 
				}
			}
			catch (IOException e) {
				o.println("IOException occurred while playing. Returning to menu."); 
				e.printStackTrace(); 
				o.println(); 
			}

		} while (mc != MENU_CHOICE.END_GAME); 
	}

	private void giveTiles() throws IOException{
		o.println("Enter tiles to add to your rack:"); 
		String t = in.readLine(); 
		try {
			ai.addTiles(t); 
		}
		catch (ScrabbleException e) {
			o.println("Exception occurred getting tiles. Returning to menu."); 
			return; 
		}
	}

	private void playOpponentMove() throws IOException {
		boolean areYouSure = false; 
		ScrabbleMove opMove; 

		do {
			o.println("Enter the opponent's move:"); 
			opMove = inputMove(); 

			while (!sb.isValidMove(opMove)) {
				o.println("Move isn't valid. Enter another move."); 
				opMove = inputMove(); 
			}

			o.println("Are you sure this is correct? (Y/N)"); 
			o.println(opMove); 
			String s = in.readLine(); 
			if (s.equals("Y") || s.equals("y"))
				areYouSure = true;

		} while (!areYouSure); 

		int score = sb.makeMove(opMove); 

		o.println("Opponent played for " + score + " points!"); 
		o.println(sb); 
	}

	private void manualMove() throws IOException {

		boolean areYouSure = false; 
		ScrabbleMove manMove; 

		do {
			o.println("Enter a manual move:"); 
			manMove = inputMove(); 

			while (!sb.isValidMove(manMove)) {
				o.println("Move isn't valid. Enter another move."); 
				manMove = inputMove(); 
			}

			o.println("Are you sure this is correct? (Y/N)"); 
			o.println(manMove); 
			String s = in.readLine(); 
			if (s.equals("Y") || s.equals("y"))
				areYouSure = true;

		} while (!areYouSure); 

		int score = sb.computeScore(manMove); 

		try {
			ai.playMove(sb, manMove); 
		}
		catch (ScrabbleException e) {
			o.println("Exception occurred playing manual move. Returning to menu.");
			o.println(e.getMessage()); 
			return; 
		}

		o.println("You played a move for " + score + " points!"); 
		o.println(sb); 

	}

	private void getBestMove() throws IOException {
		MoveScore bestMove = null; 
		try {
			bestMove = ai.getBestMove(sb); 
		}
		catch (ScrabbleException e) {
			o.println("Exception occurred getting best move. Returning to menu."); 
			o.println(e.getMessage()); 
			return; 
		}

		o.println("Your best move is " + bestMove.score + " points!"); 
		o.println(bestMove.move); 
		o.println(); 
		o.println("Play this move? (Y/N)"); 
		String s = in.readLine(); 
		if (s.equals("Y") || s.equals("y")) {
			try {
			ai.playMove(sb, bestMove.move); 
			}
			catch (ScrabbleException e) {
				o.println("Exception occurred playing move. Returning to menu."); 
				o.println(e.getMessage()); 
				return; 
			}
		}
		else {
			o.println("Not playing move. Returning to menu."); 
			o.println(); 
			return;
		}
	}

	private void endGame() {
		o.println("Ending game. Your total score was " + ai.getTotalScore()); 
		o.println(); 
	}

	private static ScrabbleMove inputMove() throws IOException{
		int r,c; 
		String play, tilesUsed; 
		DIR d; 

		o.print("Enter move row>"); 
		String s = in.readLine(); 
		r = Integer.parseInt(s); 
		o.println(); 

		o.print("Enter move col>"); 
		s = in.readLine(); 
		c = Integer.parseInt(s); 
		o.println(); 

		o.print("Enter play>"); 
		play  = in.readLine(); 
		o.println(); 

		o.print("Enter tiles used>"); 
		tilesUsed = in.readLine(); 
		o.println(); 

		o.print("Enter direction (S | E)>");
		s = in.readLine(); 
		if (s.equals("S"))
			d = DIR.S; 
		else
			d = DIR.E; 

		ScrabbleMove opMove = new ScrabbleMove(r,c,play,tilesUsed,d); 

		o.println("You entered move:"); 
		o.println(opMove); 
		o.println(); 

		return opMove; 
	}

	private static MENU_CHOICE getMenuChoice() throws IOException{
		o.println("Scrabble Player Options: "); 
		o.println("\t1) Play opponent's move"); 
		o.println("\t2) Play manual move"); 
		o.println("\t3) Get best move"); 
		o.println("\t4) Get tiles"); 
		o.println("\t5) Display board"); 
		o.println("\t6) End Game"); 

		o.print("ENTER OPTION>"); 
		String choice = in.readLine(); 

		int val; 

		try {
			val = Integer.parseInt(choice); 
		}
		catch (NumberFormatException e) {
			o.println("Invalid number entered. Displaying menu again."); 
			o.println(); 
			return getMenuChoice(); 
		}

		switch (val) {
			case 1: 
				return MENU_CHOICE.PLAY_OPPONENT_MOVE; 
			case 2: 
				return MENU_CHOICE.MANUAL_MOVE; 
			case 3: 
				return MENU_CHOICE.GET_BEST_MOVE; 
			case 4:
				return MENU_CHOICE.GIVE_TILES; 
			case 5:
				return MENU_CHOICE.DISPLAY; 
			case 6:
				return MENU_CHOICE.END_GAME; 
			default: 
				o.println("Invalid choice. Displaying menu again."); 
				o.println(); 
				return getMenuChoice(); 
		}
	}


}
