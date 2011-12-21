package capps.scrabble; 

public class ScrabbleException extends Exception {
	public ScrabbleException() {
		super("Scrabble Exception: you probably input invalid tiles to remove or an invalid move");
	}

	public ScrabbleException(String s) {
		super(s); 
	}
}
