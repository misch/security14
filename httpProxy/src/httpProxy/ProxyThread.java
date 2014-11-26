package httpProxy;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ProxyThread extends Thread {
    private Socket socket = null;
    
    public ProxyThread(Socket socket) {
        super();
        this.socket = socket;
    }

    public void run() {
    	try {
    		
            DataOutputStream browserOut = new DataOutputStream(socket.getOutputStream());
            BufferedReader browserIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String inputLine;
            String urlToCall = "";
            String request = "";
            
            String host = "www.google.com"; // hardcoded for testing
            int port = 80;

            Socket httpServerSocket = new Socket(host,port);
            
            InputStream serverIn = httpServerSocket.getInputStream();
            PrintWriter serverOut = new PrintWriter(httpServerSocket.getOutputStream());
            
            while ((inputLine = browserIn.readLine()) != null) {
            	if (inputLine.isEmpty()){
            		break;
            	}
            	
            	serverOut.println(inputLine + "\r\n");
            	
            	/* Get url out of the request */
            	if(inputLine.split(" ")[0].equals("GET")){
            		urlToCall = inputLine.split(" ")[1];
            	}
            }

            serverOut.flush();
            
//            boolean blocked = URLisBlocked(urlToCall);
            boolean blocked = true;
            if (blocked){
            	sendURLBlockedMessage(browserOut);
            }else{
            	final int BUFFER_SIZE = 32768;
                byte by[] = new byte[ BUFFER_SIZE ];
                int index = serverIn.read( by, 0, BUFFER_SIZE );
                while ( index != -1 )
                {
                  browserOut.write( by, 0, index );
                  index = serverIn.read( by, 0, BUFFER_SIZE );
                }
                browserOut.flush();
                browserOut.close();
            }
    	}catch(Exception e){
    		System.out.println("Exception");
    		e.printStackTrace();
    	}
    }
    
    private void sendURLBlockedMessage(DataOutputStream browserOut){
    	try {
			browserOut.write("HTTP/1.1 403 Forbidden\r\n".getBytes());
	    	browserOut.write("Content-Type: text/plain; charset=UTF-8\r\n".getBytes());
	    	browserOut.write("\r\n".getBytes());
	    	browserOut.write("This content is sooo blocked by the proxy...\r\n".getBytes());
	    	browserOut.flush();
	    	browserOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

    }
    
    private boolean URLisBlocked(String url){
    	List<String> blacklist = readFile("blacklist.txt");
    	// TODO: Do more matching-stuff here instead of simple comparison
    	for (String badURL : blacklist) {
    			if(url.equals(badURL)){
    				return true;
    			}
    	}
    	return false;
    }
    
	private List<String> readFile(String filename) {
		String path = System.getProperty("user.dir");
		path += "\\" + filename;
		List<String> lines = new ArrayList<String>();

		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(path));
			String line;
			while ((line = br.readLine()) != null) {
				lines.add(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lines;
	}
}