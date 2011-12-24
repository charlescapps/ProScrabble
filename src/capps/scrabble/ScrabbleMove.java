package capps.scrabble; 

import static capps.scrabble.ScrabbleConstants.*;


public class ScrabbleMove {

	final public int row; 
	final public int col; 
	final public String play; 
	final public String tilesUsed; 
	final public DIR dir; 

	//No error checking here to increase speed.
	//May want to add checks for valid chars and lengths
	//At the moment just do this in PlayScrabble class
	public ScrabbleMove(int r, int c, String word, String tilesUsed, DIR d) {
		row = r; col = c; 
		play = word; this.tilesUsed = tilesUsed; 
		dir = d; 
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(); 

		sb.append("Scrabble Move: row=" + row + ", col=" + col + NL); 
		sb.append("\tWord=\"" + play + "\", dir=" + dir.toString()); 
		sb.append("\n\tTiles used=\"" + tilesUsed + "\""); 
		return sb.toString(); 
	}

	public static boolean isValidMove(ScrabbleMove m) {
		if (m.play.length() > 7 || m.tilesUsed.length() > m.play.length())
			return false; 

		if (m.row < 0 || m.col < 0 || m.row >= ROWS || m.col >= COLS)
			return false; 

		for (int i = 0 ; i < m.play.length(); i++) {
			if (m.play.charAt(i) < 'A' || m.play.charAt(i) > 'Z'){
				return false; 
			}
			if (i < m.tilesUsed.length() && m.tilesUsed.charAt(i) < 'A' && m.tilesUsed.charAt(i) > 'Z' && m.tilesUsed.charAt(i) != WILDCARD){
				return false; 
			}
		}
		return true; 

	}
}
