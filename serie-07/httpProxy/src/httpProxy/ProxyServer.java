package httpProxy;

import java.net.*;
import java.io.*;

public class ProxyServer {
	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket = null;

	    int port = 8080;

	    try {
	    	serverSocket = new ServerSocket(port);
	        System.out.println("Proxy running. Waiting for browser to connect on port " + port);
	    } catch (IOException e) {
	    	System.err.println("Could not listen on port: " + args[0]);
	    	System.exit(-1);
	    }

	    while (true) {
	    	
	      	/* establish connection */
            Socket s = serverSocket.accept();
             
            System.out.println("Client connected from " + s.getLocalAddress().getHostName());
	    	
	    	Thread t = new ProxyThread(s);
	    	t.start();
	    }
	}
}