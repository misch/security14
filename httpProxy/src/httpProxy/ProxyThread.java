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

			DataOutputStream browserOut = new DataOutputStream(
					socket.getOutputStream());
			BufferedReader browserIn = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));

			String GETLine = browserIn.readLine();
			while (GETLine != null && !GETLine.contains("GET")) {
				GETLine = browserIn.readLine();
			}
			String url = GETLine.split(" ")[1];
			String host = getHost(url);

			if (hostIsBlocked(host)) {
				sendURLBlockedMessage(browserOut);
				return;
			}

			Socket httpServerSocket = new Socket(host, 80);

			InputStream serverIn = httpServerSocket.getInputStream();
			PrintWriter serverOut = new PrintWriter(httpServerSocket.getOutputStream());

			serverOut.println(GETLine + "\r\n");

			String inputLine;
			while ((inputLine = browserIn.readLine()) != null) {
				if (inputLine.isEmpty()) {
					break;
				}
				serverOut.println(inputLine + "\r\n");
			}
			serverOut.flush();

			final int BUFFER_SIZE = 32768;
			byte by[] = new byte[BUFFER_SIZE];
			int index = serverIn.read(by, 0, BUFFER_SIZE);
			while (index != -1) {
				browserOut.write(by, 0, index);
				index = serverIn.read(by, 0, BUFFER_SIZE);
			}
			browserOut.flush();
			browserOut.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getHost(String urlToCall) {
		String host = urlToCall.split("/")[2];
		return host;
	}

	private void sendURLBlockedMessage(DataOutputStream browserOut) {
		try {
			browserOut.write("HTTP/1.1 403 Forbidden\r\n".getBytes());
			browserOut.write("Content-Type: text/plain; charset=UTF-8\r\n"
					.getBytes());
			browserOut.write("\r\n".getBytes());
			browserOut.write("This content is sooo blocked by the proxy...\r\n"
					.getBytes());
			browserOut.flush();
			browserOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean hostIsBlocked(String host) {
		List<String> blacklist = readFile("blacklist.txt");

		for (String badURL : blacklist) {
			if (host.matches(badURL)) {
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