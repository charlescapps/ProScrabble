package capps.scrabble; 

import static capps.scrabble.ScrabbleConstants.*; 

import java.util.ArrayList;

public class AIPlayer {
	private Rack rack;
	private ScrabbleDict dict;

	public AIPlayer(String initialTiles, ScrabbleDict dict) 
			throws ScrabbleException {
		rack = new Rack(initialTiles);
		this.dict = dict; 
	}

	public MoveScore getBestMove(ScrabbleBoard b) {
		Square[][] board = b.getBoardCopy(); 
		MoveScore bestSoFar = null;
		MoveScore tmpMoveScore = null; 

		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLS; j++) {
				if (board[i][j].getLetter() != EMPTY){
//					o.println("Getting best score at ("+i+","+j+")"); 
					tmpMoveScore = genBestLocalMove(b,i,j); 
					if (tmpMoveScore==null)
						continue; 
					if (bestSoFar == null || tmpMoveScore.score > bestSoFar.score)
						bestSoFar = tmpMoveScore; 
				}
			}
		}

		return bestSoFar; 
		
	}

	//r and c start on a non-empty square. 
	//If (r,c) is the start of a word on board, look for prefixes/suffixes
	//Else, just look for perpendicular words
	public MoveScore genBestLocalMove(ScrabbleBoard sb, int r, int c) {

		Square[][] b = sb.getBoardCopy(); 
		//o.println("Got copy:");
		//o.println(ScrabbleBoard.toString(b)); 

		if (r < 0 || r >= ROWS || c < 0 || c >= COLS) {
			return null;
		}
		if (b[r][c].getLetter() == EMPTY) {
			return null; 
		}

		int bestScoreSoFar = 0; 
		ScrabbleMove bestMove = null; 

		//Looking if we're at start of a word
		//SOUTH case:
		DIR d = DIR.S; 
		String base; 
		if ((base = getWordStartingHere(b,r,c,d)) != null) {
			o.println("Base word: " + base); 
			Word w = dict.exactMatch(base); 
			if (w == null) {
				o.println("Invalid word on board at (" + r+","+c+")"); 
			}
			else {
				for (Word p: w.getPrefixes()) { //Check if prefixes work
					o.println("\tPrefix found: " + p); 
					int delta = p.toString().length() - base.length(); 
					if (r - delta < 0)
						continue;

					StringBuffer tilesNeeded = new StringBuffer(); 

					for (int i = 0; i < delta; i++) {
						if (b[r-delta+i][c].getLetter() == EMPTY)
							tilesNeeded.append(b[r-delta+i][c].getLetter()); 
					}
					
					if (!rack.hasTiles(tilesNeeded.toString()))
						continue; 

					ScrabbleMove tryMove = 
						new ScrabbleMove(r-delta,c,p.toString(),
								tilesNeeded.toString(), d); 

					if (sb.isValidMove(tryMove)) {
						int tmp = sb.computeScore(tryMove); 
						if (tmp > bestScoreSoFar) {
							bestScoreSoFar = tmp; 
							bestMove = tryMove; 
						}
					}
				}
			}
		}
		else {

		}


		if (bestMove == null)
			return null; 
		else
			return new MoveScore(bestMove, bestScoreSoFar); 
	}

	private ArrayList<String> getPrefixesFromRack(String base, int maxLen) {
		String tiles = rack.toString(); 
		Word match = null; 
		int baseLen = base.length();
		int MAX = 1 << Math.min(tiles.length(), maxLen); 
		for (int i = 0; i < MAX; i++) {
			
		}
		return null; 

	}

	public String getWordStartingHere(Square[][] b, int r, int c, DIR d) {
		if (d == DIR.S) {
			if (r > 0 && b[r-1][c].getLetter() != EMPTY)
				return null; 
			String southerly = ""; 

			int i =0; 
			while (r < ROWS && b[r+i][c].getLetter() != EMPTY) {
				southerly += b[r+i][c].getLetter(); 
				i++; 
			}
			if (southerly.length() < 2)
				return null; 
			return southerly; 
		}
		else {
			if (c > 0 && b[r][c-1].getLetter() != EMPTY)
				return null; 
			String easterly = ""; 

			int i =0; 
			while (b[r][c+i].getLetter() != EMPTY) {
				easterly += b[r][c+i].getLetter(); 
				i++; 
			}
			if (easterly.length() < 2)
				return null; 
			return easterly; 
		}
	}

	//Play move on board and remove tiles from hand. Returns score of move.
	public int playMove(ScrabbleBoard b, ScrabbleMove m) 
			throws ScrabbleException {
		rack.removeTiles(m.tilesUsed); 
		return b.makeMove(m); 
	}

	public void addTiles(String t) throws ScrabbleException {
		rack.addTiles(t); 
	}


}
