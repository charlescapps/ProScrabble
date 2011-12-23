package capps.scrabble; 

import static capps.scrabble.ScrabbleConstants.*; 

import java.io.BufferedReader; 
import java.io.IOException; 

public class ScrabbleBoard{


	private Square[][] sBoard; 
	private ScrabbleDict dict; 

	public ScrabbleBoard(BufferedReader layoutFile, ScrabbleDict dict) throws IOException{

		this.dict = dict; 

		//Strings in the text file giving the board layout
		final String doubleLett = "DL"; 
		final String doubleWord = "DW"; 
		final String tripleLett = "TL"; 
		final String tripleWord = "TW"; 
		final String fileBlank = "B"; 

		sBoard = new Square[ROWS][COLS]; 

		String line; 
		int r=0, c=0; 
		while ( (line = layoutFile.readLine()) != null) {
			String[] tokens = line.split(sBLANK); 	
			assert(tokens.length==COLS);//Demand our text file has the proper number of cols =)
			for (c = 0; c < COLS; c++) {
				if (tokens[c].equals(fileBlank)) {
					sBoard[r][c]=new Square(); 
				}
				else if (tokens[c].equals(doubleLett)){
					sBoard[r][c] = new Square(2,1); 
				}
				else if (tokens[c].equals(doubleWord)) {
					sBoard[r][c] = new Square(1,2); 
				}
				else if (tokens[c].equals(tripleLett)) {
					sBoard[r][c] = new Square(3,1); 
				}
				else if (tokens[c].equals(tripleWord)) {
					sBoard[r][c] = new Square(1,3); 
				}
				else {
					throw new IOException("Scrabble layout file has improper format at (" + r + "," + c + ")"); 
				}
			}
			++r; 
		}
		assert(r == ROWS); //Demand text file has 15 rows like a scrabble board!

	}

	//Returns point value of move
	public int makeMove(ScrabbleMove m) {
		int r = m.row, c = m.col, score = computeScore(m); 

		for (int i = 0; i < m.play.length(); i++) {
			if (m.dir == DIR.S) {
				if (sBoard[r+i][c].getLetter()==EMPTY) {
					sBoard[r+i][c].setLetter(m.play.charAt(i)); 
				}
			}
			else {
				if (sBoard[r][c+i].getLetter()==EMPTY) {
					sBoard[r][c+i].setLetter(m.play.charAt(i)); 
				}
			}
		}

		return score; 
	}

	public int computeScore(ScrabbleMove m) {
		if (!isValidMove(m))
			return -1; 
		int cumulativeScore = 0; 
		int r = m.row, c = m.col; 
		ScrabbleMove perpMove; 

		cumulativeScore += oneWordScore(m); 

		int i = 0; 
		//Here: we scan the length of the play + possible suffix. 
		//No need to consider prefixes--a move by definition starts at the beginning of a played word
		if (m.dir == DIR.S) {
			int tilesIndex = 0;
			while ((r+i < ROWS) && (i < m.play.length() || sBoard[r+i][c].getLetter() != EMPTY)) {
				if (sBoard[r+i][c].getLetter() == EMPTY 
						&& (perpMove = getImplicitPerpendicularMove(r+i, c, m, m.tilesUsed.charAt(tilesIndex))) != null) {
					cumulativeScore += oneWordScore(perpMove); 
				}
				if (sBoard[r+i][c].getLetter()==EMPTY)
					tilesIndex++;
				i++; 
			}
		}
		else {
			int tilesIndex = 0;
			while ( (c+i < COLS) && (i < m.play.length() || sBoard[r][c+i].getLetter() != EMPTY)) {
				if (sBoard[r][c+i].getLetter() == EMPTY 
					&& (perpMove = getImplicitPerpendicularMove(r, c+i, m,m.tilesUsed.charAt(tilesIndex))) != null) {
					cumulativeScore += oneWordScore(perpMove); 
				}
				if (sBoard[r][c+i].getLetter()==EMPTY)
					tilesIndex++; 
				i++;
			}
		}
		if (m.tilesUsed.length() == 7)
			cumulativeScore += 50; 
		return cumulativeScore; 
	}

	public int oneWordScore(ScrabbleMove m) {
		//o.println("Computing score of move:"); 
		//o.println(m); 
		int r = m.row, c = m.col, score = 0, wordMult = 1; 

		int tilesIndex = 0; 
		for (int i = 0; i < m.play.length(); i++) {
			if (m.dir == DIR.S) {
				assert (r+i < ROWS); 
				//If the board was empty there, score is value of tile to be played,
				//and we get the letter bonuses/word bonuses
				if (sBoard[r+i][c].getLetter()==EMPTY) { 
					score += tileVal(m.tilesUsed.charAt(tilesIndex++))*sBoard[r+i][c].letterMult;
					wordMult *= sBoard[r+i][c].wordMult; 
				}
				//If the board wasn't empty, we don't get the letter/word bonuses
				else {
					score += tileVal(sBoard[r+i][c].getLetter());
				}
			}
			else {
				assert (c+i < COLS); 
				//If the board was empty there, score is value of tile to be played,
				//and we get the letter bonuses/word bonuses
				if (sBoard[r][c+i].getLetter()==EMPTY) { 
					score += tileVal(m.play.charAt(i))*sBoard[r][c+i].letterMult;
					wordMult *= sBoard[r][c+i].wordMult; 
				}
				//If the board wasn't empty, we don't get the letter/word bonuses
				else {
					score += tileVal(sBoard[r][c+i].getLetter());
				}
			}
		}
		return score*wordMult; 
	}

	public ScrabbleMove getImplicitPerpendicularMove(int r, int c, ScrabbleMove originalMove, char tileUsed) {
		if (sBoard[r][c].getLetter() != EMPTY)
			return null; 

		if (originalMove.dir == DIR.S) {
			if ( (c == 0 || sBoard[r][c-1].getLetter() == EMPTY) && (c == COLS - 1 || sBoard[r][c+1].getLetter()==EMPTY)) {
				return null; //No perpendicular move.Check this first since it will be most frequent.
			}
			else {
				int startI = 1, endI = 1; 
				while (c - startI >= 0 && sBoard[r][c-startI].getLetter() != EMPTY ) {
					startI++; 
				}
				while (c + endI < COLS && sBoard[r][c+endI].getLetter() != EMPTY) {
					endI++;
				}
				//assert (startI != endI) : "startI = " + startI + ", r = " + r + ", c = " + c; 
				char originalChar = originalMove.play.charAt(r - originalMove.row); 
				String perpWord = ""; 
				for (int i = c - startI + 1; i < c + endI; i++) {
					if (i == c) {
						perpWord += originalChar; 
					}
					else {
						perpWord += sBoard[r][i].getLetter(); 
					}
				}
				return new ScrabbleMove(r, c - startI + 1, perpWord, "" + tileUsed, DIR.E); 
			}
		}
		else {
			if ( (r == 0 || sBoard[r-1][c].getLetter() == EMPTY) && (r == ROWS - 1 || sBoard[r+1][c].getLetter()==EMPTY)) {
				return null; //No perpendicular move.Check this first since it will be most frequent.
			}
			else {
				int startI = 1, endI = 1; 
				while (r - startI >= 0 && sBoard[r-startI][c].getLetter() != EMPTY ) {
					startI++; 
				}
				while (r + endI < ROWS && sBoard[r+endI][c].getLetter() != EMPTY) {
					endI++;
				}
				//assert(startI != endI); 
				char originalChar = originalMove.play.charAt(c - originalMove.col); 
				String perpWord = ""; 
				for (int i = r - startI + 1; i < r + endI; i++) {
					if (i == r) {
						perpWord += originalChar; 
					}
					else {
						perpWord += sBoard[i][c].getLetter(); 
					}
				}
				return new ScrabbleMove(r-startI+1, c, perpWord, "" + tileUsed, DIR.S); 
			}
		}
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(); 
		sb.append("  0 1 2 3 4 5 6 7 8 9 1011121314" + NL); 

		for (int i = 0; i < ROWS; i++) {
			sb.append(i < 10 ? i + " " : i); 
			for (int j = 0; j < COLS; j++) {
				sb.append(sBoard[i][j].getLetter()); 
				sb.append(' '); 
			}
			sb.append(NL); 
		}

		return sb.toString(); 
	}

	public static String toString(Square[][] board) {
		StringBuffer sb = new StringBuffer(); 

		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLS; j++) {
				sb.append(board[i][j].getLetter()); 
				sb.append(' '); 
			}
			sb.append(NL); 
		}

		return sb.toString(); 

	}

	public boolean isValidMove(ScrabbleMove m) {
		if (m.tilesUsed.length() > 7 || m.tilesUsed.length() <= 0) 
			return false; 

		int len = m.play.length(); 

		if (len < 2)
			return false; 

		if (!dict.inDict(m.play)) //must be a real word.
			return false; 

		//Must fit on board
		if (m.row < 0 || m.col < 0)
			return false; 

		if (m.dir == DIR.S && (m.row + len > ROWS))
			return false; 

		if (m.dir == DIR.E && (m.col + len > COLS))
			return false; 

		boolean nextToSomething = false; 
		int r = m.row, c = m.col; 

		if (m.dir == DIR.S) { //Check if it's a prefix/suffix of some word
			if (c == 7 && r <= 7 && r+len-1 >= 7) //First move case
				nextToSomething = true;
			if (sBoard[Math.max(r-1,0)][c].getLetter() != EMPTY
					|| sBoard[Math.min(r+len,ROWS-1)][c].getLetter() != EMPTY) {
				nextToSomething = true;
			}
		}
		else if (m.dir == DIR.E) {
			if (r == 7 && c <= 7 && c+len - 1 >=7)
				nextToSomething = true; 
			if (sBoard[r][Math.max(c-1,0)].getLetter() != EMPTY 
					|| sBoard[r][Math.min(c+len,COLS-1)].getLetter() != EMPTY) {
				nextToSomething = true;
			}
		}

		//Collisions with letters already on board must match up
		StringBuffer tilesNeeded = new StringBuffer();  
		for (int i = 0; i < len; i++) {
			if (m.dir==DIR.S && sBoard[r+i][c].getLetter() == EMPTY)
				tilesNeeded.append(m.play.charAt(i)); 
			else if (m.dir==DIR.E && sBoard[r][c+i].getLetter() == EMPTY)
				tilesNeeded.append(m.play.charAt(i)); 

			if (m.dir == DIR.S && sBoard[r + i][c].getLetter() != EMPTY && sBoard[r+i][c].getLetter() != m.play.charAt(i)) {
				return false; 
			}
			else if (m.dir == DIR.E && sBoard[r][c+i].getLetter() != EMPTY && sBoard[r][c+i].getLetter() != m.play.charAt(i)) {
				return false; 
			}

			//Perp moves must be in dictionary
			ScrabbleMove perpMove; 
			if (m.dir == DIR.S 
					&& (perpMove = getImplicitPerpendicularMove(r+i, c, m, WILDCARD)) != null 
					&& !dict.inDict(perpMove.play)) {
				return false; 
			}
			else if (m.dir == DIR.E 
					&& (perpMove = getImplicitPerpendicularMove(r, c+i, m, WILDCARD)) != null 
					&& !dict.inDict(perpMove.play)) {
				return false; 
			}

			if (m.dir == DIR.S && (sBoard[r+i][c].getLetter() != EMPTY
									|| sBoard[r+i][Math.max(c-1,0)].getLetter() != EMPTY 
									|| sBoard[r+i][Math.min(c+1,COLS-1)].getLetter()!=EMPTY)) {
				nextToSomething = true; 
			}
			else if (m.dir == DIR.E && sBoard[r][c+i].getLetter() != EMPTY
									|| sBoard[Math.max(r-1,0)][c].getLetter() != EMPTY
									|| sBoard[Math.min(r+1,ROWS-1)][ROWS-1].getLetter() != EMPTY) {
				nextToSomething = true; 
			}
		}

		if (!equalsWithWild(tilesNeeded.toString(), m.tilesUsed))
			return false; 

		//That about covers all the bases!
		//o.println("Next to somethin'? " + nextToSomething); 
		return nextToSomething; 
	}

	private boolean equalsWithWild(String s, String rackStr) {
		if (s.length() != rackStr.length())
			return false; 

		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) != rackStr.charAt(i) && rackStr.charAt(i) != WILDCARD)
				return false; 
		}
		return true; 
	}

	public Square[][] getBoardCopy() {
		Square[][] copy = new Square[ROWS][COLS]; 
		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLS; j++) {
				copy[i][j] = (Square) sBoard[i][j].clone(); 
			}
		}
		return copy; 
	}

}
