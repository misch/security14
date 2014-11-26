package httpProxy;

import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

public class ProxyThread extends Thread {
    private Socket socket = null;
    
    public ProxyThread(Socket socket) {
        super();
        this.socket = socket;
    }

    public void run() {
    	try {
            PrintWriter browserOut = new PrintWriter(socket.getOutputStream());
            BufferedReader browserIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String inputLine;
            String urlToCall = "";
            String request = "";
            
            
            while ((inputLine = browserIn.readLine()) != null) {
            	if (inputLine.isEmpty()){
            		break;
            	}
            	
            	request += inputLine + "\r\n";
            	System.out.println(inputLine);
            	
            	/* Get url out of the request */
            	if(inputLine.split(" ")[0].equals("GET")){
            		System.out.println("inside 'if': " + inputLine);
            		urlToCall = inputLine.split(" ")[1];
            		
            	}
            }

            String host = "www.google.com"; // hardcoded for testing
            int port = 80;

            Socket httpServerSocket = new Socket(host,port);
            
            BufferedReader serverIn = new BufferedReader(new InputStreamReader(httpServerSocket.getInputStream()));
            PrintWriter serverOut = new PrintWriter(httpServerSocket.getOutputStream());
            
            serverOut.println(request);
            serverOut.flush();
            
            if (URLisBlocked(urlToCall)){
            	sendURLBlockedMessage(browserOut);
            }else{
            	while ((inputLine = serverIn.readLine()) != null) {
            		System.out.println(inputLine);
            		browserOut.println(inputLine);
            		browserOut.flush();
            	}
            }
    	}catch(Exception e){
    		System.out.println("Exception");
    		e.printStackTrace();
    	}
    }
    
    private void sendURLBlockedMessage(PrintWriter out){
    	out.println("HTTP/1.1 403 Forbidden");
    	out.println("Content-Type: text/plain; charset=UTF-8");
    	out.println("");
    	out.println("This content is sooo blocked by the proxy...");
    	out.flush();
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