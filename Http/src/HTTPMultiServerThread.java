
import java.net.*;
import java.io.*;

public class HTTPMultiServerThread extends Thread {
	private Socket socket = null;

	public HTTPMultiServerThread(Socket socket) {
		super("HTTPMultiServerThread");
		this.socket = socket;
	}

	public void run() {

		try {
			PrintWriter outputToServer = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader inputFromServer = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));

			String inputLine, outputLine;
			HttpProtocol protocol = new HttpProtocol();
			try {
				outputLine = protocol.processInput(null, socket.getOutputStream());
//				outputToServer.println(outputLine);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

			while ((inputLine = inputFromServer.readLine()) != null) {
				try {
					outputLine = protocol.processInput(inputLine, socket.getOutputStream());
//					outputToServer.println(outputLine);
				if (outputLine.equals("Bye")){
					break;}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			outputToServer.close();
			inputFromServer.close();
			socket.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
