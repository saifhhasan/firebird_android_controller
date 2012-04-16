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


/*
 * Main UI Class for RobotController Application.
 * We haven't integrated accelerometer yet, but it could be easily setup
 */
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
    
    /*
     * Called whenever UP button is clicked, it sends 8 for Forward movement
     */
    public void onButtonUpClicked(View v) {
    	if(toggleAccelerometer == -1) {
    		return;
    	} else {
    		if(sendCommand(8))
    			Toast.makeText(RobotControllerActivity.this, "Up Button clicked", Toast.LENGTH_SHORT).show();	
    	}
    }
    
    /*
     * Called whenever RIGHT button is clicked, it sends 6 for Forward movement
     */
    public void onButtonRightClicked(View v) {
    	if(toggleAccelerometer == -1){
    		return;
    	} else {
    		if(sendCommand(6))
    			Toast.makeText(RobotControllerActivity.this, "Right Button clicked", Toast.LENGTH_SHORT).show();
    	}
    }
    
    /*
     * Called whenever LEFT button is clicked, it sends 4 for Forward movement
     */
    public void onButtonLeftClicked(View v) {
    	if(toggleAccelerometer ==  -1){
    		return;
    	} else {
    		if(sendCommand(4))
    			Toast.makeText(RobotControllerActivity.this, "Left Button clicked", Toast.LENGTH_SHORT).show();
    	}    	
    }
    
    /*
     * Called whenever DOWN button is clicked, it sends 2 for Forward movement
     */
    public void onButtonDownClicked(View v) {
    	if(toggleAccelerometer  == -1) {
    		return;
    	} else {
    		if(sendCommand(2))
    			Toast.makeText(RobotControllerActivity.this, "Down Button clicked", Toast.LENGTH_SHORT).show();
    	}
    }
    
    /*
     * Toggles video. If video was not running then it starts video streaming
     * else it stops the video streaming. (also allocates and releases required resources for video streaming)
     */
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
    
    /*
     * Called whenever BUZZER toggle button is clicked, it sends 7 which enables buzzer on robot
     */
    public void onToggleBuzzer(View v){
    	if(sendCommand(7))
    		toggleBuzzer *= -1;
    	if(toggleBuzzer == 1)
    		Toast.makeText(RobotControllerActivity.this, "Buzzer On", Toast.LENGTH_SHORT).show();
     	else 
    		Toast.makeText(RobotControllerActivity.this, "Buzzer Off", Toast.LENGTH_SHORT).show();
    }
    
    /*
     * Called whenever SMS Alert button is clicked, it sends 1 which enables Motion Detector on ROBOT.
     * Whenever motion is detected SMS will come to registered user
     */
    public void startSMSAlert(View v) {
    	if(sendCommand(1))
    		Toast.makeText(RobotControllerActivity.this, "SMS Alert Enabled", Toast.LENGTH_SHORT).show();
    	else
    		Toast.makeText(RobotControllerActivity.this, "Couldn't Enable SMS Alert", Toast.LENGTH_SHORT).show();
    }
    
    /*
     * Stops the robot by sending 5 to the remote android phone
     */
    public void stopRobot(View v) {
    	if(sendCommand(5)) {
    		Toast.makeText(RobotControllerActivity.this, "Robot Stopped", Toast.LENGTH_SHORT).show();
    	} else {
    		Toast.makeText(RobotControllerActivity.this, "Unable to stop the robot", Toast.LENGTH_SHORT).show();
    	}
    }
    
    /*
     * Tries to sends msg as int to remote android phone,
     * if succeeds then return true else return false
     */
    public boolean sendCommand(int msg) {
    	if(client != null && client.send(msg))
    		return true;
    	else
    		return false;
    }
    
    /*
     * For now we haven't implemented this. But this can be easily implemented
     */
    public void buttonAccelerometer(View v){
    	toggleAccelerometer *= -1;
    }
    
    /*
     * Tries to connect to remote TCPServer
     */
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
    
    /*
     * Closes connection with remove TCP Server
     */
    public void onButtonDisconnect(View v) {
    	if(client != null) {
    		client.close();
    		client = null;
    	}
    }
}