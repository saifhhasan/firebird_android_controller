package project.client.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPClient {
	String ip;	//ip address of server
	int port;	//port number of server process
	byte[] buffer;
	
	Socket socket;
	DataOutputStream out;
	DataInputStream in;
	
	/*
	 * Establish TCP Connection to server process
	 */
	public TCPClient(String ip, int port) throws UnknownHostException, IOException {
		this.ip = ip;
		this.port = port;
		buffer = new byte[512];
		socket = new Socket(this.ip, port);
		out = new DataOutputStream(socket.getOutputStream());
		in = new DataInputStream(socket.getInputStream());
	}
	
	public void send(int msg) {
		try {
			out.writeInt(msg);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("TCPSender: Unable to send command : " + msg);
		}
	}
	
	public void receive(byte[] buf) {
		//For now not needed as such
	}
}
