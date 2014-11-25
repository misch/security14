package httpProxy;

import java.net.*;
import java.io.*;

public class ProxyThread extends Thread {
    private Socket socket = null;
    private static final int BUFFER_SIZE = 32768;
    
    public ProxyThread(Socket socket) {
        super();
        this.socket = socket;
    }

    public void run() {
    	try {
//            PrintWriter browserOut = new PrintWriter(socket.getOutputStream());
            BufferedWriter browserOut = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
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

            String host = "www.google.com";
            int port = 80;

            Socket httpServerSocket = new Socket(host,port);
            
            BufferedReader serverIn = new BufferedReader(new InputStreamReader(httpServerSocket.getInputStream()));
            PrintWriter serverOut = new PrintWriter(httpServerSocket.getOutputStream());
            
            serverOut.println(request);
            serverOut.flush();
            
            while ((inputLine = serverIn.readLine()) != null) {
            	System.out.println(inputLine);
//            	browserOut.println(inputLine);
//            	browserOut.flush();
            	browserOut.write(inputLine);
            	browserOut.flush();
            }
    	}catch(Exception e){
    		System.out.println("Exception");
    		e.printStackTrace();
    	}
    }
}