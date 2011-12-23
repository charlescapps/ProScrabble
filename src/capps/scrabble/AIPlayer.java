package capps.scrabble; 

import static capps.scrabble.ScrabbleConstants.*; 

import java.util.ArrayList;

public class AIPlayer {
	private Rack rack;
	private ScrabbleDict dict;
	private int totalScore; 

	public AIPlayer(String initialTiles, ScrabbleDict dict) 
			throws ScrabbleException {
		this.rack = new Rack(initialTiles);
		this.dict = dict; 
		this.totalScore = 0; 
	}

	public int getTotalScore() {
		return totalScore; 
	}

	public Rack getRack() {
		return rack; 
	}

	public MoveScore getBestMove(ScrabbleBoard b) throws ScrabbleException{
		Square[][] board = b.getBoardCopy(); 

		if (b.isFirstMove()) {
			return getBestFirstMove(b, board); 
		}

		MoveScore bestSoFar = null;
		MoveScore tmpMoveScore = null; 
		boolean[][] searchedS=new boolean[ROWS][COLS]; 
		boolean[][] searchedE=new boolean[ROWS][COLS]; 

		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLS; j++) {
				if (board[i][j].getLetter() != EMPTY){
					tmpMoveScore = genBestLocalMove(b,board,i,j,searchedS,searchedE); 
					if (tmpMoveScore==null)
						continue; 
					if (bestSoFar == null || tmpMoveScore.score > bestSoFar.score)
						bestSoFar = tmpMoveScore; 
				}
			}
		}

		return bestSoFar; 
		
	}

	public MoveScore getBestFirstMove(ScrabbleBoard sb, Square[][] b) {
		
		assert (rack.toString().length() == 7); 

		final int center = 7;
		ScrabbleMove bestMoveSoFar=null; 
		int bestScoreSoFar=0; 

		//Just try vertical since the board is symmetric rotated 90 degrees
		for (int r = center - 6; r <= center; r++) {
			int MIN_LEN = center - r + 1; 
			for (int l = MIN_LEN; l <= 7; l++) {
				ArrayList<String> substrings = rack.getSubstringsOfRack(l); 
				for (String sub: substrings) {
					ArrayList<String> matches = dict.getAnagrams(sub); 
					if (matches == null) 
						continue; 

					for (String m: matches) {
						ScrabbleMove move 
							= new ScrabbleMove(r, r+l-1, m, rack.hasTiles(m), DIR.S); 

						if (sb.isValidMove(move) ) {
							int score = sb.computeScore(move); 
							if (bestMoveSoFar == null || bestScoreSoFar < score){
								bestMoveSoFar = move; 
								bestScoreSoFar = score; 
							}
						}
					}
				}
			}
		}

		if (bestMoveSoFar == null)
			return null; 
		return new MoveScore(bestMoveSoFar,bestScoreSoFar); 
	}

	//r and c start on a non-empty square. 
	//If (r,c) is the start of a word on board, look for prefixes/suffixes
	//Else, just look for perpendicular words
	public MoveScore genBestLocalMove(ScrabbleBoard sb, Square[][] b, int r, int c,
			boolean[][] searchedS, boolean[][] searchedE) 
		throws ScrabbleException{

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
		if (!searchedS[r][c]) {
			String base; 
			if ((base = getWordStartingHere(b,r,c,d)) != null) {
				//o.println("Base word: " + base); 
				Word w = dict.exactMatch(base); 
				if (w == null) {
					o.println("Invalid word on board at (" + r+","+c+")"); 
				}
				else {
					for (Word p: w.getPrefixes()) { //Check if prefixes work
						int delta = p.toString().length() - base.length(); 
						if (r - delta < 0)
							continue;

						StringBuffer tilesNeeded = new StringBuffer(); 

						for (int i = 0; i < delta; i++) {
							if (b[r-delta+i][c].getLetter() == EMPTY)
								tilesNeeded.append(p.toString().charAt(i)); 
						}
						String grabNeededTiles; 	
						if ( (grabNeededTiles = rack.hasTiles(tilesNeeded.toString()))==null){
							continue; 
						}

						ScrabbleMove tryMove = 
							new ScrabbleMove(r-delta,c,p.toString(),
									grabNeededTiles, d); 

						if (sb.isValidMove(tryMove)) {
							int tmp = sb.computeScore(tryMove); 
							if (tmp > bestScoreSoFar) {
								bestScoreSoFar = tmp; 
								bestMove = tryMove; 
							}
						}
					}
				}
				for (int r1 = r; ;r1++) {
					if (r1 >= ROWS || b[r1][c].getLetter() == EMPTY)
						break; 
					searchedS[r1][c] = true; 
				}
			}
			//Perp word case going SOUTH, i.e. we're in the middle of a word
			else {
				//NEED helper function to get all possible perp words that fit on
				//board and match up with existing tiles
				ArrayList<ScrabbleMove> perpMoves = getPerpMoves
					(sb,b,r,c,DIR.S); 
				for (ScrabbleMove m: perpMoves) {
					int score = sb.computeScore(m); 
					if (bestMove == null || bestScoreSoFar < score) {
						bestMove = m; 
						bestScoreSoFar = score; 
					}
				}
			}
		}

		if (!searchedE[r][c]) {
			//EAST case:
			d = DIR.E; 
			String base; 
			if ((base = getWordStartingHere(b,r,c,d)) != null) {
				//o.println("Base word: " + base); 
				Word w = dict.exactMatch(base); 
				if (w == null) {
					o.println("Invalid word on board at (" + r+","+c+")"); 
				}
				else {
					for (Word p: w.getPrefixes()) { //Check if prefixes work
						//o.println("\tPrefix found: " + p); 
						int delta = p.toString().length() - base.length(); 
						if (c - delta < 0)
							continue;

						StringBuffer tilesNeeded = new StringBuffer(); 

						for (int i = 0; i < delta; i++) {
							if (b[r][c-delta+i].getLetter() == EMPTY)
								tilesNeeded.append(p.toString().charAt(i)); 
						}
						
						String grabTiles; 
						if ( (grabTiles = rack.hasTiles(tilesNeeded.toString()))==null){
							//o.println("Doesn't have tiles needed"); 
							continue; 
						}

						ScrabbleMove tryMove = 
							new ScrabbleMove(r,c-delta,p.toString(),
									grabTiles, d); 

						if (sb.isValidMove(tryMove)) {
							int tmp = sb.computeScore(tryMove); 
							if (tmp > bestScoreSoFar) {
								bestScoreSoFar = tmp; 
								bestMove = tryMove; 
						//		o.println("New best move from east prefix at (" + r + "," + c + ")\n" + bestMove); 
							}
						}
					}
				}
				for (int c1 = c; ;c1++) {
					if (c1 >= COLS || b[r][c1].getLetter() == EMPTY)
						break; 
					searchedE[r][c1] = true; 
				}
			}
			//Perp word case going EAST, i.e. we're in the middle of a word
			else {
				//NEED helper function to get all possible perp words that fit on
				//board and match up with existing tiles
				ArrayList<ScrabbleMove> perpMoves = getPerpMoves
					(sb,b,r,c,DIR.E); 
				for (ScrabbleMove m: perpMoves) {
					int score = sb.computeScore(m); 
					if (bestMove == null || bestScoreSoFar < score) {
						bestMove = m; 
						bestScoreSoFar = score; 
					}
				}
			}
		}

		if (bestMove == null)
			return null; 
		else
			return new MoveScore(bestMove, bestScoreSoFar); 
	}

	private ArrayList<ScrabbleMove> getPerpMoves
		(ScrabbleBoard sb, Square[][] squares, int r, int c, DIR d) throws ScrabbleException {
		if (r < 0 || r >= ROWS || c < 0 || c >= COLS) 
			return null; 

		int numTiles = rack.toString().length();

		ArrayList<ScrabbleMove> moves = new ArrayList<ScrabbleMove>(); 

		if (d == DIR.S) {

			for (int i = 0; r - i >= 0; i++) {
				for (int j = 0; r + j < ROWS; j++) {
					int numTilesReq = 0; 

					for (int r1 = r-i; r1 <= r+j; r1++) {
						if (squares[r1][c].getLetter()==EMPTY)
							numTilesReq++; 
					}

					if (numTilesReq == 0)
						continue; 
					if (numTilesReq > numTiles)
						break; 

					ArrayList<String> substr = rack.getSubstringsOfRack(numTilesReq); 
					for (String rackStr: substr) {
						assert(rackStr.length() == numTilesReq); 
						StringBuffer buildCandStr = new StringBuffer(); 
						int rackI = 0; 
						for (int r1 = r-i; r1 <= r+j; r1++) {
							if (squares[r1][c].getLetter()==EMPTY)
								buildCandStr.append(rackStr.charAt(rackI++));
							else
								buildCandStr.append(squares[r1][c].getLetter());
						}
						String cand = buildCandStr.toString(); 
						ArrayList<String> matches = dict.getAnagrams(cand); 
						if (matches == null) 
							continue; 

						//For each possible match, check if it lines up with board
						boolean linesUp = false; 
						for (String m: matches) {
							linesUp = true; 
							for (int r1 = r-i; r1 <= r+j; r1++) {
								if (squares[r1][c].getLetter() != EMPTY
										&& squares[r1][c].getLetter() != m.charAt(r1-(r-i))) {
									linesUp = false;
									break;
								}
							}
							if (!linesUp)
								continue;


							StringBuffer rackStrInOrder = new StringBuffer(); 
							for (int r1 = r-i; r1 <= r+j; r1++) {
								if (squares[r1][c].getLetter() == EMPTY)
									rackStrInOrder.append(m.charAt(r1-r+i)); 
							}

							String grabTiles = rack.hasTiles(rackStrInOrder.toString()); 
							if (grabTiles == null) {
								o.println("Didn't have tiles '" + rackStrInOrder.toString() + "'"); 
								o.println("Tried to play word '" + m + "'"); 
								o.println(); 
								continue; 
							}

							ScrabbleMove move = new ScrabbleMove(r-i,c,m,grabTiles,DIR.S);
							if (sb.isValidMove(move)){
								//o.println("Valid move found:\n" + move);
								//o.println(); 
								moves.add(move); 
							}
						}
					}
				}
			}
		}
		else {

			for (int i = 0; c - i >= 0; i++) {
				for (int j = 0; c + j < COLS; j++) {
					int numTilesReq = 0; 

					for (int c1 = c-i; c1 <= c+j; c1++) {
						if (squares[r][c1].getLetter()==EMPTY)
							numTilesReq++; 
					}

					if (numTilesReq == 0)
						continue; 
					if (numTilesReq > numTiles)
						break; 

					ArrayList<String> substr = rack.getSubstringsOfRack(numTilesReq); 
					for (String rackStr: substr) {
						assert(rackStr.length() == numTilesReq); 
						StringBuffer buildCandStr = new StringBuffer(); 
						int rackI = 0; 
						for (int c1 = c-i; c1 <= c+j; c1++) {
							if (squares[r][c1].getLetter()==EMPTY)
								buildCandStr.append(rackStr.charAt(rackI++));
							else
								buildCandStr.append(squares[r][c1].getLetter());
						}
						String cand = buildCandStr.toString(); 
						ArrayList<String> matches = dict.getAnagrams(cand); 
						if (matches == null) 
							continue; 

						//For each possible match, check if it lines up with board
						boolean linesUp = false; 
						for (String m: matches) {
							linesUp = true; 
							for (int c1 = c-i; c1 <= c+j; c1++) {
								if (squares[r][c1].getLetter() != EMPTY
										&& squares[r][c1].getLetter() != m.charAt(c1-(c-i))) {
									linesUp = false;
									break;
								}
							}
							if (!linesUp)
								continue;

							StringBuffer rackStrInOrder = new StringBuffer(); 
							for (int c1 = c-i; c1 <= c+j; c1++) {
								if (squares[r][c1].getLetter() == EMPTY)
									rackStrInOrder.append(m.charAt(c1-c+i)); 
							}

							String grabTiles = rack.hasTiles(rackStrInOrder.toString()); 
							if (grabTiles == null) {
								o.println("Didn't have tiles '" + rackStrInOrder.toString() + "'"); 
								o.println("Tried to play word '" + m + "'"); 
								o.println(); 
								continue; 
							}
							ScrabbleMove move = new ScrabbleMove(r,c-i,m,grabTiles,DIR.E);
							if (sb.isValidMove(move)) {
								if (grabTiles.equals("")){
									o.println("grabTiles==\"\" in perpMoves"); 
									o.println("rackStr==\"" + rackStr + "\""); 
									o.println("\tPlay:"+m); 
									o.println(move); 
								}
								moves.add(move); 
							}
						}
					}
				}
			}
		}

		return moves; 

	}


	public String getWordStartingHere(Square[][] b, int r, int c, DIR d) {
		if (d == DIR.S) {
			if (r > 0 && b[r-1][c].getLetter() != EMPTY)
				return null; 
			String southerly = ""; 

			int i =0; 
			while (r+i < ROWS && b[r+i][c].getLetter() != EMPTY) {
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
			while (c+i < COLS && b[r][c+i].getLetter() != EMPTY) {
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
		int score = b.makeMove(m); 
		totalScore += score; 
		return score; 
	}

	public void addTiles(String t) throws ScrabbleException {
		rack.addTiles(t); 
	}


}
