package project.client;

import java.net.SocketException;

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
	private int toggleAudio;
	private int toggleBuzzer;
	private int toggleAccelerometer;
	private EditText ipaddress;
	private ImageView videofeed;
	Bitmap defaultBMP;
	
	private VideoThread vthread;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        toggleVideo = -1;
        toggleAudio = -1;
        toggleBuzzer = -1;
        toggleAccelerometer = 1;
        ipaddress = (EditText)this.findViewById(R.id.IpText);
        videofeed = (ImageView)this.findViewById(R.id.ImageView1);
        try {
			vthread = new VideoThread(videofeed);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }
    
    public void onButtonUpClicked(View v) {
        // Do something when the button is clicked
    	if(toggleAccelerometer == -1)
    		return;
    	else{
    		Toast.makeText(RobotControllerActivity.this, "Up Button clicked", Toast.LENGTH_SHORT).show();	
    	}
    }
    
    public void onButtonRightClicked(View v) {
        // Do something when the button is clicked
    	if(toggleAccelerometer == -1){
    		return;
    	}
    	else{
    		Toast.makeText(RobotControllerActivity.this, "Right Button clicked", Toast.LENGTH_SHORT).show();
    	}
    }
    
    public void onButtonLeftClicked(View v) {
        // Do something when the button is clicked
    	if(toggleAccelerometer ==  -1){
    		return;
    	}else{
    		Toast.makeText(RobotControllerActivity.this, "Left Button clicked", Toast.LENGTH_SHORT).show();
    	}    	
    }
    
    public void onButtonDownClicked(View v) {
        // Do something when the button is clicked
    	if(toggleAccelerometer  == -1){
    		return;
    	}else{
    		Toast.makeText(RobotControllerActivity.this, "Down Button clicked", Toast.LENGTH_SHORT).show();
    	}
    }
    
    public void onToggleVideo(View v){
    	toggleVideo *= -1; 
    	if(toggleVideo == 1) {
    		Toast.makeText(RobotControllerActivity.this, "Video On", Toast.LENGTH_SHORT).show();
    		vthread.stop();
    		vthread.execute(null);
    	} else { 
    		Toast.makeText(RobotControllerActivity.this, "Video Off", Toast.LENGTH_SHORT).show();
    		vthread.stop();
     	}
    }
    
    public void onToggleAudio(View v){
    	toggleAudio *= -1; 
    	if(toggleAudio == 1)
    		Toast.makeText(RobotControllerActivity.this, "Audio On", Toast.LENGTH_SHORT).show();
     	else 
    		Toast.makeText(RobotControllerActivity.this, "Audio Off", Toast.LENGTH_SHORT).show();
    }
    
    public void onToggleBuzzer(View v){
    	toggleBuzzer *= -1; 
    	if(toggleBuzzer == 1)
    		Toast.makeText(RobotControllerActivity.this, "Buzzer On", Toast.LENGTH_SHORT).show();
     	else 
    		Toast.makeText(RobotControllerActivity.this, "Buzzer Off", Toast.LENGTH_SHORT).show();
    }
    
    public void buttonAccelerometer(View v){
    	toggleAccelerometer *= -1;    	
    }
    
    public void onbuttonConnect(View v){    	
    	String ip = ipaddress.getText().toString();
    	Toast.makeText(RobotControllerActivity.this, "Connected to " + ip, Toast.LENGTH_SHORT).show();
    }
}