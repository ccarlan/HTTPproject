import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

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

public class HttpProtocol {

	static final String HTML_START = "<html>"
			+ "<title>HTTP Server in java</title>" + "<body>";
	static final String HTML_END = "</body>" + "</html>";
	private static final int WAITING = 0;
	private static final int SENTSERVERREQUEST = 1;
	private int state = WAITING;

	private String theOutput = null;
	private OutputStream outToClient = null;

	public String processInput(String theInput, OutputStream outputStream)
			throws Exception {

		outToClient = outputStream;

		if (state == WAITING) {
			theOutput = "Waiting for client's request";
			// Why isn't it working???
			// outToClient.write("Waiting for client's request".getBytes());
			state = SENTSERVERREQUEST;
		} else {
			if (state == SENTSERVERREQUEST) {
				StringTokenizer tokenizer = new StringTokenizer(theInput);
				String httpMethod = tokenizer.nextToken();
				String httpQueryString = tokenizer.nextToken();
				StringBuffer htmlResponse = new StringBuffer();
				htmlResponse
						.append("<b> This is the HTTP Server Home Page.... </b><BR>");
				htmlResponse.append("The HTTP Client request is ....<BR>");
				if (httpMethod.equalsIgnoreCase("GET")) {
					if (httpQueryString.equals("/")) {
						htmlResponse.append(theInput + "<BR>");
						sendResponse(200, htmlResponse.toString(), false);
					} else {
						// This is interpreted as a file name.
						String fileName = httpQueryString.replaceFirst("/", "");
						fileName = URLDecoder.decode(fileName);
						if (new File(fileName).isFile()) {
							sendResponse(200, fileName, true);
						} else {
							// Wrong formulated resource.
							sendResponse(
									404,
									"<b>The Requested resource not found ...."
											+ "Usage: http://127.0.0.1:5000 or http://127.0.0.1:5000/</b>",
									false);
						}
					}
					theOutput = "Waiting for client's request";
//					outToClient
//							.write("Waiting for client's request".getBytes());
					state = SENTSERVERREQUEST;
				} else {
					theOutput = "You're supposed to write a GET http request! Try again.";
				}
			}
		}
		return theOutput;
	}

	public void sendResponse(int statusCode, String responseString,
			boolean isFile) throws Exception {

		String statusLine = null;
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		String dateDetails = "Date: " + dateFormat.format(date).toString()
				+ " ";
		String serverdetails = "Server: Java HTTPServer";
		String contentLengthLine = null;
		String fileName = null;
		String contentTypeLine = "Content-Type: text/html";
		FileInputStream fin = null;

		if (statusCode == 200) {
			statusLine = "HTTP/1.1 200 OK";
		} else {
			statusLine = "HTTP/1.1 404 Not Found";
		}
		if (isFile) {
			fileName = responseString;
			fin = new FileInputStream(fileName);
			contentLengthLine = "Content-Length: "
					+ Integer.toString(fin.available());
			if (!fileName.endsWith(".htm") && !fileName.endsWith(".html")) {
				contentTypeLine = "Content-Type: ";
			}
		} else {
			responseString = HTML_START + responseString + HTML_END;
			contentLengthLine = "Content-Length: " + responseString.length();
		}

//		theOutput = statusLine + " ";
//		theOutput = theOutput + dateDetails + " ";
//		theOutput = theOutput + serverdetails + " ";
//		theOutput = theOutput + contentTypeLine + " ";
//		theOutput = theOutput + contentLengthLine + " ";
//		theOutput = theOutput + responseString;
//		theOutput = theOutput + "Connection: close" + " ";

		outToClient.write(statusLine.getBytes());
		outToClient.write(dateDetails.getBytes());
		outToClient.write(serverdetails.getBytes());
		outToClient.write(contentTypeLine.getBytes());
		outToClient.write(contentLengthLine.getBytes());
		outToClient.write("Connection: close.".getBytes());

		if (isFile) {
			sendFile(fin, outToClient);
		} else {
			outToClient.write(responseString.getBytes());
		}

//		outToClient.close();

	}

	public void sendFile(FileInputStream fin, OutputStream out)
			throws Exception {
		byte[] buffer = new byte[1024];
		int bytesRead;

		while ((bytesRead = fin.read(buffer)) != -1) {
			out.write(buffer, 0, bytesRead);
		}
		fin.close();
	}
}