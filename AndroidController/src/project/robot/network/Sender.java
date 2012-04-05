package project.robot.network;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Sender {
	/*
	 * Variable declarations
	 */
	private int portNo;
	private int packetSize;
	private int packetNumber = 0;
	int HEADER_SIZE = 16;
	int[] header;
	
	private InetAddress ipAddress;
	private DatagramPacket packet;
	private DatagramSocket socket;
	
	private ByteArrayOutputStream baos;
	private DataOutputStream dos;
	
	/*
	 * Sets default packet size to 1000 ints
	 * Gets Datagram socket from System with random port number
	 * Sets the IP Address and Port number which are going to used for transmitting packets
	 */
	public Sender(String ip, int port) throws UnknownHostException, SocketException {
		//Default packet size if 1000 bytes
		packetSize = 1000;
		portNo = port;
		
		//Decode String ip into ipAddress
		ipAddress = InetAddress.getByName(ip);
		
		//Gets the socket from system for sending data
		socket = new DatagramSocket();
		
		/*
		 * Header contains: 
		 * 		Packet Number
		 * 		Number of Fragments in Image Packet
		 * 		Fragment No of Current Datagram Packet
		 * 		Size of the data Contained  
		 */
		header = new int[4];
		
		HEADER_SIZE = header.length * 4;
	}
	
	/*
	 * Releases the binded socket and returns
	 */
	public void close() {
		//Release binded socket
		socket.close();
	}
	
	/*
	 * Sets packetSize to that of argument
	 * Gets Datagram socket from System with random port number
	 * Sets the IP Address and Port number which are going to used for transmitting packets
	 */
	public Sender(String ip, int port, int packetSize) throws UnknownHostException, SocketException {
		this.packetSize = packetSize;
		portNo = port;
		ipAddress = InetAddress.getByName(ip);
		socket = new DatagramSocket();
	}
	
	/*
	 * It fragments the data array into chunks of 1000 bytes. Generates
	 * appropriate header for each fragment to be sent and send the packet
	 * using UDP to specified destination
	 */
	public void sendPacket(byte[] data) {
		int len = data.length;
		int noOfPackets = (len/packetSize);
		int fragmentNo = 0, offset=0;;
		int packetLen = packetSize;
		
		//Calculating number of DatagramPackets to be sent
		if(len % packetSize != 0)
			noOfPackets++;
		
		/*
		 * System.out.println("Sending packet: " + data.length);
		 * System.out.println("len:" + len + "  noOfPackets:"+noOfPackets);
		 */
		
		for(int i=0; i<noOfPackets; i++) {
			
			//Setting fragmentLength (Datagram Packet length)
			if((offset + packetSize) >= len) {
				packetLen = (len - offset);
			}
			
			//Function Appends header and data into a single byte array
			packet = getDatagramPacket(packetNumber, fragmentNo, packetLen, data, offset, noOfPackets);
			
			/*
			 * System.out.println("\tSending fragment:" + fragmentNo);
			 */
			
			try {
				//Trying to send packet
				socket.send(packet);
				
				/*
				 * System.out.println("PN: " + packetNumber + "\tFN: " + fragmentNo + "\tFS: " + packet.getLength());
				 */
				
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Error while sending packet: " + e.toString());
			}
			
			offset += packetLen;
			fragmentNo++;
		}
		packetNumber = (packetNumber+1) % 1024;
	}
	
	/*
	 * Given a parameter values. It sets up the header.
	 * Then it generate and return byte array of header appended with data[]
	 */
	private DatagramPacket getDatagramPacket(int packetNum, int fragmentNo, int size, byte[] data, int offset, int noOfFragments) {
		DatagramPacket pkt = null;
		baos = new ByteArrayOutputStream();
		dos = new DataOutputStream(baos);
		header[0] = packetNum;
		header[1] = noOfFragments;
		header[2] = fragmentNo;
		header[3] = size;
		byte[] bytes;
		
		//Writing header[] and data[] into DataOutputStream, which can be directly converted to byte array 
		try {
			for(int i=0; i<4; i++)
				dos.writeInt(header[i]);
			dos.write(data, offset, size);
			bytes = baos.toByteArray();
			pkt = new DatagramPacket(bytes, bytes.length, ipAddress, portNo);
		} catch (IOException e) {
			System.out.println("Exception while generating Datagram Packet");
			e.printStackTrace();
		}
		return pkt;
	}
	
}
