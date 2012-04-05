package project.robot;

import project.robot.network.TCPServer;
import project.robot.network.VideoThread;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import controller.gui.R;

public class AndroidControllerActivity extends Activity {
	VideoThread vthread;
	//private EditText ipaddress;
	private TCPServer server;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.main);
        //ipaddress = (EditText)this.findViewById(R.id.ipText);
    }
    
    /*
    public void handleClick(View v) {
    	String ip = ipaddress.getText().toString();
    	try {
			vthread = new VideoThread(ip);
			vthread.startVideo();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void stopVideo(View v) {
    	if(vthread!=null) {
    		vthread.stopVideo();
    		vthread = null;
    	}
    }
    */
    
    public void startServer(View v) {
		server = new TCPServer(8655);
		server.execute(null);
    }
    
    public void stopServer(View v) {
    	server.stop();
    }
}