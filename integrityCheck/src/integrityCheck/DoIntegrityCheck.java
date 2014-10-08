package integrityCheck;

import java.io.File;
import java.util.ArrayList;

/**
 * This class contains a runnable main method to perform the integrity check.
 * 
 * @author Michèle Wyss (GitHub: {@link github.com/misch})
 */
public class DoIntegrityCheck {

	static ArrayList<File> files = new ArrayList<File>();
	
	public static void main(String args[]){
		
		// Check if there's an argument; otherwise, terminate
		if (args.length == 0){
			System.out.println("No valid mode. Please choose indexing or analysis!");
			System.exit(0);
		}
		
		String mode = args[0];
		
		// Set paths to current directory, index- and ignore-file
		String checkDirectory = System.getProperty("user.dir");
		System.out.println(checkDirectory);
		String indexFilePath = checkDirectory + "\\.index";
		String ignorePath = checkDirectory + "\\.ignore";
		
		IntegrityChecker checker = new IntegrityChecker(ignorePath);
		
		if (mode.equals("indexing")){
			checker.indexFiles(checkDirectory,indexFilePath, ignorePath);
		}
		else{
			if (mode.equals("analysis")){
				checker.analyseFiles(checkDirectory,indexFilePath, ignorePath);
			}
			else{
				System.out.println("No valid mode. Please choose indexing or analysis!");
			}
		}
	}
}
