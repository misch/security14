import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client implements Runnable {

	private Socket socket;
	private final static int PORT = 6677;
	private final static String HOST = "localhost";
	
	public Client(Socket s)
	{
		socket = s;
	}
	
	@Override
	public void run()
	{
		try
		{
			Scanner chat = new Scanner(System.in);
			Scanner in = new Scanner(socket.getInputStream()); // input from server
			PrintWriter out = new PrintWriter(socket.getOutputStream()); // to send stuff to the server
			
			while (true)
			{						
				String input = chat.nextLine();
				
				/* send typed text to the server */
				out.println(input);
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
	
	public static void main(String[] args) throws IOException
	{
		try 
		{
			/* connect to server */
			Socket s = new Socket(HOST, PORT);
			
			System.out.println("Successfully connected to " + HOST);
			
			Client client = new Client(s);
			
			Thread t = new Thread(client);
			t.start();
			
		} 
		catch (Exception noServer)
		{
			System.out.println("Cannot connect to server.");
		}
	}
	

}