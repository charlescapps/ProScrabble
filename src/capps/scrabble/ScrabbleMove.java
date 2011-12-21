package capps.scrabble; 

import static capps.scrabble.ScrabbleConstants.*;


public class ScrabbleMove {

	final public int row; 
	final public int col; 
	final public String play; 
	final public String tilesUsed; 
	final public DIR dir; 

	public ScrabbleMove(int r, int c, String word, String tiles, DIR d) {
		row = r; col = c; 
		play = word; tilesUsed = tiles; 
		dir = d; 
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(); 

		sb.append("Scrabble Move: row=" + row + ", col=" + col + NL); 
		sb.append("Word=\"" + play + "\", dir=" + dir.toString()); 
		return sb.toString(); 
	}

}