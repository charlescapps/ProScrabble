package capps.scrabble; 

import static capps.scrabble.ScrabbleConstants.*; 

public class Square implements Cloneable{

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

	public void setIsBlank(boolean b) {
		this.isBlank = b; 
	}

	@Override
	public Object clone() {
		Square aClone = new Square(letterMult,wordMult); 
		aClone.setLetter(letter); 
		aClone.setIsBlank(isBlank); 

		return aClone; 
	}
}
