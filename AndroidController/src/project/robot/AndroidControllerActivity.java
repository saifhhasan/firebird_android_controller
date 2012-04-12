package project.robot;

import project.robot.network.TCPServer;
import project.robot.network.VideoThread;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import controller.gui.R;

public class AndroidControllerActivity extends Activity {
	VideoThread vthread;
	private TCPServer server;
	public int msg;
	public boolean buzzer;
	public int count;
	BluetoothConnection conn;
	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.main);
        conn = new BluetoothConnection("00:19:A4:02:C6:7E"); //pararth ka mac
        if(conn.connected)
        	TCPServer.conn = conn;
        else
        	TCPServer.conn = null;
        count = 0;
    }
    
    /*
     * Starts a TCPServer socket, which starts listening to incoming connections
     * Allocates required resources for video streaming
     */
    public void startServer(View v) {
    	if(server != null)
    		stopServer(v);
		server = new TCPServer(8655);
		server.execute(null);
    }
    
    /*
     * Stops TCPServer socket and releases all held resources
     */
    public void stopServer(View v) {
    	if(server != null)
    		server.stop();
    	server = null;
    }    
}