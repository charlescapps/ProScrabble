package capps.scrabble; 

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	public ScrabbleMove(String acmString) throws BadStateException{
		String[] tokens = acmString.split(" \t"); 
		if (!tokens[0].toUpperCase().equals("SCRBL_MOVE:")) {
			throw new BadStateException("Move string doesn't start with 'scrbl_move:'"
						+ NL + "\tInput was: '" + acmString + "'");
		}

		Pattern used = Pattern.compile("^\\s*([A-Za-z\\*]+)");
		Matcher m = used.matcher(acmString); 
		if (!m.lookingAt()) {
			throw new BadStateException("Tiles used not found at start of move string." 
					+"Move string: '" + acmString + "'"); 
		}

		this.tilesUsed = m.group(1).toUpperCase(); 

		Pattern theRest = Pattern.compile(
				"\\(\\s*([0-9]+)\\s*," //Row 
			   +"\\s*([0-9]+)\\s*," //Col
			   +"\\s*([a-zA-Z]+)\\s*," //Play
			   +"\\s*((?:EAST)|(?:SOUTH)\\)"); //EAST or SOUTH

		m = theRest.matcher(acmString); 
		if (!m.lookingAt()) {
			throw new BadStateException("Invlid move string: '" + acmString + "'"); 
		}

		this.row = Integer.parseInt(m.group(1)); 
		this.col = Integer.parseInt(m.group(2)); 
		this.play = m.group(3); 
		this.dir = m.group(4).equals("E") ? DIR.E : DIR.S; 
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(); 

		sb.append("Scrabble Move: row=" + row + ", col=" + col + NL); 
		sb.append("\tWord=\"" + play + "\", dir=" + dir.toString()); 
		sb.append("\n\tTiles used=\"" + tilesUsed + "\""); 
		return sb.toString(); 
	}

	public String toAcmString() {

		return ("scrbl_move: " + "(" + row + ", " + col +", " + play + ", " + dir.toString() + ")"); 
		
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
