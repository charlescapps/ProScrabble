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
		String tilesPlayed = "";
		int r = m.row, c = m.col, score = computeScore(m); 

		for (int i = 0; i < m.play.length(); i++) {
			if (m.dir == DIR.S) {
				if (sBoard[r+i][c].getLetter()==EMPTY) {
					sBoard[r+i][c].setLetter(m.play.charAt(i)); 
					tilesPlayed += m.play.charAt(i); 
				}
			}
			else {
				if (sBoard[r][c+i].getLetter()==EMPTY) {
					sBoard[r][c+i].setLetter(m.play.charAt(i)); 
					tilesPlayed += m.play.charAt(i); 
				}
			}
		}

		return score; 
	}

	public int computeScore(ScrabbleMove m) {
		int cumulativeScore = 0; 
		int r = m.row, c = m.col; 
		ScrabbleMove perpMove; 

		cumulativeScore += oneWordScore(m); 

		for (int i = 0; i < m.play.length(); i++) {
			if (m.dir == DIR.S) {
				if (sBoard[r+i][c].getLetter() == EMPTY && (perpMove = getImplicitPerpendicularMove(r+i, c, m)) != null) {
					cumulativeScore += oneWordScore(perpMove); 
				}
			}
			else {
				if (sBoard[r][c+i].getLetter() == EMPTY && (perpMove = getImplicitPerpendicularMove(r, c+i, m)) != null) {
					cumulativeScore += oneWordScore(perpMove); 
				}
			}
		}
		return cumulativeScore; 
	}

	public int oneWordScore(ScrabbleMove m) {
		o.println("Computing score of move:"); 
		o.println(m); 
		int r = m.row, c = m.col, score = 0, wordMult = 1; 

		for (int i = 0; i < m.play.length(); i++) {
			if (m.dir == DIR.S) {
				assert (r+i < ROWS); 
				//If the board was empty there, score is value of tile to be played,
				//and we get the letter bonuses/word bonuses
				if (sBoard[r+i][c].getLetter()==EMPTY) { 
					score += tileVal(m.play.charAt(i))*sBoard[r+i][c].letterMult;
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

	public ScrabbleMove getImplicitPerpendicularMove(int r, int c, ScrabbleMove originalMove) {
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
				return new ScrabbleMove(r, c - startI + 1, perpWord, "", DIR.E); 
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
				return new ScrabbleMove(r-startI+1, c, perpWord, "", DIR.S); 
			}
		}
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(); 

		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLS; j++) {
				sb.append(sBoard[i][j].getLetter()); 
				sb.append(' '); 
			}
			sb.append(NL); 
		}

		return sb.toString(); 
	}

	public boolean isValidMove(ScrabbleMove m) {
		if (!dict.inDict(m.play)) //must be a real word.
			return false; 

		//Must fit on board
		if (m.row < 0 || m.col < 0)
			return false; 

		int len = m.play.length(); 

		if (m.dir == DIR.S && (m.row + len > ROWS))
			return false; 

		if (m.dir == DIR.E && (m.col + len > COLS))
			return false; 

		int r = m.row, c = m.col; 
		//Collisions with letters already on board must match up
		for (int i = 0; i < len; i++) {
			if (m.dir == DIR.S && sBoard[r + i][c].getLetter() != EMPTY && sBoard[r+i][c].getLetter() != m.play.charAt(i)) {
				return false; 
			}
			else if (m.dir == DIR.E && sBoard[r][c+i].getLetter() != EMPTY && sBoard[r][c+i].getLetter() != m.play.charAt(i)) {
				return false; 
			}

			//Perp moves must be in dictionary
			ScrabbleMove perpMove; 
			if (m.dir == DIR.S && (perpMove = getImplicitPerpendicularMove(r+i, c, m)) != null && !dict.inDict(perpMove.play)) {
				return false; 
			}
			else if (m.dir == DIR.E && (perpMove = getImplicitPerpendicularMove(r, c+i, m)) != null && !dict.inDict(perpMove.play)) {
				return false; 
			}
		}

		//That about covers all the bases!
		return true; 
	}


}