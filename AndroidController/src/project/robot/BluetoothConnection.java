package project.robot;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class BluetoothConnection {
	private String macAddress;
	public boolean connected;
	
	private BluetoothSocket socket;
	private BluetoothDevice device;
	private OutputStream out;
	
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
		
		device = adapter.getRemoteDevice(macAddress);
		if(device == null) {
			System.out.println("BluetoothConnection: null device, can't find device with specified macAddress");
			return;
		}
		
		Method method;
		try {
			method = device.getClass().getMethod("createRfcommSocket",new Class[] { int.class });
			socket = (BluetoothSocket) method.invoke(device, Integer.valueOf(1));
		} catch (Exception e) {
			System.out.println("BluetoothConnection: Error while getting method");
			e.printStackTrace();
			return;
		}
		
		try {
			socket.connect();
		} catch (Exception e) {
			System.out.println("BluetoothConnection: Error while initiating socket connection");
			e.printStackTrace();
			return;
		}
		
		System.out.println("BluetoothConnection: Socket connected");
		
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
