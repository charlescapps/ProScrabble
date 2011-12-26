package	capps.scrabble; 

import java.util.Iterator;

public class WordBucket 
	implements Iterable<String>, java.io.Serializable {
	private static final long serialVersionUID = 0x11111111;

	//Nested class for iterating over this uber-simple linked list
	public static final class StringIter implements Iterator<String> {
		private WordBucket b; 

		public StringIter(WordBucket w) {
			b = w; 
		}

		@Override
		public boolean hasNext() {
			return b!=null; 
		}

		@Override
		public String next() {
			String w = b.getWord(); 
			b = b.getNext(); 
			return w; 
		}

		@Override
		public void remove() {}
	}

	private WordBucket next;
	private String w; 

	public WordBucket(String w) {
		this.w = w; 
		next = null; 
	}

	public WordBucket(String w, WordBucket wb) {
		this.w = w;
		this.next = wb;  
	}

	public Iterator<String> iterator() {
		return new StringIter(this); 

	}

	public String getWord() {
		return w;
	}

	public void setNext(WordBucket n) {
		next = n; 
	}

	public WordBucket getNext() {
		return next; 
	}

	public WordBucket add(String w) {
		WordBucket newHead = new WordBucket(w); 
		newHead.setNext(this); 
		return newHead; 
	}
}
