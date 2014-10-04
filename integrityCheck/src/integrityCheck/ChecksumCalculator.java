package integrityCheck;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;

public class ChecksumCalculator {
	
	MessageDigest md;
	
	public ChecksumCalculator(String alg){
		try {
			this.md = MessageDigest.getInstance(alg);
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Algorithm " + alg + " not available.");
		} 
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
