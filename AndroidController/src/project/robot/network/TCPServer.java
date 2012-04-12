package project.robot.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import project.robot.BluetoothConnection;
import android.os.AsyncTask;

public class TCPServer extends AsyncTask<Void, Integer, Void> {
	int port;				//Port on which server is running
	String clientIP;		//IP address of remote client
	ServerSocket serverSocket;	//Server Socket
	Socket clientSocket;	//Socket connected to client
	DataOutputStream out;	//Output stream object to send data
	DataInputStream in;			//Input Stream object to receive data
	
	boolean flag;
	boolean videoFlag;	//Used to toggle video
	private VideoThread vthread;	//Video Thread Object
	
	public static BluetoothConnection conn;
	
	/*
	 * Starts a TCP Server which listens to incoming connections
	 */
	public TCPServer(int port) {
		this.port = port;
		videoFlag = false;
		vthread = null;
	}
	
	@Override
	protected Void doInBackground(Void... arg0) {
		int msg;	
		
		//Initiating server socket
		try {
			serverSocket = new ServerSocket(port);
			flag = true;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.println("Tcpserver: unable to bind socket");
			e1.printStackTrace();
			flag = false;
		}
		
		System.out.println("TCPServer: Server started");
		
		while(flag) {
			try {
				//Accepting incoming connection
				clientSocket = serverSocket.accept();
				clientIP = clientSocket.getInetAddress().getHostAddress();
				System.out.println("TCPServer: Connected to client at " + clientIP);
				
				//Getting input and output streams
				in = new DataInputStream(clientSocket.getInputStream());
				out = new DataOutputStream(clientSocket.getOutputStream());
				msg = 1;
				
				//Start VideoThread
				try {
					vthread = new VideoThread(clientIP);
					vthread.startVideo();
					System.out.println("TCPServer: Video started");
					videoFlag = true;
				} catch (Exception e) {
					e.printStackTrace();
					vthread = null;
					System.out.println("TCPServer: Error while starting video");
				}
				
				//Reading input commands and signaling processing function
				while(flag && msg != 0) {
					msg = in.readInt();
					publishProgress(msg);
				}
				
				//Stopping video streaming if running
				if(vthread != null) {
					vthread.stopVideo();
					vthread = null;
				}
				System.out.println("TCPServer: Video stopped");
				
				//Closing Connection to client
				clientSocket.close();
				clientSocket = null;
				System.out.println("TCPServer: Closed connection to host at " + clientIP);
				clientIP = "null";
				
			} catch (IOException e) {
				if(clientSocket != null) {
					clientSocket = null;
				}
				flag = false;
				System.out.println("TCPServer: Error while accepting or closing client connection");
			}
		}
		
		//Closing serverSocket
		if(serverSocket != null) {
			try {
				serverSocket.close();
				System.out.println("TCPServer: Server Socket Closed");
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("TCPServer: Error while closing socket");
			}
			serverSocket = null;
		}
		
		System.out.println("TCPServer: Server stopped");
		return null;
	}
	
	private void sendSignal(int signal) {
		if(conn != null)
			conn.send(signal);
		else
			System.out.println("TCPServer: null conn, can't send value");
	}
	
	
	@Override
	protected void onProgressUpdate(Integer... integers) {
		//Method is called every time a publishProgress is called from doInBackground
		for(Integer integer : integers) {
			System.out.println("TCPServer: Message received - " + integer);
			switch(integer) {
			case 1:
				//Enable SMS service
				if(vthread != null)
					vthread.msgFlag = true;
				break;
			case 2:
				//Move backward
				sendSignal(2);
				break;
			case 4:
				//Move left
				sendSignal(4);
				break;
			case 5:
				//Stop
				sendSignal(5);
				break;
			case 6:
				//Move right
				sendSignal(6);
				break;
			case 7:
				//Buzzer toggle
				sendSignal(7);
			case 8:
				//Move forward
				sendSignal(8);
				break;
			case 9:
				//Toggle Video
				toggleVideo();
				break;
			default:
				System.out.println("TCPServer: Unrecognized instruction : " + integers[0]);
				break;
			}
		}
	}
	
	/*
	 * Toggles video streaming state
	 * if it was on then it will stop it
	 * else it will start video streaming
	 */
	private void toggleVideo() {
		//If videoFlag is true then stop Video else start video
		if(vthread==null)
			return;
		
		if(vthread.videoStream) {
			vthread.videoStream = false;
			System.out.println("TCPServer: Video streamming stopped");
		} else {
			System.out.println("TCPServer: Video streamming started");
			vthread.videoStream = true;
		}
	}
	
	
	/*
	 * `s the server process
	 */
	public void stop() {
		System.out.println("TCPServer: Stopping server");
		flag = false;
		
		//Stopping video if it is running
		if(vthread != null) {
			vthread.stopVideo();
			vthread = null;
		}
		System.out.println("TCPServer: Video stopped");
		
		//Closing server socket
		if(serverSocket != null) {
			try {
				serverSocket.close();
				System.out.println("TCPServer: Server Socket Closed");
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("TCPServer: Error while closing socket");
			}
			serverSocket = null;
		}
	}
}
