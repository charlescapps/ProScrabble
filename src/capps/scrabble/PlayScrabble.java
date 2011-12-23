package capps.scrabble; 

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static capps.scrabble.ScrabbleConstants.*; 

public class PlayScrabble {
	private static final enum MENU_CHOICE 
		{PLAY_OPPONENT_MOVE, MANUAL_MOVE, GET_BEST_MOVE, END_GAME}; 

	private AIPlayer ai; 
	private ScrabbleBoard sb; 
	private ScrabbleDict dict;
	private static final BufferedReader in 
		= new BufferedReader(new InputStreamReader(System.in)); 

	public PlayScrabble(ScrabbleBoard sb, ScrabbleDict dict) throws IOException {
		this.sb = sb; 
		this.dict = dict; 

		getInitialPlayer(); 
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

	private void gameLoop() throws IOException {

		MENU_CHOICE mc; 
		do {
			mc = getMenuChoice(); 

		} while (mc != MENU_CHOICE.END_GAME); 
	}

	private MENU_CHOICE getMenuChoice() throws IOException{
		o.println("Options: "); 
		o.println("\t1) Play opponent's move"); 
		o.println("\t2) Play manual move"); 
		o.println("\t3) Get best move"); 
		o.println("\t4) End Game"); 

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
				return MENU_CHOICE.END_GAME; 
			default: 
				o.println("Invalid choice. Displaying menu again."); 
				o.println(); 
				return getMenuChoice(); 
		}
	}


}
