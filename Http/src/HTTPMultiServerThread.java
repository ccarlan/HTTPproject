
import java.net.*;
import java.io.*;

public class HTTPMultiServerThread extends Thread {
	private Socket clientSocket = null;

	public HTTPMultiServerThread(Socket socket) {
		super("HTTPMultiServerThread");
		this.clientSocket = socket;
	}

	public void run() {
		PrintWriter outputToServer;
		BufferedReader inputFromServer;
		try {
			outputToServer = new PrintWriter(clientSocket.getOutputStream(), true);
			inputFromServer = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));

			String inputLine, outputLine;
			HttpProtocol protocol = new HttpProtocol();
			
			 try {
				outputLine = protocol.processInput(null, clientSocket.getOutputStream());
				outputToServer.println(outputLine);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		     
		        
			while ((inputLine = inputFromServer.readLine()) != null) {
				try {
					outputLine = protocol.processInput(inputLine, clientSocket.getOutputStream());
					outputToServer.println(outputLine);
				if (outputLine.equals("Bye")){
					break;}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			outputToServer.close();
			inputFromServer.close();
			clientSocket.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
