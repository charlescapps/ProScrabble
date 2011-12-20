package capps.scrabble; 

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
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
			o.println("Loading scrabble layout from \"" + args[0] + "\""); 
			sBoard = new ScrabbleBoard(layoutFile); 
		}
		catch (IOException e) {
			o.println("Error parsing \"" + args[0] + "\""); 
			e.printStackTrace(); 
			System.exit(1); 
		}


		Word w1 = new Word("abcdef"); 
		Word w2 = new Word("fdceba"); 

		o.println("\"abcdef\" is anagram of \"fdcebd\"? " + w1.isAnagram(w2.toString())); 

		o.println("Loading scrabble dictionary from \"" + args[1] + "\""); 
		try {
			dict = new ScrabbleDict(dictFile); 
			BufferedWriter testDict = new BufferedWriter(new FileWriter("data/testDict.txt")); 
			dict.dumpDict(testDict);
		}
		catch (IOException e) {
			e.printStackTrace(); 
			System.exit(1); 
		}
	}

}
