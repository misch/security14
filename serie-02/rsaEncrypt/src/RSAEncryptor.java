import java.math.BigInteger;
import java.util.Random;

public class RSAEncryptor {

	private BigInteger p, q;
	private BigInteger n;
	private BigInteger phi;
	 
	private BigInteger e, d;
	
	public RSAEncryptor() {
		generateKeyPair();
	}

	public static void main(String args[]) {
		RSAEncryptor enc = new RSAEncryptor();

		char test = 'h' ;
		BigInteger plaintext = BigInteger.valueOf(test);
		
		BigInteger encrypted = enc.encrypt(plaintext);
		

		System.out.println("Plaintext: " + plaintext.toString());
		System.out.println("Ciphertext: " + encrypted.toString());
		
		BigInteger decrypted = enc.decrypt(encrypted);
		System.out.println("Decrypted: " + decrypted.toString());
	}

	private void generateKeyPair() {
		BigInteger[] primes = getPrimes();

		this.p = primes[0];
		this.q = primes[1];
		
		this.n = p.multiply(q);
		
		this.phi = (p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE)));

		this.e = BigInteger.valueOf(3); // maybe not too secure to start with 3?
		while (!coprime(e,phi)) {
			e = e.add(BigInteger.valueOf(2));
		}
		
		this.d = e.modInverse(phi);
	}
	
	
	public BigInteger encrypt(BigInteger text, RSAKey publicKey){
		return text.modPow(publicKey.getExp(),publicKey.getMod());
	}
	
	public BigInteger encrypt(BigInteger text){
		return encrypt(text, this.getPublicKey());
	}
	 
	/**
	 * This method decrypts a given ciphertext using the private key of the RSA encryptor.
	 * 
	 * @param ciphertext BigInteger
	 * @return decrypted ciphertext
	 */
	public BigInteger decrypt(BigInteger ciphertext)
	{
		return ciphertext.modPow(d, n);
	}

	/**
	 * This method returns two different prime numbers
	 * 
	 * @return An BigInteger-array of length 2, containing two different prime numbers.
	 */
	private BigInteger[] getPrimes() {
		int length = 12;
		BigInteger p = BigInteger.probablePrime(length, new Random());

		BigInteger q;
		do {
			q = BigInteger.probablePrime(length, new Random());
		} while (p == q);

		BigInteger[] primes = { p, q };
		return primes;
	}

	/**
	 * Check whether two numbers are coprime or not.
	 * 
	 * @param x BigInteger
	 * @param y BigInteger
	 * @return boolean: true if x and y are coprime, false otherwise
	 * */
	private boolean coprime(BigInteger x, BigInteger y) {
		return x.gcd(y).equals(BigInteger.ONE);
	}
	
	public RSAKey getPublicKey(){
		return new RSAKey(n,e);
	}
}
