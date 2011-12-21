package capps.scrabble; 

import static capps.scrabble.ScrabbleConstants.*; 

public class Square {

	public final int letterMult; 
	public final int wordMult; 

	private char letter; 
	private boolean isBlank; 

	public Square() {
		this.letterMult = this.wordMult = 1; 
		this.letter = EMPTY; 
		isBlank = false; 
	}

	public Square(int letterMult, int wordMult) {
		this.letterMult = letterMult; 
		this.wordMult = wordMult; 
		this.letter = EMPTY; 
		isBlank = false; 
	}

	public char getLetter() {
		return letter; 
	}	

	public void setLetter(char c) {
		letter = c; 
	}

	public boolean isBlank() {
		return isBlank; 
	}

}
