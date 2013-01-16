import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * This class implements and HTTPClient.
 * 
 * @author carmen
 */
public class HTTPClient {
	
	public static void main(String[] args) throws IOException {

        // The client's socket.
		Socket clientSocket = null;
		// The data the client sends to the server.
		PrintWriter outputToServer = null;
		// The data the client receives from the server.
		BufferedReader inputFromServer = null;

		try {
			InetAddress address = InetAddress.getByName("127.0.0.1");
			// Connecting the client to a server with a certain address and the
			// port 5000.
			clientSocket = new Socket(address, 5000);
			outputToServer = new PrintWriter(clientSocket.getOutputStream(),
					true);
			inputFromServer = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));
		} catch (UnknownHostException e) {
			System.err.println("Don't know about this host.");
			System.exit(1);
		} catch (IOException e) {
			System.err
					.println("Couldn't get I/O for the connection to the server.");
			System.exit(1);
		}

		BufferedReader stdIn = new BufferedReader(new InputStreamReader(
				System.in));
		
		String fromServer;
		String fromUser;

		while ((fromServer = inputFromServer.readLine()) != null) {
			System.out.print("Server: " + fromServer);
			if (fromServer.equals("Bye.")) {
				break;
			}

			fromUser = stdIn.readLine();
			if (fromUser != null) {
				System.out.println("Client: " + fromUser);
				outputToServer.println(fromUser);
			}
		}

		outputToServer.close();
		inputFromServer.close();
		stdIn.close();
		clientSocket.close();
	}
}
