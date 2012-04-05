/**
 * Project Name: Android_FB5
 * Date:	8/11/2010
 */
/********************************************************************************

   Copyright (c) 2010, ERTS Lab IIT Bombay erts@cse.iitb.ac.in               -*- c -*-
   All rights reserved.

   Redistribution and use in source and binary forms, with or without
   modification, are permitted provided that the following conditions are met:

   * Redistributions of source code must retain the above copyright
     notice, this list of conditions and the following disclaimer.

   * Redistributions in binary form must reproduce the above copyright
     notice, this list of conditions and the following disclaimer in
     the documentation and/or other materials provided with the
     distribution.

   * Neither the name of the copyright holders nor the names of
     contributors may be used to endorse or promote products derived
     from this software without specific prior written permission.

   * Source code can be used for academic purpose. 
	 For commercial use permission form the author needs to be taken.

  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
  ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
  POSSIBILITY OF SUCH DAMAGE. 

  Software released under Creative Commence cc by-nc-sa licence.
  For legal information refer to: 
  http://creativecommons.org/licenses/by-nc-sa/3.0/legalcode

********************************************************************************/


package project.client;

import controller.gui.R;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/** Class for implementing Accelerometer sensor reading.
 * Task: (1) Start listener for change in accelerometer values.
 * 		 (2) Accelerometer change listener, sending appropriate commands over BT after decoding
 * 		 (3) Stop accelerometer listener.  
 */
@SuppressWarnings("deprecation")
public class AccelerometerReader {

	final String tag = "Android_FB5";

	private static final int STOP = 0;
	private static final int FRONT = 1;
	private static final int RIGHT = 2;
	private static final int BACK = 3;
	private static final int LEFT = 4;

	private SensorManager mSensorManager;
	private float mAccelX = 0;
	private float mAccelY = 0;
	public float mAccelZ = 0;

	private int prev_state = -1;
	private int curr_state = -1;
	private byte cur_speed = -1;
	private byte prev_speed = -1;

	Activity mactivity;
	TextView xViewA = null;
	TextView yViewA = null;
	TextView zViewA = null;
	TextView cmdView = null;
	ImageView mImageView = null;

	//private BluetoothComm mmBluetoothComm;
	private Context mcontext;

	private int count = 0;
	private final int DELAY = 20;

	private final SensorListener mSensorAccelerometer = new SensorListener()
	{
		/** Called when there is a change in accelerometer value. Takes the acceleration values, decodes it
		 * and display currently.
		 */
		public void onSensorChanged(int sensor, float [] values)
		{
			//Log.d(tag, "onSensorChanged: " + sensor + ", x: " + values[0] + ", y: " + values[1] + ", z: " + values[2]);
			/** Insert delay, so as to filter out the change in acceleration caused due to sudden jerks. */
			if(count<DELAY)
			{
				count = count + 1;
				return;
			}

			count = 0;
			
			/** Copy the values of acceleration in 3 directions. */
			mAccelX = values[0];
			mAccelY = values[1];
			mAccelZ = values[2];

			/** Display the acceleration value in text box. */
			xViewA.setText("Acceleration in X:  " + mAccelX);
			yViewA.setText("Acceleration in Y:  " + mAccelY);
			zViewA.setText("Acceleration in Z:  " + mAccelZ);

			byte[] send_buffer = new byte[4];
			
			/** Start decoding accelerometer values. */
			/** With phone held in upright condition, +ve X-axis goes to right,
			 *  +ve Y-axis goes front and +ve Z-axis points towards sky.
			 * 
			 *  Hence 	+ve x-value--> Right
			 *  		-ve x-value--> Left
			 *  		+ve y-value--> Front
			 *  		-ve y-value--> Back
			 */
			if (mAccelX>2.0 && mAccelZ>-9.7)  
			{
				curr_state = RIGHT;
				cur_speed = find_speed(mAccelX);
			}
			else if (mAccelX< -2.0 && mAccelZ>-9.7)
			{
				curr_state = LEFT;
				cur_speed = find_speed(mAccelX);
			}
			else //(mAccelX<2 && mAccelX>-2)
			{
				if (mAccelY>2 && mAccelZ>-9.7)
				{
					curr_state = FRONT;
					cur_speed = find_speed(mAccelY);
				}
				else if (mAccelY<-2 && mAccelZ>-9.7)
				{
					curr_state = BACK;
					cur_speed = find_speed(mAccelY);
				}
				else 
				{
					curr_state = STOP;
					cur_speed = 0;
				}
			}

			/**Update the text box and send BT command only if direction or speed is changed. */
			if(prev_state != curr_state || prev_speed !=cur_speed)
			{
				switch (curr_state)
				{
				case STOP:
				{
					cmdView.setText("Command: Stop"); //Update text box.
					send_buffer[0] = 'S';	// Set buffer with string to indicate Stop command.					 
					break;
				}
				case FRONT:
				{
					cmdView.setText("Command: Front. " + "Speed: " +cur_speed);					
					send_buffer[0] = 'F';
					break;
				}
				case RIGHT:
				{
					cmdView.setText("Command: Right. " + "Speed: " +cur_speed);					
					send_buffer[0] = 'R';
					break;
				}
				case BACK:
				{
					cmdView.setText("Command: Back. " + "Speed: " +cur_speed);					
					send_buffer[0] = 'B';
					break;
				}
				case LEFT:
				{
					cmdView.setText("Command: Left. " + "Speed: " +cur_speed);					
					send_buffer[0] = 'L';
					break;
				}
				}
				
				/*
				 * 
				 * Protocol for sending needs to be decided
				 */
				int tmp = cur_speed + (byte)48;   // need to experiment this value of 48
				
				send_buffer[1] = Byte.valueOf((byte)tmp); //Set buffer to indicate the speed				
				send_buffer[2] = 0x0d; //Add 'CR' 'LF' at the end of command string
				send_buffer[3] = 0x0a;
				
				
				//mmBluetoothComm.BluetoothSend(send_buffer); //Bluetooth send function.
				Log.d(tag, "Transmitted: "+ send_buffer[0] + cur_speed);
			}

			prev_state = curr_state; //Update the state and speed
			prev_speed = cur_speed;
		}

		public void onAccuracyChanged(int sensor, int accuracy) {
			// not used
		}
	};

	/** Function to find the speed (from 1 to 8) based on the amount of tilt.
	 * Task: (1)Based on the acceleration values it finds the speed(irrespective of direction) to be sent over BT.
	 * Arguments: Acceleration value.
	 * Return : Encoded speed on a scale of 1 to 8. 
	 */
	private byte find_speed(float acc_value)
	{
		byte speed = 0;
		if ((acc_value>2 && acc_value<=3) || (acc_value<-2 && acc_value>=-3))
		{
			speed = 1;
		}
		else if ((acc_value>3 && acc_value<=4) || (acc_value<-3 && acc_value>=-4))
		{
			speed = 2;
		}
		else if ((acc_value>4 && acc_value<=5) || (acc_value<-4 && acc_value>=-5))
		{
			speed = 3;
		}
		else if ((acc_value>5 && acc_value<=6) || (acc_value<-5 && acc_value>=-6))
		{
			speed = 4;
		}
		else if ((acc_value>6 && acc_value<=7) || (acc_value<-6 && acc_value>=-7))
		{
			speed = 5;
		}
		else if ((acc_value>7 && acc_value<=8) || (acc_value<-7 && acc_value>=-8))
		{
			speed = 6;
		}
		else if ((acc_value>8 && acc_value<=9) || (acc_value<-8 && acc_value>=-9))
		{
			speed = 7;
		}
		else
		{
			speed = 8;
		}

		return speed;		
	}


	/** Function to set an appropriate image on UI
	 * Task: Based on the direction and the speed, an appropriate image is displayed on the UI.
	 * Arguments: Direction(state) and speed.
	 * Return : Null
	 */
	private void setImage(int state, int speed)
	{
	/**	switch(state)
		{
		case STOP:
		{
			mImageView.setImageResource(R.drawable.stop);
			break;
		}
		case FRONT:
		{
			switch (speed)
			{
			case 1:
			{
				mImageView.setImageResource(R.drawable.front1);
				break;
			}
			case 2:
			{
				mImageView.setImageResource(R.drawable.front3);
				break;
			}
			case 3:
			{
				mImageView.setImageResource(R.drawable.front4);
				break;
			}
			case 4:
			{
				mImageView.setImageResource(R.drawable.front5);
				break;
			}
			case 5:
			{
				mImageView.setImageResource(R.drawable.front6);
				break;
			}
			case 6:
			{
				mImageView.setImageResource(R.drawable.front7);
				break;
			}
			case 7:
			{
				mImageView.setImageResource(R.drawable.front8);
				break;
			}
			case 8:
			{
				mImageView.setImageResource(R.drawable.front9);
				break;
			}

			}
			break;
		}
		case BACK:
		{
			switch (speed)
			{
			case 1:
			{
				mImageView.setImageResource(R.drawable.back1);
				break;
			}
			case 2:
			{
				mImageView.setImageResource(R.drawable.back3);
				break;
			}
			case 3:
			{
				mImageView.setImageResource(R.drawable.back4);
				break;
			}
			case 4:
			{
				mImageView.setImageResource(R.drawable.back5);
				break;
			}
			case 5:
			{
				mImageView.setImageResource(R.drawable.back6);
				break;
			}
			case 6:
			{
				mImageView.setImageResource(R.drawable.back7);
				break;
			}
			case 7:
			{
				mImageView.setImageResource(R.drawable.back8);
				break;
			}
			case 8:
			{
				mImageView.setImageResource(R.drawable.back9);
				break;
			}

			}
			break;
		}
		case RIGHT:
		{
			switch (speed)
			{
			case 1:
			{
				mImageView.setImageResource(R.drawable.right1);
				break;
			}
			case 2:
			{
				mImageView.setImageResource(R.drawable.right3);
				break;
			}
			case 3:
			{
				mImageView.setImageResource(R.drawable.right4);
				break;
			}
			case 4:
			{
				mImageView.setImageResource(R.drawable.right5);
				break;
			}
			case 5:
			{
				mImageView.setImageResource(R.drawable.right6);
				break;
			}
			case 6:
			{
				mImageView.setImageResource(R.drawable.right7);
				break;
			}
			case 7:
			{
				mImageView.setImageResource(R.drawable.right8);
				break;
			}
			case 8:
			{
				mImageView.setImageResource(R.drawable.right9);
				break;
			}

			}
			break;
		}
		case LEFT:
		{
			switch (speed)
			{
			case 1:
			{
				mImageView.setImageResource(R.drawable.left1);
				break;
			}
			case 2:
			{
				mImageView.setImageResource(R.drawable.left3);
				break;
			}
			case 3:
			{
				mImageView.setImageResource(R.drawable.left4);
				break;
			}
			case 4:
			{
				mImageView.setImageResource(R.drawable.left5);
				break;
			}
			case 5:
			{
				mImageView.setImageResource(R.drawable.left6);
				break;
			}
			case 6:
			{
				mImageView.setImageResource(R.drawable.left7);
				break;
			}
			case 7:
			{
				mImageView.setImageResource(R.drawable.left8);
				break;
			}
			case 8:
			{
				mImageView.setImageResource(R.drawable.left9);
				break;
			}

			}
			break;
		}
		}

*/
	}

	/** Constructor for the class.Starts acceleration listener.
	 * Acquires handels on the text box and image view. 
	 */
	public AccelerometerReader(Context context, Activity activity)
	{
		/** Register Sensor listener as soon as the class is instantiated. */
		
		mSensorManager = (SensorManager)activity.getSystemService(Context.SENSOR_SERVICE);
		mSensorManager.registerListener(mSensorAccelerometer,SensorManager.SENSOR_ACCELEROMETER,SensorManager.SENSOR_DELAY_GAME);

		mactivity = activity;		 
		mcontext = context;
		
		/** Acquire handels on the text box and image view. */
		xViewA = (TextView) mactivity.findViewById(R.id.xbox);
		yViewA = (TextView) mactivity.findViewById(R.id.ybox);
		zViewA = (TextView) mactivity.findViewById(R.id.zbox);
		cmdView	= (TextView)mactivity.findViewById(R.id.cmdbox);
		

	}

	/** Function to start the accelerometer listener. */
	public void registerListener()
	{
		mSensorManager.registerListener(mSensorAccelerometer,SensorManager.SENSOR_ACCELEROMETER,SensorManager.SENSOR_DELAY_GAME);
	}

	/** Function to stop the accelerometer listener. 
	 * Also sets the acceleration text box and image viewer blank.
	 */
	public void unregisterListener()
	{
		mSensorManager.unregisterListener(mSensorAccelerometer);
		xViewA.setText("Acceleration in X:  - - - - ");
		yViewA.setText("Acceleration in Y:  - - - - ");
		zViewA.setText("Acceleration in Z:  - - - - ");
		cmdView.setText("");
		mImageView.setImageBitmap(null);
		Toast.makeText(mcontext, "Disconnected", 0).show();
	}

	/** Function to access x-acceleration values from outside the class. */	
	public float getXvalue()
	{
		return mAccelX;
	}

	/** Function to access y-acceleration values from outside the class. */
	public float getYvalue()
	{
		return mAccelY;
	}

	/** Function to access z-acceleration values from outside the class. */
	public float getZvalue()
	{
		return mAccelZ;
	}

}