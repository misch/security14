import java.math.BigInteger;
import java.util.Scanner;


public class RSAKey {
	private BigInteger modulus;
	private BigInteger exponent;

	public RSAKey(BigInteger modulus, BigInteger exponent) {
		this.modulus = modulus;
		this.exponent = exponent;
	}
	
	public BigInteger getExp(){
		return this.exponent;
	}
	
	public BigInteger getMod(){
		return this.modulus;
	}
	
	public String toString(){
		// Note: Limited values for long are problematic... Instead, one could represent this as byte array!
		return ("e:" + this.getExp().longValue() + "\tn:" + this.getMod().longValue());
	}
	
	public static RSAKey readKey(Scanner in){
		String serverPublicKey = in.nextLine();
		
		String[] keySplitted = serverPublicKey.split("\t");
		String exponent = keySplitted[0].split(":")[1];
		String modulus = keySplitted[1].split(":")[1];
		
		RSAKey serverKey = new RSAKey(new BigInteger(modulus) ,new BigInteger(exponent));
		return serverKey;
	}
}