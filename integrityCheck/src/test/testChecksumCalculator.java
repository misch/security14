package test;

import static org.junit.Assert.*;
import integrityCheck.ChecksumCalculator;

import org.junit.Test;

public class testChecksumCalculator {
	ChecksumCalculator calculator = new ChecksumCalculator("MD5");
	
	@Test
	public void testCalculator() {
		byte[] checksum = calculator.computeChecksum("abc");
		assertEquals(ChecksumCalculator.checksumToString(checksum),"900150983cd24fb0d6963f7d28e17f72");
	}

}
