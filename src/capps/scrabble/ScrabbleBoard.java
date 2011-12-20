package capps.scrabble; 

import static capps.scrabble.ScrabbleConstants.*; 

import java.io.BufferedReader; 
import java.io.IOException; 

public class ScrabbleBoard{

	private Square[][] sBoard; 

	public ScrabbleBoard(BufferedReader layoutFile) throws IOException{

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


}
