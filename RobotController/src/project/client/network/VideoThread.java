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
		this.display = display;
		flag = false;
	}
	
	public void stop() {
		flag = false;
	}

	@Override
	protected Void doInBackground(Void... arg0) {
		// TODO Auto-generated method stub
		byte[] data;
		flag = true;
		System.out.println("VideoThread: Starting a new thread");
		while(flag) {
			data = receiver.recievePacket();
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
		display.setImageBitmap(bitmaps[0]);
		//This method is called by UI thread to update the image on screen
	}
}
