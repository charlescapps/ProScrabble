package capps.scrabble;

import java.util.ArrayList;

public class Word {

	private String txt; 
	private ArrayList<Word> prefixes; 
	private ArrayList<Word> suffixes; 

	public Word(String w) {
		txt = w; 
	}

	public String getTxt() {return txt; }

	public String toString() {return txt; }

	public void addPrefix(Word p) {
		prefixes.add(p); 
	}

	public void addSuffix(Word s) {
		suffixes.add(s); 
	}

	public boolean strEquals(String s) {
		return s.equals(txt); 
	}

	//O(n^2) behaviour, don't think this can be beat unless we had letters ordered??!
	public boolean isAnagram(String s) {
		int len = txt.length(); 
		if (s.length() != len) {
			return false; 
		}
		
		boolean marked[] = new boolean[len]; 

		for (int i = 0; i < len; i++) {
			if (!markNext(marked, s.charAt(i)))
				return false; 
		}

		return true; 
	}

	//Search for c in txt. Return true if we found an un-marked occurrence, false o/w
	private boolean markNext(boolean[] marked, char c) {
		for (int i = 0; i < marked.length; i++) {
			if (!marked[i] && txt.charAt(i) == c) {
				marked[i] = true; 
				return true; 
			}
		}

		return false; 
	}
}
