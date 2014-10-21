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
			System.out.println("Transmit public key to client (unencrypted)...");
			out.println(encryptor.getPublicKey().toString());
			out.flush();
			
			/* Read in client's public key */
			clientKey = RSAKey.readKey(in);
			System.out.println("Received client's public key.");
			
			while (true)
			{		
				if (in.hasNext())
				{
					/* read the client's input */
					String inputStr = in.nextLine();
					String decryptedStr = encryptor.decriptString(inputStr);

					/* send some stupid things to the client */
					String plaintext = "Hello, this is the server. I received your message ["+inputStr+"] and decrypted it to: [" + decryptedStr + "]";
					String ciphertext = encryptor.encryptString(plaintext, clientKey);
					out.println(ciphertext);
					out.flush();
				}
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}	
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