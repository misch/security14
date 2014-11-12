import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SMTPGateway implements Runnable {

	private Socket socket;
	private final static int PORT = 25;
	private String from;
	private String to;

	public SMTPGateway(Socket s) {
		socket = s;
	}

	@Override
	public void run() {
		try {
			PrintWriter out = new PrintWriter(socket.getOutputStream()); // to
																			// send
																			// stuff
																			// to
																			// the
																			// client
			Scanner in = new Scanner(socket.getInputStream()); // input from
																// client

			/* Confirm that the server is ready. */
			out.println("220");
			out.flush();

			System.out.println("Receiving data...");

			/* Acknowledge client's HELO */
			if (in.hasNext()) {
				String nextLine = in.nextLine();
				// System.out.println("Client: " + nextLine);
				if (nextLine.contains("HELO") || nextLine.contains("EHLO"))
					out.println("250");
				else
					out.println("You didn't say HELO!");
				out.flush();
			}

			/*
			 * Read and store the "MAIL FROM:" and also confirm this to the
			 * client.
			 */
			if (in.hasNext()) {
				String nextLine = in.nextLine();
				// System.out.println("Client: " + nextLine);
				if (nextLine.contains("MAIL FROM:")
						|| nextLine.contains("EHLO")) {
					this.from = nextLine.split(": ")[1];
					this.from = this.from.replace("<", "");
					this.from = this.from.replace(">", "");
					// System.out.println(this.from);
					out.println("250");
				} else
					out.println("You didn't say MAIL FROM:");
				out.flush();
			}

			/*
			 * Read and store the "RCPT TO:" and also confirm this to the
			 * client.
			 */
			if (in.hasNext()) {
				String nextLine = in.nextLine();
				// System.out.println("Client: " + nextLine);
				if (nextLine.contains("RCPT TO:") || nextLine.contains("EHLO")) {
					this.to = nextLine.split(": ")[1];
					this.to = this.to.replace("<", "");
					this.to = this.to.replace(">", "");
					// System.out.println(this.to);
					out.println("250");
				} else
					out.println("You didn't say RCPT TO:");
				out.flush();
			}

			/* Confirm to the client that it's allowed to send the message now. */
			if (in.hasNext()) {
				String nextLine = in.nextLine();
				// System.out.println("Client: " + nextLine);
				if (nextLine.contains("DATA")) {
					out.println("354");
				} else
					out.println("You didn't say DATA");
				out.flush();
			}

			String message = "";
			String subject = "";
			boolean isHeader = true;
			while (true) {
				if (in.hasNext()) {
					/* read the client's input */
					String nextLine = in.nextLine();
					if (nextLine.equals(".")) {
						break;
					} else {
						if (nextLine.contains("Subject: ")) {
							subject = nextLine.split(": ")[1];
						}
						if (nextLine.isEmpty())
							isHeader = false;

						if (!isHeader) {
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

			antiVirusScan(message);

			/* Send the message through the fake server */
			SMTPServerConnector mailer = new SMTPServerConnector();
			mailer.send(from, to, subject, message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {
		try {

			ServerSocket serverSocket = new ServerSocket(PORT);
			System.out.println("Gateway running...");

			while (true) {
				/* establish connection to client */
				Socket s = serverSocket.accept();

				/* create a new server and start thread */
				SMTPGateway chatServer = new SMTPGateway(s);

				Thread t = new Thread(chatServer);
				t.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String filter(String text) {
		List<String> unauthorized = readFile(".unauthorized_keywords");
		String filtered = text;
		for (String badWord : unauthorized) {
			filtered = filtered.replace(badWord, "*beeep*");
		}
		return filtered;
	}

	private void antiVirusScan(String text) {
		String path = System.getProperty("user.dir") + "\\tmpAV.txt";
		PrintWriter writer;
		try {
			writer = new PrintWriter(path);
			writer.print(text);
			writer.close();
		} catch (FileNotFoundException e) {
			System.out
					.println("Could not write temp file to scan for viruses.");
		}

		/* Call Avast Antivirus and scan file */
//		String cmd = "\"C:\\Program Files\\AVAST Software\\Avast\\ashCmd.exe\" C:\\Users\\Misch\\security14\\serie-04\\SMTPGateway\\test.txt /_> C:\\Users\\Misch\\security14\\serie-04\\SMTPGateway\\results.txt";
//		try {
//			Process process = Runtime.getRuntime().exec("cmd /c " + cmd);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		List<String> AVresults = readFile("test2.txt");
		for(String AVresult : AVresults){
			System.out.println(AVresult);
		}


	}

	private List<String> readFile(String filename) {
		String path = System.getProperty("user.dir");
		path += "\\"+filename;
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