package integrityCheck;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class conatains methods to index and analyse file systems.
 * 
 * @author Michèle Wyss (GitHub: {@link github.com/misch})
 *
 */
public class IntegrityChecker {

	List<String> ignore;
	
	public IntegrityChecker(String ignorePath){
		this.ignore = readFile(ignorePath);
	}
	public HashMap<String,String> indexFiles(String pathToCheck, String pathToIndexFile, String ignorePath){
		HashMap<String,String> hashMap = new HashMap<String,String>();
		ChecksumCalculator checksumCalculator = new ChecksumCalculator("MD5");
		
		File folder = new File(pathToCheck);
		
		ArrayList<File> files = new ArrayList<File>();
		getFiles(folder,files, ignorePath);
		
		String checksums = "";
		for (File file : files){
			byte[] checksum = checksumCalculator.computeChecksum(file);
			hashMap.put(file.getAbsolutePath().trim(), ChecksumCalculator.checksumToString(checksum).trim());
			checksums += file.getAbsolutePath() + ": " + ChecksumCalculator.checksumToString(checksum)+"\n";
		}
		
		if (!pathToIndexFile.isEmpty()){
			writeIndexFile(checksums,pathToIndexFile);
		}
		return hashMap;
	}
	
	public void analyseFiles(String pathToCheck, String pathToIndexFile, String ignorePath){
		HashMap<String,String> old_files = readIndexFile(pathToIndexFile);
		HashMap<String,String> new_files = indexFiles(pathToCheck,"",ignorePath);
		
		if (old_files.equals(new_files)){
			System.out.println("Nothing changed!");
		}else{
			System.out.println("Something changed:");
			
			// Find deleted files
			for (String key : old_files.keySet()){
				if (!new_files.containsKey(key)){
					System.out.println("Deleted: " + key);
				}
			}
			
			// Find added files
			for (String key : new_files.keySet()){
				if (!old_files.containsKey(key)){
					System.out.println("Added: " + key);
				}
			}
			
			// Find modified files
			for (String key : new_files.keySet()){
				if (old_files.containsKey(key) && !new_files.get(key).equals(old_files.get(key))){
					System.out.println("Modified: " + key);
				}
			}
		}

	}
	
	
	private HashMap<String,String> readIndexFile(String path){
		HashMap<String, String> hashMap = new HashMap<String,String>();
		
		List<String> lines = readFile(path);

		for (String line : lines){
			String[] parts = line.split(": ");
			hashMap.put(parts[0].trim(), parts[1].trim());
		}
		
		return hashMap;
		
	}
	
	private List<String> readFile(String path){
		List<String> lines = new ArrayList<String>();
		
		File file = new File(path);
		try {
			lines = Files.readAllLines(file.toPath());
		} catch (IOException e) {}
			
		return lines;
	}
	
	private void writeIndexFile(String checksums, String path) {
		PrintWriter writer;
		try {
			writer = new PrintWriter(path);
			writer.print(checksums);
			writer.close();
		} catch (FileNotFoundException e) {
			System.out.println("Could not write index file.");
		}
	}
	
	private void getFiles(File folder, ArrayList<File> files, String ignorePath){
		for (File file : folder.listFiles()){
			if (!ignore.contains(file.getAbsolutePath()))
				if (file.isDirectory()){
					files.add(file);
					getFiles(file, files, ignorePath);
				}
				else{
					if (!file.getName().equals(".index")){
						files.add(file);
					}
				}
		}
	}
}
