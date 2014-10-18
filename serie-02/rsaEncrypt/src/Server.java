import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;


public class Server implements Runnable{

	private Socket socket;
	private final static int PORT = 6677;
	
	public Server(Socket s)
	{
		socket = s;
	}
	
	@Override
	public void run()
	{
		try
		{
			Scanner in = new Scanner(socket.getInputStream()); // input from client
			PrintWriter out = new PrintWriter(socket.getOutputStream()); // to send stuff to the client
			
			while (true)
			{		
				if (in.hasNext())
				{
					/* read the client's input */
					String input = in.nextLine(); 
					System.out.println("Client Said: " + input);
					
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
                Server chatServer = new Server(s);
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