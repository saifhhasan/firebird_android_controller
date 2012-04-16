package project.client.network;

import java.net.SocketException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

/*
 * This is asyncTask in which doInBackground function runs in separate thread and
 * on ProgressUpadate is called by main UI thread so that it can update UI
 */
public class VideoThread extends AsyncTask<Void, Bitmap, Void> {
	Receiver receiver;
	ImageView display;
	Bitmap bitmap;
	boolean flag;
	
	/*
	 * Initiate class object and sets global parameters
	 */
	public VideoThread(ImageView display) throws SocketException {
		receiver = new Receiver(8656);
		display.setAdjustViewBounds(true);
		display.setMaxHeight(320);
		display.setMaxWidth(480);
		this.display = display;
		flag = false;
	}
	
	/*
	 * Stops video thread
	 */
	public void stop() {
		flag = false;
		if(receiver != null)
			receiver.close();
		receiver = null;
	}
	
	
	@Override
	protected Void doInBackground(Void... arg0) {
		byte[] data = null;
		flag = true;
		System.out.println("VideoThread: Starting a new thread");
		while(flag) {
			if(receiver != null)
				data = receiver.recievePacket();
			else
				data = null;
			
			if(data == null) {
				System.out.println("VideoThread: Null packet");
			} else {
				bitmap = BitmapFactory.decodeByteArray(data,0,data.length);
				publishProgress(bitmap);
			}
		}
		return null;
	}
	
	
	@Override
	protected void onProgressUpdate(Bitmap... bitmaps) {		
		for(Bitmap bitmap : bitmaps) {
			display.setImageBitmap(bitmaps[0]);
		}
		//This method is called by UI thread to update the image on screen
	}
}
