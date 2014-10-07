package integrityCheck;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ChecksumCalculator {

	MessageDigest md;

	public ChecksumCalculator(String alg) {
		try {
			this.md = MessageDigest.getInstance(alg);
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Algorithm " + alg + " not available.");
		}
	}

	public byte[] computeChecksum(File file) {
		byte[] digest = { 0 };

		if (file.isFile()) {
			try (InputStream is = Files.newInputStream(file.toPath())) {
				DigestInputStream dis = new DigestInputStream(is, md);

				int numRead;
				do {
					numRead = dis.read();
				} while (numRead != -1);
				
				dis.close();
				digest = md.digest();
			} catch (IOException e) {
				System.out.println("Couldn't compute checksum.");
			}
		}

		md.reset();

		return digest;
	}

	public static String checksumToString(byte[] checksum) {
		BigInteger bigInt = new BigInteger(1, checksum);
		return String.format("%0" + (checksum.length << 1) + "X", bigInt).toLowerCase();
	}
}
