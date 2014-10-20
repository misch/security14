import java.math.BigInteger;


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
		return ("e:" + this.getExp().intValue() + "\tn:" + this.getMod().intValue());
	}
}