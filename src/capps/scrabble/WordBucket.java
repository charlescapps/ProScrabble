package	capps.scrabble; 

import java.util.Iterator;

public class WordBucket implements Iterable<Word> {

	//Nested class for iterating over this uber-simple linked list
	public static final class WordIter implements Iterator<Word> {
		private WordBucket b; 

		public WordIter(WordBucket w) {
			b = w; 
		}

		@Override
		public boolean hasNext() {
			return b!=null; 
		}

		@Override
		public Word next() {
			Word w = b.getWord(); 
			b = b.getNext(); 
			return w; 
		}

		@Override
		public void remove() {}
	}

	private WordBucket next;
	private Word w; 

	public WordBucket(Word w) {
		this.w = w; 
		next = null; 
	}

	public WordBucket(Word w, WordBucket wb) {
		this.w = w;
		this.next = wb;  
	}

	public Iterator<Word> iterator() {
		return new WordIter(this); 

	}

	public Word getWord() {
		return w;
	}

	public void setNext(WordBucket n) {
		next = n; 
	}

	public WordBucket getNext() {
		return next; 
	}

	public WordBucket add(Word w) {
		WordBucket newHead = new WordBucket(w); 
		newHead.setNext(this); 
		return newHead; 
	}
}
