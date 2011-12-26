package capps.scrabble; 

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import java.util.ArrayList;

public class ScrabbleDict implements java.io.Serializable {
	protected static final long serialVersionUID=0xffffffff;

	protected static final int HASH_SIZE = 1 << 26; //26 letters in English language!
	private final ArrayList<Word> lexiDict; 
	private final WordBucket[] hashDict;
	protected final int NUM_WORDS; 
	protected final static char COMMENT_CHAR = '#';

	public ScrabbleDict(BufferedReader dictFile) throws IOException {
		//first pass: get in lexicographic order
		lexiDict = new ArrayList<Word>(); 
		String line; 
		while ((line = dictFile.readLine()) != null) {
			//Allow commenting lines when I discover words not in 4th edition!
			if (line.length() > 1 && line.charAt(0) != COMMENT_CHAR ) { 
				lexiDict.add(new Word(line)); 
			}
		}
		dictFile.close(); 

		NUM_WORDS = lexiDict.size(); 

		//Second pass: Get words in hash table and attach prefixes/suffixes
		hashDict = new WordBucket[HASH_SIZE]; 
		for (int i = 0; i < NUM_WORDS; i++) {
			Word w = lexiDict.get(i); 
			int hashVal = hash(w.toString()); 
			hashDict[hashVal] = new WordBucket(w, hashDict[hashVal]); 

			int j = i + 1; 

			//Get suffixes. Will be immediately following in lexicographic order!
			while (j < NUM_WORDS && lexiDict.get(j).toString().startsWith(w.toString())) {
				w.addSuffix(lexiDict.get(j)); 
				j++; 

			}
		}

		//Third pass: Use hash table to find prefixes
		//by checking words with the same letters
		for (Word w: lexiDict) {
			for (Word p: lexiDict) {
				if (p.toString().length() > w.toString().length() && 
						p.toString().endsWith(w.toString()) )
					w.addPrefix(p); 

				}
			System.out.println("Got prefixes for: " + w); 
			}
	}

	//Testing to verify it worked
	public void dumpDict(BufferedWriter bw) throws IOException {
		/*
		for (Word w: lexiDict) {
			bw.write(w.toString()); 
			bw.newLine(); 
		}*/

		bw.write("HASH TABLE:");
		bw.newLine(); 

		for (int i = 0; i < HASH_SIZE; i++) {
			if (hashDict[i] != null) {
				for (Word w: hashDict[i]) {
					bw.write(w.toLongString()); 
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

		for (Word w: matches) {
			if (w.strEquals(s))
				return true;
		}

		return false; 
	}

	public Word exactMatch(String s) {
		WordBucket matches = getMatches(s); 
		if (matches == null) {
			return null; 
		}

		for (Word w: matches) {
			if (w.strEquals(s))
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

		for (Word w: matches) {
			if (w.isAnagram(s))
				anagrams.add(w.toString()); 
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

		return hashVal; 
	}

	public void addToDict(String s) {
		if (inDict(s.toUpperCase())) {
			System.out.println("Word \"" + s + "\" already in dictionary."); 
			return; 
		}
		s = s.toUpperCase(); 
		Word newWord = new Word(s); 
		WordBucket bucket = getMatches(s); 
		bucket.add(newWord); 

		for (Word w: lexiDict) {
			if (w.toString().endsWith(s) && !w.strEquals(s)) {
				newWord.addPrefix(w); 
			}
			else if (s.startsWith(w.toString()) && !w.strEquals(s)) {
				w.addSuffix(newWord); 
			}
			else if (s.endsWith(w.toString()) && !w.strEquals(s)) {
				w.addPrefix(newWord); 	
			}
			else if (w.toString().startsWith(s) && !w.strEquals(s)) {
				newWord.addSuffix(w); 
			}
		}
	}


}
