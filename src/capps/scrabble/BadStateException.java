package capps.scrabble; 

public class BadStateException extends Exception {
	public BadStateException() {
		super();
	}

	public BadStateException(String msg) {
		super(msg);
	}
}
