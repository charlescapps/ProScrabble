package capps.scrabble; 

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class GenDict {

	private static final String USAGE 
		= "java -jar gendict.jar <input_dict_textfile> <output_obj_file> <output_txt_file>"; 

	public static void main (String [] args) 
		throws FileNotFoundException, IOException 
	{
		if (args.length != 3) {
			System.out.println(USAGE); 
			System.exit(1); 
		}

		BufferedReader dictFile = new BufferedReader(new FileReader(args[0])); 

		ScrabbleDict dict = new ScrabbleDict(dictFile); 

		FileOutputStream fos = new FileOutputStream(args[1]); 
		ObjectOutputStream oos = new ObjectOutputStream(fos); 

		System.out.println("Writing dictionary object to \"" + args[1] + "\""); 
		oos.writeObject(dict); 
		oos.close(); 

		System.out.println("Dumping dict in text format to \"" + args[2] + "\""); 
		BufferedWriter testDict = new BufferedWriter(new FileWriter(args[2])); 
		dict.dumpDict(testDict);
		
	}

}
