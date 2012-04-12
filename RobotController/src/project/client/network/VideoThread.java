package project.client.network;

import java.net.SocketException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class VideoThread extends AsyncTask<Void, Bitmap, Void> {
	Receiver receiver;
	ImageView display;
	Bitmap bitmap;
	boolean flag;
	
	public VideoThread(ImageView display) throws SocketException {
		receiver = new Receiver(8656);
		display.setAdjustViewBounds(true);
		display.setMaxHeight(320);
		display.setMaxWidth(480);
		this.display = display;
		flag = false;
	}
	
	
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
		System.out.println("Height: " + bitmaps[0].getHeight());
		System.out.println("Width: " + bitmaps[0].getWidth());
		
		for(Bitmap bitmap : bitmaps) {
			display.setImageBitmap(bitmaps[0]);
		}
		//This method is called by UI thread to update the image on screen
	}
}
