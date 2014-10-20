import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Scanner;

public class RSAClient implements Runnable {

	private Socket socket;
	private final static int PORT = 6677;
	private final static String HOST = "localhost";
	private RSAEncryptor encryptor;
	private RSAKey serverKey;
	
	public RSAClient(Socket s)
	{
		socket = s;
		encryptor = new RSAEncryptor();
	}
	
	@Override
	public void run()
	{
		try
		{
			Scanner chat = new Scanner(System.in);
			Scanner in = new Scanner(socket.getInputStream()); // input from server

			/* Read in server public key */
			serverKey = getServerKey(in);

			/* Transmit own public key */
			PrintWriter out = new PrintWriter(socket.getOutputStream()); // to send stuff to the server
			out.println(encryptor.getPublicKey().toString());
			out.flush();
			
			while (true)
			{						
				int input = chat.nextInt();
				
				BigInteger ciphertext = encryptor.encrypt(BigInteger.valueOf(input), this.serverKey);
				/* send typed text to the server */
				out.println(ciphertext.toString());
				out.flush();
				
				/* print whatever the server sent */
				if(in.hasNext())
					System.out.println(in.nextLine());
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		} 
	}
	
	private RSAKey getServerKey(Scanner in){
		String serverPublicKey = in.nextLine();
		
		String[] keySplitted = serverPublicKey.split("\t");
		String exponent = keySplitted[0].split(":")[1];
		String modulus = keySplitted[1].split(":")[1];
		
		RSAKey serverKey = new RSAKey(new BigInteger(modulus) ,new BigInteger(exponent));
		return serverKey;
	}
	public static void main(String[] args) throws IOException
	{
		try 
		{
			/* connect to server */
			Socket s = new Socket(HOST, PORT);
			System.out.println("Successfully connected to " + HOST);
			
			RSAClient client = new RSAClient(s);
			
			Thread t = new Thread(client);
			t.start();
		} 
		catch (Exception noServer)
		{
			System.out.println("Cannot connect to server.");
		}
	}
	

}