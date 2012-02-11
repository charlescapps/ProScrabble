package capps.scrabble; 

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import java.util.ArrayList;

public class ScrabbleDict implements java.io.Serializable {
	private static final long serialVersionUID=0xffffffff;

	private static final int HASH_SIZE = 1 << 20; //26 letters in English language, but take mod 2^20 to reduce no. of buckets.!
	private final ArrayList<String> lexiDict; 
	private final WordBucket[] hashDict;
	private final int NUM_WORDS; 
	private final static char COMMENT_CHAR = '#';

	public ScrabbleDict(BufferedReader dictFile) throws IOException {
		//first pass: get in lexicographic order
		lexiDict = new ArrayList<String>(); 
		hashDict = new WordBucket[HASH_SIZE]; 
		String line; 
		while ((line = dictFile.readLine()) != null) {
			line = line.toUpperCase(); 
			//Allow commenting lines when I discover words not in 4th edition!
			if (line.length() > 1 && line.charAt(0) != COMMENT_CHAR ) { 
				lexiDict.add(line); 
			}
			int hashVal = hash(line); 
			hashDict[hashVal] = new WordBucket(line, hashDict[hashVal]); 
		}
		dictFile.close(); 

		NUM_WORDS = lexiDict.size(); 
	}

	//Testing to verify it worked
	public void dumpDict(BufferedWriter bw) throws IOException {

		bw.write("HASH TABLE:");
		bw.newLine(); 

		for (int i = 0; i < HASH_SIZE; i++) {
			if (hashDict[i] != null) {
				bw.write("[" + Integer.toString(i,2) + "]\n"); //Writes binary string.
				for (String w: hashDict[i]) {
					bw.write("\t" + w); 
					bw.newLine(); 
				}
				
			}
		}

		bw.close(); 
	}

	public boolean inDict(String s) {
		WordBucket matches = getMatches(s); 
		if (matches == null) {
			return false; 
		}

		for (String w: matches) {
			if (w.equals(s))
				return true;
		}

		return false; 
	}

	public String exactMatch(String s) {
		WordBucket matches = getMatches(s); 
		if (matches == null) {
			return null; 
		}

		for (String w: matches) {
			if (w.equals(s))
				return w;
		}
		return null; 
	}

	public WordBucket getMatches(String s) {
		return hashDict[hash(s)]; 
	}

	public ArrayList<String> getAnagrams(String s) {
		WordBucket matches = getMatches(s); 
		if (matches == null)
			return null; 
		ArrayList<String> anagrams = new ArrayList<String>(); 

		for (String w: matches) {
			if (ScrabbleConstants.areAnagrams(s,w))
				anagrams.add(w); 
		}

		return anagrams; 
	}

	public static int hash(String w) {
		String W = w.toUpperCase();
		int len = W.length(), hashVal = 0; 

		for (int i = 0; i < len; i++) {
			int letterNum = W.charAt(i) - 'A'; 	
			int bit = 1 << letterNum; 
			hashVal |= bit; 
		}

		return hashVal % HASH_SIZE; 
	}

	public void addToDict(String s) {
		if (inDict(s.toUpperCase())) {
			System.out.println("String \"" + s + "\" already in dictionary."); 
			return; 
		}
		s = s.toUpperCase(); 
		int sHash = hash(s); 
		hashDict[sHash] = new WordBucket(s,hashDict[sHash]); 

		for (int i = 0; i < lexiDict.size(); i++) {
			if (lexiDict.get(i).compareTo(s) < 0 && (i == lexiDict.size() - 1 || lexiDict.get(i+1).compareTo(s) > 0)) {
				lexiDict.add(i+1,s); 
			}
			
			i++;
		}

	}


}
