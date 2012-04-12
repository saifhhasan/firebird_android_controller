package project.client;

import project.client.network.TCPClient;
import project.client.network.VideoThread;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import controller.gui.R;

public class RobotControllerActivity extends Activity {
	
	private int toggleVideo;
	private int toggleBuzzer;
	private int toggleAccelerometer;
	private EditText ipaddress;
	private ImageView videofeed;
	Bitmap defaultBMP;
	
	private VideoThread vthread;
	private TCPClient client;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        toggleVideo = -1;
        toggleBuzzer = -1;
        toggleAccelerometer = 1;
        ipaddress = (EditText)this.findViewById(R.id.IpText);
        videofeed = (ImageView)this.findViewById(R.id.ImageView1);
        client = null;
    }
    
    public void onButtonUpClicked(View v) {
        // Do something when the button is clicked
    	if(toggleAccelerometer == -1) {
    		return;
    	} else {
    		if(sendCommand(8))
    			Toast.makeText(RobotControllerActivity.this, "Up Button clicked", Toast.LENGTH_SHORT).show();	
    	}
    }
    
    public void onButtonRightClicked(View v) {
        // Do something when the button is clicked
    	if(toggleAccelerometer == -1){
    		return;
    	} else {
    		if(sendCommand(6))
    			Toast.makeText(RobotControllerActivity.this, "Right Button clicked", Toast.LENGTH_SHORT).show();
    	}
    }
    
    public void onButtonLeftClicked(View v) {
    	if(toggleAccelerometer ==  -1){
    		return;
    	} else {
    		if(sendCommand(4))
    			Toast.makeText(RobotControllerActivity.this, "Left Button clicked", Toast.LENGTH_SHORT).show();
    	}    	
    }
    
    public void onButtonDownClicked(View v) {
    	if(toggleAccelerometer  == -1) {
    		return;
    	} else {
    		if(sendCommand(2))
    			Toast.makeText(RobotControllerActivity.this, "Down Button clicked", Toast.LENGTH_SHORT).show();
    	}
    }
    
    public void onToggleVideo(View v){
    	if(sendCommand(9))
    		toggleVideo *= -1;
    	
    	if(toggleVideo == 1) {
    		Toast.makeText(RobotControllerActivity.this, "Video On", Toast.LENGTH_SHORT).show();
    		if(vthread!=null)
    			vthread.stop();
    		try {
    			vthread = new VideoThread(videofeed);
    			vthread.execute(null);
    		} catch (Exception e) {
    			Toast.makeText(RobotControllerActivity.this, "Error while starting video", Toast.LENGTH_SHORT).show();
    			e.printStackTrace();
    		}
    	} else { 
    		Toast.makeText(RobotControllerActivity.this, "Video Off", Toast.LENGTH_SHORT).show();
    		if(vthread != null)
    			vthread.stop();
    		vthread = null;
     	}
    }
    
    public void onToggleBuzzer(View v){
    	if(sendCommand(7))
    		toggleBuzzer *= -1;
    	if(toggleBuzzer == 1)
    		Toast.makeText(RobotControllerActivity.this, "Buzzer On", Toast.LENGTH_SHORT).show();
     	else 
    		Toast.makeText(RobotControllerActivity.this, "Buzzer Off", Toast.LENGTH_SHORT).show();
    }
    
    public void startSMSAlert(View v) {
    	if(sendCommand(1))
    		Toast.makeText(RobotControllerActivity.this, "SMS Alert Enabled", Toast.LENGTH_SHORT).show();
    	else
    		Toast.makeText(RobotControllerActivity.this, "Couldn't Enable SMS Alert", Toast.LENGTH_SHORT).show();
    }
    
    public void buttonAccelerometer(View v){
    	toggleAccelerometer *= -1;
    }
    
    public void onbuttonConnect(View v){    	
    	String ip = ipaddress.getText().toString();
    	try {
			client = new TCPClient(ip, 8655);
			Toast.makeText(RobotControllerActivity.this, "Connected to " + ip, Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			client = null;
			Toast.makeText(RobotControllerActivity.this, "Unable to connect to " + ip, Toast.LENGTH_SHORT).show();
		}
    }
    
    public void onButtonDisconnect(View v) {
    	if(client != null) {
    		client.close();
    		client = null;
    	}
    }
    
    public void stopRobot(View v) {
    	if(sendCommand(5)) {
    		Toast.makeText(RobotControllerActivity.this, "Robot Stopped", Toast.LENGTH_SHORT).show();
    	} else {
    		Toast.makeText(RobotControllerActivity.this, "Unable to stop the robot", Toast.LENGTH_SHORT).show();
    	}
    }
    
    /*
     * Sends msg to another android phone
     */
    public boolean sendCommand(int msg) {
    	if(client != null && client.send(msg))
    		return true;
    	else
    		return false;
    }
}