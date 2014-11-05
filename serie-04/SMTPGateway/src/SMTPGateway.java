import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class SMTPGateway implements Runnable{

	private Socket socket;
	private final static int PORT = 25;
	private String from;
	private String to;
	
	public SMTPGateway(Socket s)
	{
		socket = s;
	}
	
	@Override
	public void run()
	{
		try
		{
			PrintWriter out = new PrintWriter(socket.getOutputStream()); // to send stuff to the client
			Scanner in = new Scanner(socket.getInputStream()); // input from client
			
			/* Confirm that the server is ready. */ 
			out.println("220");
			out.flush();
			
			System.out.println("Receiving data...");
			
			/* Acknowledge client's HELO */
			if (in.hasNext()){
				String nextLine = in.nextLine();
//				System.out.println("Client: " + nextLine);
				if (nextLine.contains("HELO") || nextLine.contains("EHLO"))
					out.println("250");
				else
					out.println("You didn't say HELO!");
				out.flush();
			}
			
			/* Read and store the "MAIL FROM:" and also confirm this to the client. */
			if (in.hasNext()){
				String nextLine = in.nextLine();
//				System.out.println("Client: " + nextLine);
				if (nextLine.contains("MAIL FROM:") || nextLine.contains("EHLO")){
					this.from = nextLine.split(": ")[1];
					this.from = this.from.replace("<", "");
					this.from = this.from.replace(">", "");
//					System.out.println(this.from);
					out.println("250");
				}
				else
					out.println("You didn't say MAIL FROM:");
				out.flush();
			}
			
			/* Read and store the "RCPT TO:" and also confirm this to the client. */
			if (in.hasNext()){
				String nextLine = in.nextLine();
//				System.out.println("Client: " + nextLine);
				if (nextLine.contains("RCPT TO:") || nextLine.contains("EHLO")){
					this.to = nextLine.split(": ")[1];
					this.to = this.to.replace("<", "");
					this.to = this.to.replace(">", "");
//					System.out.println(this.to);
					out.println("250");
				}
				else
					out.println("You didn't say RCPT TO:");
				out.flush();
			}
			
			/* Confirm to the client that it's allowed to send the message now. */
			if (in.hasNext()){
				String nextLine = in.nextLine();
//				System.out.println("Client: " + nextLine);
				if (nextLine.contains("DATA")){
					out.println("354");
				}
				else
					out.println("You didn't say DATA");
				out.flush();
			}
			
			String message = "";
			String subject = "";
			boolean isHeader = true;
			while (true)
			{		
				if (in.hasNext())
				{
					/* read the client's input */
					String nextLine = in.nextLine();
					if (nextLine.equals(".")){
						break;
					}else{
						if(nextLine.contains("Subject: ")){
							subject = nextLine.split(": ")[1];
						}
						if (nextLine.isEmpty())
							isHeader = false;
						
						if (!isHeader){
							message += nextLine + "\n";
						}
					}
				}
			}
			
			/* Confirm to the client that the message was submitted. */
			out.println("250");
			out.flush();
			
			System.out.println("Transmission finished. Filter and forward...");
			subject = filter(subject);
			message = filter(message);
			
			/* Send the message through the fake server */
			//TODO: Filter-stuff before!
			SMTPServerConnector mailer = new SMTPServerConnector();
			mailer.send(from, to, subject, message);
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
            System.out.println("Gateway running...");
         
            while (true)
            {                   
            	/* establish connection to client */
                Socket s = serverSocket.accept();
                 
                /* create a new server and start thread */
                SMTPGateway chatServer = new SMTPGateway(s);
                
                Thread t = new Thread(chatServer);
        		t.start();
            }
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        }
    }

    private String filter(String text){
    	List<String> unauthorized = readFile();
    	String filtered = text;
    	for (String badWord : unauthorized){
    		filtered = filtered.replace(badWord,"*beeep*");
    	}
    	return filtered;
    }
    
	private List<String> readFile(){
		String path = System.getProperty("user.dir");
		path += "\\.unauthorized_keywords";
		List<String> lines = new ArrayList<String>();
		
		File file = new File(path);
		try {
			lines = Files.readAllLines(file.toPath());
		} catch (IOException e) {}
			
		return lines;
	}
}