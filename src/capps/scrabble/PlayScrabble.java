package capps.scrabble; 

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static capps.scrabble.ScrabbleConstants.*; 

public class PlayScrabble {
	private static enum MENU_CHOICE 
		{PLAY_OPPONENT_MOVE, MANUAL_MOVE, GET_BEST_MOVE, GIVE_TILES, 
			REMOVE_TILES, FORCE_MOVE, UNDO, DISPLAY, ADD_TO_DICT, END_GAME}; 

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
		
		boolean wasValid = false; 
		while (!wasValid) {
			o.print("Enter initial rack>"); 
			try {
				ai = new AIPlayer(in.readLine(), dict); 
				wasValid = true; 
			}
			catch (ScrabbleException e) {
				o.println("Invalid initial rack. Try again!"); 
				o.println(e.getMessage()); 
				o.println(); 
				wasValid = false; 
			}
		}
		o.println(); 
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
					case FORCE_MOVE:
						forceMove();
						break; 
					case UNDO: 
						undoMove(); 
						break;
					case GIVE_TILES: 
						giveTiles(); 
						break; 
					case REMOVE_TILES: 
						removeTiles(); 
						break; 
					case DISPLAY: 
						o.println(sb); 
						o.println("Your rack: \"" + ai.getRack().toString() + "\""); 
						o.println("Your total score: " + ai.getTotalScore()); 
						o.println(); 
						break;
					case ADD_TO_DICT: 
						addToDict(); 
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
		o.print("Enter tiles to add to your rack>"); 
		String t = in.readLine(); 
		o.println(); 
		try {
			ai.addTiles(t); 
		}
		catch (ScrabbleException e) {
			o.println("Exception occurred getting tiles. Returning to menu."); 
			o.println(e.getMessage());
			o.println(); 
			return; 
		}
		o.println("Your new rack: \"" + ai.getRack().toString() + "\""); 
		o.println(); 
	}

	private void removeTiles() throws IOException{
		o.print("Enter tiles to remove>"); 
		String s = in.readLine(); 
		o.println(); 

		try {
			ai.getRack().removeTiles(s); 
		}
		catch (ScrabbleException e) {
			o.println("Invalid tiles chosen to remove. Returning to menu."); 
			o.println(); 
		}
	}

	private void playOpponentMove() throws IOException {
		boolean areYouSure = false; 
		ScrabbleMove opMove; 

		do {
			o.println("Enter the opponent's move:"); 
			opMove = inputMove(); 

			while (opMove == null || !sb.isValidMove(opMove)) {
				o.print("Move isn't valid. Enter another move? (Y/N)."); 
				String s = in.readLine(); 
				if (s.equals("Y") || s.equals("y")) {
					opMove = inputMove(); 
				}
				else
					return; 
			}

			o.println(opMove); 
			o.print("Are you sure this is correct? (Y/N) "); 
			String s = in.readLine(); 
			o.println(); 
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

			while (manMove == null || !sb.isValidMove(manMove)) {
				o.println("Move isn't valid. Enter another move."); 
				manMove = inputMove(); 
			}

			o.println(manMove); 
			o.print("Are you sure this is correct? (Y/N) "); 
			String s = in.readLine(); 
			o.println(); 
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

		if (bestMove == null) {
			o.println("You have no moves. Suggest getting tiles from bag."); 
			o.println(); 
			return; 
		}

		o.println("Your best move is " + bestMove.score + " points!"); 
		o.println(bestMove.move); 
		o.println(); 
		o.print("Play this move? (Y/N)"); 
		String s = in.readLine(); 
		o.println(); 
		if (s.equals("Y") || s.equals("y")) {
			try {
				ai.playMove(sb, bestMove.move); 
				o.println(sb); 
				o.println(); 
			}
			catch (ScrabbleException e) {
				o.println("Exception occurred playing move. Returning to menu."); 
				o.println(e.getMessage()); 
				o.println(); 
				return; 
			}
		}
		else {
			o.println("Not playing move. Returning to menu."); 
			o.println(); 
			return;
		}
	}

	private void forceMove() throws IOException{
		ScrabbleMove forcedMove = inputMove(); 
		if (forcedMove == null) {
			o.println("Invalid move entered. Returning to menu."); 
			o.println(); 
			return; 
		}
		o.println("Move to force:"); 
		o.println(forcedMove); 
		o.println(); 
		o.print("Is this correct? (Y/N)");
		String s = in.readLine(); 
		if (s.equals("y") || s.equals("Y"))
			sb.forceMove(forcedMove); 
		else 
			o.println("Returning to menu."); 
		o.println(); 
	}

	private void undoMove() throws IOException{
		o.print("Are you sure you want to undo a move? (Y/N)");
		String s = in.readLine(); 

		if (s.equals("Y") || s.equals("y"))
			sb.undoMove(); 

		o.println(); 
	}

	private void addToDict() throws IOException {
		o.print("Enter a word to add to dictionary>");
		String word = in.readLine(); 

		o.println();
		o.print("Are you sure you want to add word \"" + word + "\"? (Y/N)"); 
		String s = in.readLine(); 

		if (s.equals("Y") || s.equals("y")) {
			dict.addToDict(word); 
		}
		else {
			o.println("Aborting. Returning to menu."); 
			o.println(); 
		}

	}

	private void endGame() {
		o.println("Ending game. Your total score was " + ai.getTotalScore()); 
		o.println(); 
	}

	private static ScrabbleMove inputMove() throws IOException{
		int r,c; 
		String play, tilesUsed, s; 
		DIR d; 

		try {
			o.print("Enter move row>"); 
			s = in.readLine(); 
			r = Integer.parseInt(s); 

			o.print("Enter move col>"); 
			s = in.readLine(); 
			c = Integer.parseInt(s); 
			o.println();
		}
		catch (NumberFormatException e) {
			o.println("Invalid number entered. Returning to menu."); 
			o.println(); 
			return null; 
		}

		o.print("Enter play>"); 
		play  = in.readLine().toUpperCase(); 

		o.print("Enter tiles used>"); 
		tilesUsed = in.readLine().toUpperCase(); 
		o.println(); 

		o.print("Enter direction (S | E)>");
		s = in.readLine(); 
		if (s.equals("S"))
			d = DIR.S; 
		else
			d = DIR.E; 

		ScrabbleMove opMove = new ScrabbleMove(r,c,play,tilesUsed,d); 
		if (!ScrabbleMove.isValidMove(opMove)){
			o.println("Invalid move entered. Returning to menu."); 
			o.println(); 
			return null; 
		}

		//o.println("You entered move:"); 
		//o.println(opMove); 
		//o.println(); 

		return opMove; 
	}

	private static MENU_CHOICE getMenuChoice() throws IOException{
		o.println("Scrabble Player Options: "); 
		o.println("\t1) Play opponent's move"); 
		o.println("\t2) Play manual move"); 
		o.println("\t3) Play best move"); 
		o.println("\t4) Force a move"); 
		o.println("\t5) Undo previous move"); 
		o.println("\t6) Add tiles"); 
		o.println("\t7) Remove tiles"); 
		o.println("\t8) Display board"); 
		o.println("\t9) Add word to dictionary"); 
		o.println("\t10) End Game"); 

		o.print("ENTER OPTION>"); 
		String choice = in.readLine(); 
		o.println(); 

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
				return MENU_CHOICE.FORCE_MOVE; 
			case 5:
				return MENU_CHOICE.UNDO;
			case 6:
				return MENU_CHOICE.GIVE_TILES; 
			case 7:
				return MENU_CHOICE.REMOVE_TILES;
			case 8:
				return MENU_CHOICE.DISPLAY; 
			case 9:
				return MENU_CHOICE.ADD_TO_DICT; 
			case 10:
				return MENU_CHOICE.END_GAME; 
			default: 
				o.println("Invalid choice. Displaying menu again."); 
				o.println(); 
				return getMenuChoice(); 
		}
	}


}
