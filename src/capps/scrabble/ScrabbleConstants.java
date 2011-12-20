package capps.scrabble; 

import java.io.PrintStream; 

public class ScrabbleConstants {

	public static final char BLANK = ' ';
	public static final char EMPTY = '_'; 
	public static final char WILDCARD = '*'; 
	public static final String sBLANK = " "; 
	public static final String sEMPTY = "_"; 
	public static final String sWILDCARD = "*"; 

	public static final int ROWS = 15; 
	public static final int COLS = 15; 

	public static final String NL = System.getProperty("line.separator"); 

	public static final PrintStream o = System.out; //Come on, no one wants to type System.out 1,000,000 times

	//Could have used a HashMap, but this is probably faster, and this way it can be a static function
	public static final int tileVal(char t) {
		char T = Character.toTitleCase(t); 

		switch (T) {
			case '*': 
				return 0; 
			case 'E': case 'A': case 'I': case 'O': case 'U' : 
			case 'N': case 'R': case 'T': case 'S': case 'L' :
				return 1; 
			case 'D': case 'G': 
				return 2; 
			case 'B': case 'C': case 'M': case 'P':
				  return 3; 
			case 'F': case 'H': case 'V': case 'W': case 'Y':
				return 4;
			case 'K':
				return 5; 
			case 'J': case 'X':
				return 8; 
			case 'Q': case 'Z':
				return 10; 
		}

		return -1; 
			
	}
}
