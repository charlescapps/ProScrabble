package capps.scrabble; 

//To avoid re-computing scores excessively
public class MoveScore {

	public final ScrabbleMove move;
	public final int score; 

	public MoveScore(ScrabbleMove m, int score) {

		this.move = m; 
		this.score = score; 
	}


}
