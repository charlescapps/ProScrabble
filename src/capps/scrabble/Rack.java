package capps.scrabble; 

import java.util.ArrayList;

import static capps.scrabble.ScrabbleConstants.sWILDCARD; 
import static capps.scrabble.ScrabbleConstants.WILDCARD; 

public class Rack {

	private String tiles; 
	private ArrayList<ArrayList<String>> substrByLen; 
	private int numWild; 

	public Rack(String initialTiles) throws ScrabbleException {
		tiles = initialTiles.toUpperCase(); 
		if (tiles.length() != 7) 
			throw new ScrabbleException("Initial tiles not size 7!"); 

		substrByLen = new ArrayList<ArrayList<String>>(); 
		numWild = 0;
		for (int i = 0; i < tiles.length(); i++){ 
			substrByLen.add(new ArrayList<String>());
			if (tiles.charAt(i)==WILDCARD)
				numWild++; 
		}
		assert(numWild <=2); 
	}

	@Override
	public String toString() {
		return tiles; 
	}

	public ArrayList<String> getSubstringsOfRack(int len) {
		if (len == 0)
			return null; 

		ArrayList<String> substrings=substrByLen.get(len-1); 

		if (substrings.size() > 0)
			return substrings; 

		int rackLen = tiles.length(); 

		if (len == rackLen & numWild == 0){
			substrings.add(tiles);
			return substrings; 
		}

		boolean[] chosen = new boolean[rackLen]; 

		getSubstringsHelper(chosen,tiles,new StringBuffer(),len,0,substrings); 
		return substrings; 
	}

	private void getSubstringsHelper
		(boolean[] chosen, String base, StringBuffer buildStr, int len, int startIndex, ArrayList<String> addToMe) {

		if (buildStr.length() + base.length() - startIndex < len)
			return; 

		if (buildStr.length() == len) {
			if (!addToMe.contains(buildStr.toString()))
				addToMe.add(buildStr.toString()); 
			return; 
		}

		for (int i = startIndex; i < base.length(); i++) {
			if (!chosen[i]) {
				chosen[i] = true;
				if (base.charAt(i) != WILDCARD){
					buildStr.append(base.charAt(i)); 
					getSubstringsHelper(chosen,base,buildStr,len,(i+1),addToMe);
					buildStr.deleteCharAt(buildStr.length()-1); 
				}
				else {
					for (char c = 'A'; c <= 'Z'; c++) {//Wildcards
						buildStr.append(c); 
						getSubstringsHelper(chosen,base,buildStr,len,(i+1),addToMe);
						buildStr.deleteCharAt(buildStr.length()-1); 
					}
				}
				chosen[i] = false; 
			}
		}
	}

	public String hasTiles(String t) {
		boolean[] marked = new boolean[tiles.length()]; 
		boolean markedSomething = false; 
		StringBuffer buildTiles = new StringBuffer(); 
		boolean hasWild = tiles.contains(sWILDCARD); 

		for (int i = 0; i < t.length(); i++) {
			markedSomething = false; 
			for (int j = 0; j < tiles.length(); j++) {
				if (!marked[j] && tiles.charAt(j) == t.charAt(i)) {
					marked[j] = true; 
					markedSomething = true; 
					buildTiles.append(tiles.charAt(j)); 
					break; 
				}
			}
			if (!markedSomething && hasWild){//Attempt to get wildcard
				for (int k = 0; k < tiles.length(); k++) {
					if (!marked[k] && tiles.charAt(k)==WILDCARD) {
						marked[k] = true; 
						markedSomething=true;
						buildTiles.append(WILDCARD); 
						break; 
					}
				}
			}
			if (!markedSomething)
				return null; 
		}

		return buildTiles.toString(); 
	}

	public void addTiles(String toAdd) throws ScrabbleException {
		if (tiles.length() + toAdd.length() > 7)
			throw new ScrabbleException("Too many tiles given to add!"); 

		tiles = (tiles + toAdd).toUpperCase(); 

		numWild = 0;
		for (int i = 0; i < tiles.length(); i++){ 
			if (tiles.charAt(i)==WILDCARD)
				numWild++; 
		}

		substrByLen = new ArrayList<ArrayList<String>>(); 
		for (int i = 0; i < tiles.length(); i++) 
			substrByLen.add(new ArrayList<String>()); 
	}

	public void removeTiles (String toRemove) throws ScrabbleException {
		if (toRemove.length() > tiles.length()) {
			throw new ScrabbleException("Too many tiles given to remove"); 
		}

		boolean[] markedToRemove = new boolean[tiles.length()]; 

		boolean markedSomething = false; 

		for (int i = 0; i < toRemove.length(); i++) {
			markedSomething = false; 
			for (int j = 0; j < tiles.length(); j++) {
				if (tiles.charAt(j) == toRemove.charAt(i) && !markedToRemove[j]) {
					markedSomething = true; 
					markedToRemove[j] = true; 
					break; 
				}
			}
			if (!markedSomething) {
				throw new ScrabbleException("Tiles to remove had extra tiles."); 
			}
		}

		StringBuffer newTiles = new StringBuffer(); 

		for (int i = 0; i < tiles.length(); i++) {
			if (!markedToRemove[i])
				newTiles.append(tiles.charAt(i)); 
		}
		tiles = newTiles.toString(); 	

		numWild = 0;
		for (int i = 0; i < tiles.length(); i++){ 
			if (tiles.charAt(i)==WILDCARD)
				numWild++; 
		}
		substrByLen = new ArrayList<ArrayList<String>>(); 
		for (int i = 0; i < tiles.length(); i++) 
			substrByLen.add(new ArrayList<String>()); 
	}

}
