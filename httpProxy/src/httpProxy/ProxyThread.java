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

            String host = "";
            int port = 80;

            Socket httpServerSocket = null; 
            
            InputStream serverIn = null;
            PrintWriter serverOut = null;
            
            String inputLine;
            while ((inputLine = browserIn.readLine()) != null) {
            	if (inputLine.isEmpty()){
            		break;
            	}
            	
            	/* Get url out of the request */
            	if(inputLine.split(" ")[0].equals("GET")){
            		String url = inputLine.split(" ")[1];
            		host = getHost(url);
            		httpServerSocket = new Socket(host,port);
            		serverOut = new PrintWriter(httpServerSocket.getOutputStream());
            	}
            	serverOut.println(inputLine + "\r\n");
            }
            
            serverOut.flush();
            
            serverIn = httpServerSocket.getInputStream();
            boolean blocked = hostIsBlocked(host);
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
    		e.printStackTrace();
    	}
    }
    
    private String getHost(String urlToCall) {
		String host = urlToCall.split("/")[2];
		return host;
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
    
    private boolean hostIsBlocked(String host){
    	List<String> blacklist = readFile("blacklist.txt");
    	
    	for (String badURL : blacklist) {
    			if(host.matches(badURL)){
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
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lines;
	}
}