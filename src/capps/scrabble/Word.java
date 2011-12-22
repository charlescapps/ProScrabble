package capps.scrabble;

import java.util.ArrayList;

public class Word implements java.io.Serializable {
	private static final long serialVersionUID = 0x22222222;

	private String txt; 
	private ArrayList<Word> suffixes; 
	private ArrayList<Word> prefixes; 

	public Word(String w) {
		txt = w; 
		suffixes = new ArrayList<Word>(); 
		prefixes = new ArrayList<Word>(); 
	}

	public ArrayList<Word> getSuffixes() {
		return suffixes; 
	}

	public ArrayList<Word> getPrefixes() {
		return prefixes; 
	}

	public String toString() {return txt; }

	public String toLongString() {
		StringBuffer sb = new StringBuffer(); 
		sb.append(txt); 
		sb.append(" prefixes: "); 
		for (Word w: prefixes) {
			sb.append(w.toString() + " "); 
		}
		sb.append(" suffixes: "); 
		for (Word w: suffixes) {
			sb.append(w.toString() + " "); 
		}

		return sb.toString(); 
	}

	public void addSuffix(Word s) {
		suffixes.add(s); 
	}

	public void addPrefix(Word s) {
		prefixes.add(s); 
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
