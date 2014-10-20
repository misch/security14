import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;


public class RSAServer implements Runnable{

	private Socket socket;
	private final static int PORT = 6677;
	RSAEncryptor encryptor;
	RSAKey clientKey;
	
	public RSAServer(Socket s)
	{
		socket = s;
		encryptor = new RSAEncryptor();
	}
	
	@Override
	public void run()
	{
		try
		{
			
			PrintWriter out = new PrintWriter(socket.getOutputStream()); // to send stuff to the client
			Scanner in = new Scanner(socket.getInputStream()); // input from client
			
			/* Transmit own public key */
			out.println(encryptor.getPublicKey().toString());
			out.flush();
			
			/* Read in client public key */
			clientKey = getClientKey(in);
			
			while (true)
			{		
				if (in.hasNext())
				{
					/* read the client's input */
					int input = in.nextInt(); 
					
					System.out.println("Client Said: " + input);
					BigInteger decrypted = encryptor.decrypt(BigInteger.valueOf(input));
					System.out.println("Decrypted: " + decrypted.intValue());
					
					/* send something back to client */
					out.println("You Said: " + input);
					out.flush();
				}
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}	
	}
	
	private RSAKey getClientKey(Scanner in){
		String serverPublicKey = in.nextLine();
		
		String[] keySplitted = serverPublicKey.split("\t");
		String exponent = keySplitted[0].split(":")[1];
		String modulus = keySplitted[1].split(":")[1];
		
		RSAKey clientKey = new RSAKey(new BigInteger(modulus) ,new BigInteger(exponent));
		
		return clientKey;
	}
	
    public static void main(String[] args) throws IOException {
        try
        {
            
            ServerSocket serverSocket = new ServerSocket(PORT); 
            System.out.println("Server running. Waiting for client to connect...");
         
            while (true)
            {                   
            	/* establish connection to client */
                Socket s = serverSocket.accept();
                 
                System.out.println("Client connected from " + s.getLocalAddress().getHostName());
                
                /* create a new server and start thread */
                RSAServer chatServer = new RSAServer(s);
                
                Thread t = new Thread(chatServer);
        		t.start();
            }
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        }
    }

}