import java.awt.List;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.net.Socket;
import java.util.ArrayList;
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

			/* Transmit own public key */
			System.out.println("Transmit public key to server (unencrypted)...");
			PrintWriter out = new PrintWriter(socket.getOutputStream()); // to send stuff to the server
			out.println(encryptor.getPublicKey().toString());
			out.flush();
			
			/* Read in server public key */
			System.out.println("Received server's public key.");
			serverKey = RSAKey.readKey(in);

			while (true)
			{						
				String userInput = chat.nextLine();
				String ciphertext = encryptor.encryptString(userInput, serverKey);
				out.println(ciphertext);
				out.flush();
				
				if(in.hasNext()){
					/* read the server's input */
					String inputStr = in.nextLine();
					
					String decryptedStr = encryptor.decriptString(inputStr);
					System.out.println(decryptedStr);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		} 
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