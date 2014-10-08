package integrityCheck;

import java.io.File;
import java.util.ArrayList;

public class DoIntegrityCheck {
	
	static ArrayList<File> files = new ArrayList<File>();
	
	public static void main(String args[]){
		if (args.length == 0){
			System.out.println("No valid mode. Please choose indexing or analysis!");
			System.exit(0);
		}
		
		String mode = args[0];
		
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
