package coderats.ar;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class RawTripCircleData {

	private static final int BUFFER_SIZE = 5;
	private static final int PROXIMITY = 52;
	private static final int TIMEOUT = 250;
	private static final int SAMPLE_FRAME_SIZE = 64;
	private static final int THRESHOLD = 127;
	private static final float ROUGH_STEP = 0.7f;
	private static final float FINE_STEP = 0.18f;
	private static final int SCAN_DIVISIONS = 30;
	private static final int DATA_LENGTH = 6;
	
	private int last_index;

	private volatile double lastX = 0;
	private volatile double lastY = 0;
	private volatile float lastR = 0;
	private volatile float lastAngle = 0;
	
	private long timestamp;

	private BufferedImage processedTripcode;
	private int[] code_confirms;

	public RawTripCircleData() {
		code_confirms = new int[BUFFER_SIZE];
		processedTripcode = new BufferedImage(SAMPLE_FRAME_SIZE, SAMPLE_FRAME_SIZE, BufferedImage.TYPE_3BYTE_BGR);
	}

	public RawTripCircleData(double[] coords, Mat m) {
		this();
		processNewCoordinates(coords, m);
	}

	private void updateImage(Mat m){

		if (m.channels() != 1){
			System.err.println("Wrong format matrix given!");
			return;
		}

		int yLow = (int) (getY()-SAMPLE_FRAME_SIZE/2);
		int yHigh = (int) (getY()+SAMPLE_FRAME_SIZE/2);
		int xLow = (int) (getX()-SAMPLE_FRAME_SIZE/2);
		int xHigh = (int) (getX()+SAMPLE_FRAME_SIZE/2);

		if(yLow < 0) {
			yHigh = SAMPLE_FRAME_SIZE;
			yLow = 0;
		}
		if(xLow < 0){
			xHigh = SAMPLE_FRAME_SIZE;
			xLow = 0;
		}
		if(yHigh > m.height()) {
			yHigh = m.height()-1;
			yLow = m.height() - SAMPLE_FRAME_SIZE;
		}
		if(xHigh > m.width()) {
			xHigh = m.width()-1;
			xLow = m.width() - SAMPLE_FRAME_SIZE;
		}

		Mat newFrame = m.submat(yLow, yHigh, xLow, xHigh).clone();
		
		processNewFrame(newFrame);
		
		newFrame.release();

	}

	//	private void transformNewFrame(Mat frame){
	//		if(lastProcessedFrame == null){
	//			lastProcessedFrame = frame;
	//		} else {
	//			try {
	//				Mat transform = Video.estimateRigidTransform(lastProcessedFrame,frame,false);
	//				if(transform.empty()){
	//					lastProcessedFrame = frame;
	//				} else {
	//					Imgproc.warpAffine(frame,lastProcessedFrame,transform,lastProcessedFrame.size(),Imgproc.INTER_NEAREST|Imgproc.WARP_INVERSE_MAP);
	//				}
	//			} catch (Exception e){
	//				S.eprintf(e.getMessage());
	//			}
	//		}
	//	}

	private float findAngleByMaximum(float from, float to, float step, Mat m){
		int lim = 0;
		float target = Integer.MAX_VALUE;
		float angle = from;
		
		//Rough find the starting segment
		for(float i=from; i < to; i += step){

			float xi = (float) (Math.sin(i));
			float yi = (float) (Math.cos(i));
			lim = 0;

			for(int d = 5; d <= 30; d++){
				int x = (int)(xi*d + SAMPLE_FRAME_SIZE/2.0f);				
				int y = (int)(yi*d + SAMPLE_FRAME_SIZE/2.0f);
				lim += m.get(y, x)[0];
				//g.drawLine(x, y, x, y);
			}
			
			if(lim < target){
				target = lim;
				angle = i;
			}
			
		}
		return angle;
	}
	
	private float findAngleBySweep(float from, float step, Mat m){
	
		float angle = from;
		float value;
		int interrupt = 0;
		
		do {
			float xi = (float) (Math.sin(angle));
			float yi = (float) (Math.cos(angle));
			
			value = 0;
			
			//g.setColor(Color.green);
			
			for(int r = 15; r <= 20; r++){
				int x = (int)(xi*r + SAMPLE_FRAME_SIZE/2.0f);				
				int y = (int)(yi*r + SAMPLE_FRAME_SIZE/2.0f);
				value += (float) m.get(y, x)[0];
				//g.drawLine(x, y, x, y);
			}
			
			value /= 5;
			
			if(value > THRESHOLD){
				angle += step;
				step *= 0.5f;
			}
		
			angle -= step;
			
			interrupt ++;
		
		} while (interrupt < 10);
		
		return angle;
	}
	
	private void processNewFrame(Mat frame){
	
		double xShift = 0;
		double yShift = 0;
		
//		boolean findXMin = true, findXMax = true, findYMin = true, findYMax = true;
//		int outerTop = 0;
//		int outerBottom = 0;
//		int outerLeft = 0;
//		int outerRight = 0;
//		
//		int innerTop = 0;
//		int innerBottom = 0;
//		int innerLeft = 0;
//		int innerRight = 0;
//		
//		//Test where the center is. Inside out.
//		for(int i=1; i < 8; i++){
//			if(findXMax && frame.get(SAMPLE_FRAME_SIZE/2, SAMPLE_FRAME_SIZE/2 + i)[0] > THRESHOLD) innerLeft = i; else findXMax = false;
//			if(findXMin && frame.get(SAMPLE_FRAME_SIZE/2, SAMPLE_FRAME_SIZE/2 - i)[0] > THRESHOLD) innerRight = i; else findXMin = false;
//			if(findYMax && frame.get(SAMPLE_FRAME_SIZE/2 + i, SAMPLE_FRAME_SIZE/2)[0] > THRESHOLD) innerTop = i; else findYMax = false;
//			if(findYMin && frame.get(SAMPLE_FRAME_SIZE/2 - i, SAMPLE_FRAME_SIZE/2)[0] > THRESHOLD) innerBottom = i; else findYMin = false;
//		}
//
//		findXMin = true;
//		findXMax = true;
//		findYMin = true;
//		findYMax = true;
//		
//		//Test the outsides. Outside in.
//		for(int i=1; i < SAMPLE_FRAME_SIZE*0.35f; i++){
//			if(findXMax && frame.get(SAMPLE_FRAME_SIZE/2, i)[0] > THRESHOLD) outerLeft = i; else findXMax = false;
//			if(findXMin && frame.get(SAMPLE_FRAME_SIZE/2, SAMPLE_FRAME_SIZE - 1 - i)[0] > THRESHOLD) outerRight = i; else findXMin = false;
//			if(findYMax && frame.get(i, SAMPLE_FRAME_SIZE/2)[0] > THRESHOLD) outerTop = i; else findYMax = false;
//			if(findYMin && frame.get(SAMPLE_FRAME_SIZE - 1 - i, SAMPLE_FRAME_SIZE/2)[0] > THRESHOLD) outerBottom = i; else findYMin = false;
//		}
//		
//		//System.out.println(innerLeft + " " + innerRight + " " + innerTop + " " + innerBottom);
//		
//		if(outerTop >= SAMPLE_FRAME_SIZE-1) outerTop = SAMPLE_FRAME_SIZE-1;
//		if(outerRight >= SAMPLE_FRAME_SIZE-1) outerRight = SAMPLE_FRAME_SIZE-1;
		
		Mat contourFrame = new Mat();
		Imgproc.Canny(frame, contourFrame, THRESHOLD, 250);
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>(2);
		Mat hierarchy = new Mat();
		Imgproc.findContours(contourFrame, contours, hierarchy, Imgproc.RETR_EXTERNAL , Imgproc.CHAIN_APPROX_NONE);
		hierarchy.release();
		contourFrame.release();
		
		MatOfPoint2f edgels = new MatOfPoint2f();
		
		double largestArea = 0;
		
		for(int i=0; i < contours.size(); i++){
			
			MatOfPoint contour = contours.get(i);
			
			double contourArea = Imgproc.contourArea(contour);
			
			if(largestArea < contourArea){
				largestArea = contourArea;
				//Core.drawContours(frame, contours, i, new Scalar(255));
				contours.get(i).convertTo(edgels, CvType.CV_32FC2);
			}
			
			contour.release();
		}

		if(edgels.empty() || edgels.rows() < 100){
			edgels.release();
			return;
		}
		
		RotatedRect ellipse = Imgproc.fitEllipse(edgels);

		edgels.release();
		
		//Correct shift
//		xShift = ((innerRight-innerLeft)+(outerRight-outerLeft) + 2*(SAMPLE_FRAME_SIZE/2 - ellipse.center.x))/6.0f;
//		yShift = ((innerBottom-innerTop)+(outerBottom-outerTop) + 2*(SAMPLE_FRAME_SIZE/2 - ellipse.center.y))/6.0f;
		xShift = SAMPLE_FRAME_SIZE/2.0f - ellipse.center.x;
		yShift = SAMPLE_FRAME_SIZE/2.0f - ellipse.center.y;
		
		lastX -= xShift;
		lastY -= yShift;
		
//		ArrayList<Edgel> edgels = new ArrayList<Edgel>();
//		AlgoTrackEdgels.trackEdgels(frame, edgels, outerTop, SAMPLE_FRAME_SIZE/2);
//		if(!edgels.isEmpty()){
//			EllipseParams p = AlgoEllipseFitting.ellipseFitting(edgels);
//			System.out.println(p.getX());
//		}
		
		//Transform image 
		//TODO: optimize transformation matrices into one before applying the transformation
		Mat M = Mat.zeros(2, 3, CvType.CV_32F);
		M.put(0, 0, new double[] {1,0,xShift,
								  0,1,yShift});
		Imgproc.warpAffine(frame, frame, M, new Size(SAMPLE_FRAME_SIZE, SAMPLE_FRAME_SIZE));
		M.release();
		
//		M = Imgproc.getRotationMatrix2D(new Point(SAMPLE_FRAME_SIZE/2, SAMPLE_FRAME_SIZE/2), -ellipse.angle, 1);
//		Imgproc.warpAffine(frame, frame, M, new Size(SAMPLE_FRAME_SIZE, SAMPLE_FRAME_SIZE));
//		M.release();

		Imgproc.threshold(frame, frame, THRESHOLD, 255, Imgproc.THRESH_BINARY);
		
		float roughAngle = 0;
		float refinedAngle = 0;
		
		roughAngle = findAngleByMaximum(0, (float) (2*Math.PI), ROUGH_STEP, frame);
		refinedAngle = findAngleBySweep(roughAngle, FINE_STEP, frame);
		
		updateAngle(refinedAngle);
		
		//float size = (2*SAMPLE_FRAME_SIZE-outerBottom-outerTop-outerLeft-outerRight)/(float)SAMPLE_FRAME_SIZE;
		
		//Take size with average of W and H
		float size = (float) (ellipse.size.width+ellipse.size.height)/(float)SAMPLE_FRAME_SIZE;
		
		this.lastR = size/2.0f;
		
		M = Imgproc.getRotationMatrix2D(new Point(SAMPLE_FRAME_SIZE/2, SAMPLE_FRAME_SIZE/2), Math.toDegrees(-refinedAngle), 1/(size*0.5f)); //1+(ellipse.size.height/SAMPLE_FRAME_SIZE)*0.5f);
		Imgproc.warpAffine(frame, frame, M, new Size(SAMPLE_FRAME_SIZE, SAMPLE_FRAME_SIZE));
		M.release();
		
//		//Accumulate data
//		if(accumFrame == null) accumFrame = Mat.zeros(SAMPLE_FRAME_SIZE, SAMPLE_FRAME_SIZE, CvType.CV_32FC1);
//		Imgproc.accumulateWeighted(frame, accumFrame, 0.2f);
//		accumFrame.convertTo(frame, CvType.CV_8UC1);
		
		processedTripcode = OpenCVUtils.matToBufferedImage(frame);
		Graphics2D g =  (Graphics2D) processedTripcode.getGraphics();
		
		//int newCode = parseCodeBySweep(frame);
		
		int newCode = parseCodeByRays(frame, g);
		
		if(newCode > 0) code_confirms[last_index] = newCode;
		
	}
	
	private int parseCodeByRays(Mat frame, Graphics2D g){

		char rawData[] = new char[DATA_LENGTH]; 
		char di = 0;
		
		for(float i=(float) (Math.PI*0.36f); i < Math.PI*1.75f; i += (Math.PI*2)/(DATA_LENGTH+2)){

			float xi = (float) (Math.sin(i));
			float yi = (float) (Math.cos(i));

			int x = (int)(xi*25 + SAMPLE_FRAME_SIZE/2.0f);				
			int y = (int)(yi*25 + SAMPLE_FRAME_SIZE/2.0f);
			if(x < 0) x = 0; else if(x >= SAMPLE_FRAME_SIZE) x = SAMPLE_FRAME_SIZE-1;
			if(y < 0) y = 0; else if(y >= SAMPLE_FRAME_SIZE) y = SAMPLE_FRAME_SIZE-1;

			g.setColor(Color.lightGray);
			//g.drawLine(SAMPLE_FRAME_SIZE/2, SAMPLE_FRAME_SIZE/2, x, y);

			float value1 = 0;
			float value2 = 0;
			
			for(int r=13; r < 17; r++){
				x = (int)(xi*r + SAMPLE_FRAME_SIZE/2.0f);				
				y = (int)(yi*r + SAMPLE_FRAME_SIZE/2.0f);
				if(x < 0) x = 0; else if(x >= SAMPLE_FRAME_SIZE) x = SAMPLE_FRAME_SIZE-1;
				if(y < 0) y = 0; else if(y >= SAMPLE_FRAME_SIZE) y = SAMPLE_FRAME_SIZE-1;

				value1 += (float) (frame.get(y, x)[0]);
				g.drawLine(x, y, x, y);
			}
			
			value1 /= 4;
			
			for(int r=19; r < 23; r++){
				x = (int)(xi*r + SAMPLE_FRAME_SIZE/2.0f);				
				y = (int)(yi*r + SAMPLE_FRAME_SIZE/2.0f);
				if(x < 0) x = 0; else if(x >= SAMPLE_FRAME_SIZE) x = SAMPLE_FRAME_SIZE-1;
				if(y < 0) y = 0; else if(y >= SAMPLE_FRAME_SIZE) y = SAMPLE_FRAME_SIZE-1;

				value2 += (float) (frame.get(y, x)[0]);
				g.drawLine(x, y, x, y);
			}
			
			value2 /= 4;
			
			if(value1 < THRESHOLD && value2 > THRESHOLD){
				rawData[di] = 1;
			} else if(value1 > THRESHOLD && value2 < THRESHOLD){
				rawData[di] = 2;
			} else if(value1 < THRESHOLD && value2 < THRESHOLD){
				rawData[di] = 3;
			} else {
				rawData[di] = 0;
			}
			
			di++;
			
		}
		
		if(checksumOK(rawData, 6)){
			int newCode = rawData[0] + rawData[1]*3 + rawData[2]*9 + rawData[3]*27 + rawData[4]*81 + rawData[5]*243;
			return newCode;
		}

		return -1;
	}
	
	private int parseCodeBySweep(Mat frame){
		
		char[] rawData = new char[SCAN_DIVISIONS];
		char di = 0;
		
		//float df = ((outerTop+outerBottom)/(float)(outerLeft+outerRight));
		//float dcos = ((outerLeft+outerRight)/(float)(outerLeft+outerRight));
		
		for(float i=(float) (Math.PI*0.2f); i < Math.PI*2; i += (Math.PI*2)/SCAN_DIVISIONS){
			
			float xi = (float) (Math.sin(i));
			float yi = (float) (Math.cos(i));
	
			int x = (int)(xi*25 + SAMPLE_FRAME_SIZE/2.0f);				
			int y = (int)(yi*25 + SAMPLE_FRAME_SIZE/2.0f);
			if(x < 0) x = 0; else if(x >= SAMPLE_FRAME_SIZE) x = SAMPLE_FRAME_SIZE-1;
			if(y < 0) y = 0; else if(y >= SAMPLE_FRAME_SIZE) y = SAMPLE_FRAME_SIZE-1;
			
			//g.setColor(Color.black);
			//g.drawLine(SAMPLE_FRAME_SIZE/2, SAMPLE_FRAME_SIZE/2, x, y);
			
			//g.setColor(Color.white);
			
			float value1 = 255;
			float value2 = 255;
			
			for(int r=13; r < 16; r++){
				x = (int)(xi*r + SAMPLE_FRAME_SIZE/2.0f);				
				y = (int)(yi*r + SAMPLE_FRAME_SIZE/2.0f);
				if(x < 0) x = 0; else if(x >= SAMPLE_FRAME_SIZE) x = SAMPLE_FRAME_SIZE-1;
				if(y < 0) y = 0; else if(y >= SAMPLE_FRAME_SIZE) y = SAMPLE_FRAME_SIZE-1;
				
				value1 += (float) (frame.get(y, x)[0]);
				//g.drawLine(x, y, x, y);
			}
			value1 /= 3;
			
			for(int r=20; r < 23; r++){
				x = (int)(xi*r + SAMPLE_FRAME_SIZE/2.0f);	
				y = (int)(yi*r + SAMPLE_FRAME_SIZE/2.0f);
				if(x < 0) x = 0; else if(x >= SAMPLE_FRAME_SIZE) x = SAMPLE_FRAME_SIZE-1;
				if(y < 0) y = 0; else if(y >= SAMPLE_FRAME_SIZE) y = SAMPLE_FRAME_SIZE-1;
				
				value2 += (float) (frame.get(y, x)[0]);
				//value2 += (float) (frame.get(y, x)[0]);
				//g.drawLine(x, y, x, y);
			}
			value2 /= 3;
			
			//System.out.println(i + " " +value1 + " " +  value2);
			
			//float value2 = (float) ((frame.get(y, x)[0] + frame.get(y, x+1)[0] + frame.get(y+1, x)[0] + frame.get(y+1, x+1)[0])/4);
			
			if(value1 < THRESHOLD && value2 > THRESHOLD){
				rawData[di] = 1;
			} else if(value1 > THRESHOLD && value2 < THRESHOLD){
				rawData[di] = 2;
			} else if(value1 < THRESHOLD && value2 < THRESHOLD){
				rawData[di] = 3;
			} else {
				rawData[di] = 0;
			}
			
			di++;
			
		}
	
		char[] data = new char[DATA_LENGTH];
		
		int ndi = 0;
		
		//Collapse numbers
		for(di=0; di < SCAN_DIVISIONS; di++){
			if(rawData[di] == 3) continue;
			data[ndi] = rawData[di];			
			while(di < SCAN_DIVISIONS && data[ndi] == rawData[di]){
				di++;
			}
			ndi++;
			if(ndi >= DATA_LENGTH) break;
		}
		
		if(checksumOK(data, ndi)){
			int newCode = data[0] + data[1]*3 + data[2]*9 + data[3]*27 + data[4]*81 + data[5]*243;
			return newCode;
		}
		
		return -1;
		
	}
	
	private boolean checksumOK(char[] data, int lastIndex){
		boolean dataCorrect = true;
		//Correctness check
		if(lastIndex == DATA_LENGTH){
			int ld = -1;
			for(int d : data){
				if(d == ld){
					dataCorrect = false;
					break;
				}
				ld = d;
			}
		} else {
			dataCorrect = false;
		}
		
		return dataCorrect;
	}

	private void updateAngle(float b){
		lastAngle = b;
	}
	
	public boolean isDead(){
		if(System.currentTimeMillis() < timestamp+TIMEOUT) return false;
		return true;
	}

	public void processNewCoordinates(double[] coordinates, Mat m){
		last_index++;
		if(last_index >= BUFFER_SIZE) last_index = 0;

		lastX = coordinates[0];
		lastY = coordinates[1];
		lastR = (float)coordinates[2];
		
		updateImage(m);
		timestamp = System.currentTimeMillis();
	}

	public boolean isCloseTo(double[] coordinates){
		if(Math.abs(getX() - coordinates[0]) <= PROXIMITY && Math.abs(getY() - coordinates[1]) <= PROXIMITY) return true;
		return false;
	}

	public float getRadius() {
		return lastR;
	}

	public BufferedImage getTripcode() {
		Graphics2D g = (Graphics2D) processedTripcode.getGraphics();
		g.setFont(new Font("Serif", Font.PLAIN, 7));
		g.setColor(Color.white);
		g.fillRect(0, 0, 64, 8);
		g.setColor(Color.blue);
		g.drawString("  "+getID(), 2, 8);
		return processedTripcode;
	}

	public int getID() {
		return codemode(code_confirms.clone())[0];
	}
	
	public float getX(){
		return (float)lastX;
	}

	public float getY(){
		return (float)lastY;
	}

	public float getQuality() {
		
		float quality = 1;
		
		int[] c = codemode(code_confirms.clone());
		
		if(c[0] == 0) return 0;
		
		quality -= 0.5f*(c[1]/(float)BUFFER_SIZE);
		
		//quality = 0.5f*(1-separation(r));
		
		//quality -= (separation(x)/30 + separation(y)/30);
		
		quality -= (System.currentTimeMillis() - timestamp)/TIMEOUT;
		
		if(quality < 0) return 0;
		if(quality > 1) return 1;
		return quality;
		
	}
	
	public static float separation(float a[]){
		Arrays.sort(a);
		float min = Integer.MAX_VALUE;
		float max = 0;
		
		for(float i : a){
			if(i < min) min = i;
			else if(i > max) max = i;
		}
		
		return max - min;
	}
	
	public static int[] codemode(int a[]) {
		
		Arrays.sort(a);
		
		int modeNum = a[0];
		int testNum = a[0];
		int curCount = 0;
		int maxCount = 0;
		
		for(int i=1; i < a.length; i++){
			
			if(a[i] == 0) break;
			
			if(a[i] == testNum){
				curCount++;
			} else {
				if(curCount > maxCount){
					modeNum = testNum;
					maxCount = curCount;
				}
				testNum = a[i];
				curCount = 0;
			}
			
			if(maxCount >= a.length-i) break;
		}
		
		return new int[] {modeNum, maxCount};
		
	}
	
	public float getAngle(){
		return lastAngle;
	}

}
