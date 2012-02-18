package capps.scrabble; 

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static capps.scrabble.ScrabbleConstants.*;

public class ScrabbleMove {

	final public static String ACM_FORMAT 
		= "scrbl_move: (tiles_used) (ROW, COL, word_played, EAST | SOUTH)";
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
		String[] tokens = acmString.split(":"); 
		if (!tokens[0].toUpperCase().equals("SCRBL_MOVE")) {
			throw new BadStateException("Move string doesn't start with \"scrbl_move:\""
						+ NL + "\tFormat is: '" + ACM_FORMAT + "'");
		}

		Pattern used = Pattern.compile("\\(\\s*([A-Za-z\\*]+)\\s*\\)");
		Matcher m = used.matcher(acmString); 
		if (!m.find()) {
			throw new BadStateException("Tiles used not found in move string." 
					+"Format is: '" + ACM_FORMAT + "'"); 
		}

		this.tilesUsed = m.group(1).toUpperCase(); 

		Pattern theRest = Pattern.compile(
				"\\(\\s*([0-9]+)\\s*," //Row 
			   +"\\s*([0-9]+)\\s*," //Col
			   +"\\s*([a-zA-Z]+)\\s*," //Play
			   +"\\s*([a-zA-Z]+)\\s*\\)"); //EAST or SOUTH

		m = theRest.matcher(acmString); 
		if (!m.find()) {
			throw new BadStateException("Invlid move string '" + acmString + "'"); 
		}

		this.row = Integer.parseInt(m.group(1)); 
		this.col = Integer.parseInt(m.group(2)); 
		this.play = m.group(3).toUpperCase(); 
        String eOrS = m.group(4).toUpperCase(); 
        if (eOrS.equals("S") || eOrS.equals("SOUTH"))
            this.dir = DIR.S; 
        else if (eOrS.equals("E") || eOrS.equals("EAST"))
            this.dir = DIR.E; 

        else throw new BadStateException("Invalid move string '" + acmString + "'\n" 
                                       + "Must specify SOUTH or EAST"); 

	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(); 

		sb.append("Scrabble Move: row=" + row + ", col=" + col + NL); 
		sb.append("\tWord=\"" + play + "\", dir=" + (dir==DIR.E ? "EAST" : "SOUTH")); 
		sb.append("\n\tTiles used=\"" + tilesUsed + "\""); 
		return sb.toString(); 
	}

	public String toAcmString() {

		return ("scrbl_move: " + "(" + this.tilesUsed + ") " + "(" + row + ", " + col +", " + play + ", " + (dir == DIR.E ? "EAST" : "SOUTH") + ")"); 
		
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
