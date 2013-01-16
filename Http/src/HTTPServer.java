/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class HTTPServer {
	 public static void main(String[] args) throws Exception {

	   		 
	        ServerSocket serverSocket = null;
	        try {
	            serverSocket = new ServerSocket (5000, 10, InetAddress.getByName("127.0.0.1"));
	        } catch (IOException e) {
	            System.err.println("Could not listen on port: 4444.");
	            System.exit(1);
	        }

	
	        Socket clientSocket = null;
	        try {
	            clientSocket = serverSocket.accept();
	        } catch (IOException e) {
	            System.err.println("Accept failed.");
	            System.exit(1);
	        }

	        PrintWriter outputToSever = new PrintWriter(clientSocket.getOutputStream(), true);
	        BufferedReader inputFromServer = new BufferedReader(
					new InputStreamReader(
					clientSocket.getInputStream()));
	        String inputLine, outputLine;
	        HttpProtocol protocol = new HttpProtocol();

	        outputLine = protocol.processInput(null, clientSocket.getOutputStream());
	        outputToSever.println(outputLine);

	        while ((inputLine = inputFromServer.readLine()) != null) {
	             outputLine = protocol.processInput(inputLine, clientSocket.getOutputStream());
	             outputToSever.println(outputLine);
	             if (outputLine.equals("Bye.")) {
	                break;
	             }
	        }
	        outputToSever.close();
	        inputFromServer.close();
	        clientSocket.close();
	        serverSocket.close();
	    }
}
