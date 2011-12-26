package	capps.scrabble; 

import java.util.Iterator;

public class StringBucket 
	implements Iterable<String>, java.io.Serializable {
	private static final long serialVersionUID = 0x11111111;

	//Nested class for iterating over this uber-simple linked list
	public static final class StringIter implements Iterator<String> {
		private StringBucket b; 

		public StringIter(StringBucket w) {
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

	private StringBucket next;
	private String w; 

	public StringBucket(String w) {
		this.w = w; 
		next = null; 
	}

	public StringBucket(String w, StringBucket wb) {
		this.w = w;
		this.next = wb;  
	}

	public Iterator<String> iterator() {
		return new StringIter(this); 

	}

	public String getWord() {
		return w;
	}

	public void setNext(StringBucket n) {
		next = n; 
	}

	public StringBucket getNext() {
		return next; 
	}

	public StringBucket add(String w) {
		StringBucket newHead = new StringBucket(w); 
		newHead.setNext(this); 
		return newHead; 
	}
}
