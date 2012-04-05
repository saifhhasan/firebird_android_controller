package project.robot.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

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
	
	/*
	 * Starts a TCP Server which listens to incoming connections
	 */
	public TCPServer(int port) {
		this.port = port;
		videoFlag = false;
		vthread = null;
	}
	
	
	/*
	 * Stops the server process
	 */
	public void stop() {
		System.out.println("TCPServer: Stopping server");
		flag = false;
		if(serverSocket != null) {
			try {
				serverSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("TCPServer: Error while closing socket");
			}
			serverSocket = null;
		}
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
				
				//Reading input commands and signaling processing function
				while(flag && msg != 0) {
					msg = in.readInt();
					publishProgress(msg);
				}
				
				//Stopping video streaming if running
				if(videoFlag)
					toggleVideo();
				
				//Closing Connection to client
				clientSocket.close();
				clientSocket = null;
				System.out.println("TCPServer: Closed connection to host at " + clientIP);
				clientIP = "null";
				
			} catch (IOException e) {
				if(clientSocket != null) {
					clientSocket = null;
				}
			}
			
			if(serverSocket != null) {
				try {
					serverSocket.close();
				} catch (IOException e) {
					System.out.println("TCPServer: Error while closing socket");
				}
				serverSocket = null;
			}
		}
		
		//Closing serverSocket
		if(serverSocket != null) {
			try {
				serverSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("TCPServer: Error while closing socket");
			}
			serverSocket = null;
		}
		System.out.println("TCPServer: Server stopped");
		return null;
	}
	
	@Override
	protected void onProgressUpdate(Integer... integers) {
		//Method is called every time a publishProgress is called from doInBackground
		switch(integers[0]) {
		case 2:
			//Move forward
			//send signal to robot with value 2
			break;
		case 4:
			//Move right
			//send signal to robot with value 4
			break;
		case 5:
			//Stop
			//send signal to robot with value 5 to stop it
			break;
		case 6:
			//Move right
			//Send signal to robot with value 6
			break;
		case 7:
			//Buzzer toggle
			//Send signal to toggle buzzer
			break;
		case 8:
			//Move forward
			break;
		case 9:
			toggleVideo();
			break;
		default:
			System.out.println("TCPServer: Unrecognized instruction : " + integers[0]);
		}
	}
	
	/*
	 * Toggles video streaming state
	 * if it was on then it will stop it
	 * else it will start video streaming
	 */
	private void toggleVideo() {
		//If videoFlag is true then stop Video else start video
		if(videoFlag) {
			if(vthread != null) {
				vthread.stopVideo();
				vthread = null;
			}
			System.out.println("TCPServer: Video stopped");
			videoFlag = false;
		} else {
			if(vthread != null) {
				vthread.stopVideo();
				vthread = null;
			}
			try {
				vthread = new VideoThread(clientIP);
				vthread.startVideo();
				System.out.println("TCPServer: Video started");
				videoFlag = true;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				vthread = null;
				System.out.println("TCPServer: Error while starting video");
			}
		}
	}
}
