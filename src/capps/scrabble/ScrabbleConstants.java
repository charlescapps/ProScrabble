package capps.scrabble; 

import java.io.PrintStream; 
import java.io.BufferedReader; 
import java.io.IOException; 
import java.util.HashMap; 

public class ScrabbleConstants {

	public static enum DIR {S, E}; 

	public static final char BLANK = ' '; //delimits squares in layout file / board state file
	public static final char EMPTY = '_'; //Character on board for an empty square
	public static final char WILDCARD = '*'; //Character in rack for wildcard (blank tile)
	public static final String sBLANK = " "; //delimits squares in layout file / board state file
	public static final String sEMPTY = "_"; //String version of char on board for empty square
	public static final String sWILDCARD = "*"; //String version of blank tile in rack

	public static final String SCRBL_MOVE = "SCRBL_MOVE"; 
	public static final String SCRBL_RACK = "SCRBL_RACK"; 
	public static final String SCRBL_BOARD = "SCRBL_BOARD"; 

	public static final int ROWS = 15; 
	public static final int COLS = 15; 

	public static final String NL = System.getProperty("line.separator"); 

	public static final PrintStream o = System.out; //Come on, no one wants to type System.out 1,000,000 times

    private static boolean useCustomPoints = false; 
    private static HashMap<Character, Integer> customPoints = null; //Map a letter to the point value

    public static void setUseCustomPoints(boolean yesOrNo) {
        useCustomPoints = yesOrNo; 
    }

    public static String printPointDistro() {
        StringBuffer sb = new StringBuffer(); 
        sb.append("Point distrubution:" + NL + NL); 
        for (int i = 0; i < 26; i++) {
            char c = Character.toChars('A'+i)[0];
            sb.append(c);
            sb.append(" " + tileVal(c) + "\n"); 
        }

        return sb.toString(); 
    }

    public static void setCustomPointDistribution(BufferedReader file) 
        throws ScrabbleException, IOException {
        String line; 
        customPoints = new HashMap<Character,Integer>(); 
        while ((line = file.readLine()) != null) {
            String[] tokens = line.split(" "); 

            if (tokens.length < 2 || tokens[0].length() != 1) {
                customPoints = null; 
                throw new ScrabbleException("Points file in wrong format.\n" 
                        + "Each line must have a single letter, a space, then an integer value."); 
            }

            char c = tokens[0].toUpperCase().charAt(0); 
            if (!(c >= 'A' && c <= 'Z')) {
                customPoints = null; 
                throw new ScrabbleException("Start of line must be a letter a-z or A-Z in points file!"); 
            }

            int val = Integer.parseInt(tokens[1]); 

            if (customPoints.get(c) != null) {
                customPoints = null;
                throw new ScrabbleException("Letter appears twice in points file!"); 
            }

            customPoints.put(c, val); 
        }

        if (customPoints.size() != 26) {
            customPoints = null; 
            throw new ScrabbleException("Points file must have all 26 letters!"); 
        }

        customPoints.put(' ', 0); 

        useCustomPoints = true; 
    }

    public static int customTileVal(char c) {
        Integer val; 
        if (customPoints == null || (val = customPoints.get(c))==null){
            return -1024; 
        }
        return val; 
    }

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

        if (useCustomPoints) {
            return customTileVal(T); 
        }

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
