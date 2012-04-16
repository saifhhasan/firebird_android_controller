package project.robot;

import java.io.OutputStream;
import java.lang.reflect.Method;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

/*
 * Creates a bluetooth Connection to module Identified by specified MAC Address
 * Has functionality to send byte to connected device
 */
public class BluetoothConnection {
	private String macAddress;
	public boolean connected;
	
	private BluetoothSocket socket;
	private BluetoothDevice device;
	private OutputStream out;
	
	
	/*
	 * Constructor: Tries to connect to device, if success then sets connected to true
	 * else it sets connected to false
	 */
	public BluetoothConnection(String mac) {
		macAddress = mac;
		this.socket = null;
		this.device = null;
		this.out = null;
		connected = false;
		
		//Initializing bluetooth module
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		if(adapter == null) {
			System.out.println("BluetoothConnection: Bluetooth not supported by system");
			return;
		}
		
		//Searching for device
		device = adapter.getRemoteDevice(macAddress);
		if(device == null) {
			System.out.println("BluetoothConnection: null device, can't find device with specified macAddress");
			return;
		}
		
		//Creating socket from method
		Method method;
		try {
			method = device.getClass().getMethod("createRfcommSocket",new Class[] { int.class });
			socket = (BluetoothSocket) method.invoke(device, Integer.valueOf(1));
		} catch (Exception e) {
			System.out.println("BluetoothConnection: Error while getting method");
			e.printStackTrace();
			return;
		}
		
		//Trying to connect to socket
		try {
			socket.connect();
		} catch (Exception e) {
			System.out.println("BluetoothConnection: Error while initiating socket connection");
			e.printStackTrace();
			return;
		}
		
		System.out.println("BluetoothConnection: Socket connected");
		
		//Getting Outputstream from socket
		try {
			out = socket.getOutputStream();
		} catch (Exception e) {
			System.out.println("BluetoothConnection: Error while getting outputstream from socket, socket might be null");
			e.printStackTrace();
			return;
		}
		
		System.out.println("BluetoothConnection: Successfully connected to device " + macAddress);
		connected = true;
	}
	
	/*
	 * Sends the last 8 bits of value to the connected device in raw format
	 */
	public void send(int value) {
		int msg = (int) (0xff & value);
		try {
			out.write(msg);
			System.out.println("BluetoothConnection: value sent : " + msg);
		} catch (Exception e) {
			System.out.println("BluetoothConnection: eror while sending value : " + msg);
			e.printStackTrace();
		}
	}
	
	/*
	 * Closes existing bluetooth Connection.
	 * Release held resources
	 */
	public void close() {
		try {
			if(out != null)
				out.close();
			if(socket != null)
				socket.close();
			out = null;
			socket = null;
			device = null;
		} catch(Exception e) {
			System.out.println("BluetoothConnection: error while closing bluetooth connection");
			e.printStackTrace();
		}
	}
}
