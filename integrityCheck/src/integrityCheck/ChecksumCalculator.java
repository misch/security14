package integrityCheck;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class ChecksumCalculator {
	
	MessageDigest md;
	
	public ChecksumCalculator(String alg){
		try {
			this.md = MessageDigest.getInstance(alg);
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Algorithm " + alg + " not available.");
		} 
	}
	
	public byte[] computeChecksum(File file){
		List<String> lines = new ArrayList<String>();
		String allLines = "";
		
		try {
			lines = Files.readAllLines(file.toPath());
		} catch (IOException e) {
			System.out.println("Could not read lines.");
		}

		for (String line : lines){
			allLines += line;
		}
		
		return computeChecksum(allLines);
	}
	
	public byte[] computeChecksum(String input){
		md.update(input.getBytes());
		return md.digest();
	}
	
	public static String checksumToString(byte[] checksum){
		BigInteger bigInt = new BigInteger(1,checksum);
		return String.format("%0" + (checksum.length << 1) + "X", bigInt).toLowerCase();
	}
}
