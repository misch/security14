package integrityCheck;

import java.io.File;
import java.util.ArrayList;

public class DoIntegrityCheck {
	
	static ArrayList<File> files = new ArrayList<File>();
	
	public static void main(String args[]){
		IntegrityChecker checker = new IntegrityChecker();
		
//		checker.indexFiles("C:/Users/Misch/TestIntegrity","C:/Users/Misch/TestIntegrity/.index");
		checker.analyseFiles("C:/Users/Misch/TestIntegrity","C:/Users/Misch/TestIntegrity/.index");

	}
}
