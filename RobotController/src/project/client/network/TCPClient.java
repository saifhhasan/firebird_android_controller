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
	
	/*
	 * Sends msg to remote connected Server
	 */
	public boolean send(int msg) {
		try {
			out.writeInt(msg);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("TCPSender: Unable to send command : " + msg);
			return false;
		}
	}
	
	/*
	 * Not implemented yet cause it was not required
	 */
	public int receive() {
		return 0;
	}
	
	/*
	 * Closes existing connection and releases socket
	 */
	public void close() {
		if(socket != null) {
			try {
				socket.close();
				System.out.println("TCPClient: TCP Socket closed");
			} catch (IOException e) {
				System.out.println("TCPClient: Error while closing tcp socket");
				e.printStackTrace();
			}
			socket = null;
			in = null;
			out = null;
		}
	}
}
