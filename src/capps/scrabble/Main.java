package capps.scrabble; 

import java.io.FileNotFoundException;
import java.io.IOException;

import static capps.scrabble.ScrabbleConstants.*; 
import java.io.BufferedReader;
import java.io.FileReader;

public class Main {
	private final static String USAGE = "java -jar scrabble.jar \"layout_file\" \"dict_file\""; 

	private static BufferedReader layoutFile, dictFile; 

	private static ScrabbleBoard sBoard; 
	private static ScrabbleDict dict; 

	public static void main (String [] args)
	{
		if (args.length != 2) {
			o.println(USAGE); 
			System.exit(1); 
		}

		try {
			layoutFile = new BufferedReader(new FileReader(args[0])); 
			dictFile = new BufferedReader(new FileReader(args[1])); 
		}
		catch (FileNotFoundException e) {
			o.println("Invalid file names given."); 
			System.exit(1); 
		}

		try {
			sBoard = new ScrabbleBoard(layoutFile); 
		}
		catch (IOException e) {
			o.println("Error parsing \"" + args[0] + "\""); 
			e.printStackTrace(); 
			System.exit(1); 
		}

		o.println(sBoard); 
	}

}
