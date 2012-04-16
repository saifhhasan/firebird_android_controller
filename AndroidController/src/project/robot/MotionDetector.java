package project.robot;


/*
 * This class detects changes in the successive frames, uses number of pixels
 * changed to detect motion
 */
public class MotionDetector {
	int[] prev;
	int[] current;
	int threshold;
	int pixelThreshold;
	int imgLength;
	boolean nw;
		
	/*
	 * Initiate class, Allocate memory to image arrays and set threshold values
	 */
	public MotionDetector() {
		prev = null;
		current = null;
		threshold = 19999;
		pixelThreshold = 70;
		prev = new int[500*500];
		current = new int[500*500];
		nw = true;
	}
	
	/*
	 * Detects the motion using current and previously stored image.
	 * It gives the false for the first image
	 */
	public boolean detectMotion(int[] img) {
		int diff;
		imgLength = img.length;
		
		System.arraycopy(current, 0, prev, 0, current.length);
		System.arraycopy(img, 0, current, 0, img.length);
		
		//If new then return false
		if(nw) {
			nw = false;
			return false;
		}
		
		//Get number of pixels changed in current image wrt previous image
		diff = getDifference();
		
		//If exceeds threshold then return true else return false
		if(diff > threshold)
			return true;
		else
			return false;
	}
	
	/*
	 * Counts the number of pixels changed in current wrt previous image
	 */
	private int getDifference() {
		int diff = 0;
		int pix1, pix2;
		
		for(int i=0; i<imgLength; i++) {
			pix1 = (int) (0xff & current[i]);
			pix2 = (int) (0xff & prev[i]);
			
			//Checking for if pixels are out of range
			if(pix1 < 0)
				pix1 = 0;
			else if (pix1 > 255)
				pix1 = 255;
			
			if(pix2 < 0)
				pix2 = 0;
			else if(pix2 > 255)
				pix2 = 255;
			
			if(Math.abs(pix1-pix2) > pixelThreshold)
				diff++;
		}
		
		//In case overflow happens
		if(diff < 0)
			diff = threshold + 1;
		return diff;
	}
}

