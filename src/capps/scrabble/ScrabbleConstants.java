package capps.scrabble; 

import java.io.PrintStream; 

public class ScrabbleConstants {

	public static enum DIR {S, E}; 

	public static final char BLANK = ' ';
	public static final char EMPTY = '_'; 
	public static final char WILDCARD = '*'; 
	public static final String sBLANK = " "; 
	public static final String sEMPTY = "_"; 
	public static final String sWILDCARD = "*"; 

	public static final String SCRBL_MOVE = "SCRBL_MOVE"; 
	public static final String SCRBL_RACK = "SCRBL_RACK"; 
	public static final String SCRBL_BOARD = "SCRBL_BOARD"; 

	public static final int ROWS = 15; 
	public static final int COLS = 15; 

	public static final String NL = System.getProperty("line.separator"); 

	public static final PrintStream o = System.out; //Come on, no one wants to type System.out 1,000,000 times

	//O(n^2) behaviour, don't think this can be beat unless we had letters ordered??!
	public static boolean areAnagrams(String s1, String s2) {
		int len1 = s1.length(), len2 = s2.length(); 
		if (len1 != len2) {
			return false; 
		}
		
		boolean marked[] = new boolean[len1]; 

		for (int i = 0; i < len1; i++) {
			if (!markNext(marked, s1.charAt(i), s2))
				return false; 
		}

		return true; 
	}

	private static boolean markNext(boolean[] marked, char c, String s) {
		for (int i = 0; i < marked.length; i++) {
			if (!marked[i] && s.charAt(i) == c) {
				marked[i] = true; 
				return true; 
			}
		}
		return false; 
	}

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

	//This checks if haystack contains the letters of needle (with duplication)
	//Does not take into account wildcards/blank tiles like the method
	//hasTiles in the Rack class. 
	public static final boolean containsLetters(String needle, String haystack) {
		boolean[] marked = new boolean[haystack.length()]; 
		
		for (int i = 0; i < needle.length(); i++) {
			boolean markedSomething = false; 
			for (int j = 0; j < haystack.length(); j++) {
				if (haystack.charAt(j) == needle.charAt(i) && !marked[j]) {
					marked[j] = true; 
					markedSomething = true;
					break; 
				}
			}
			if (!markedSomething) {
				return false; 
			}
		}
		return true; 
	}
}
