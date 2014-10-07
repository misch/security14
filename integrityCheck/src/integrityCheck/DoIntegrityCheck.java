package integrityCheck;

import java.io.File;
import java.util.ArrayList;

public class DoIntegrityCheck {
	
	static ArrayList<File> files = new ArrayList<File>();
	private static final boolean INDEX = true;
	private static final boolean ANALYSE = false;
	
	public static void main(String args[]){
		String checkDirectory = "C:/Users/Misch/TestIntegrity";
		String indexFilePath = "C:/Users/Misch/TestIntegrity/.index";
		String ignorePath = "C:/Users/Misch/TestIntegrity/.ignore";
		
		IntegrityChecker checker = new IntegrityChecker(ignorePath);
		
		boolean mode = ANALYSE;
		
		if (mode == INDEX){
			checker.indexFiles(checkDirectory,indexFilePath, ignorePath);
		}
		else
			checker.analyseFiles(checkDirectory,indexFilePath, ignorePath);
		
	}
}
