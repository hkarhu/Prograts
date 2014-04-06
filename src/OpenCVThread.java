import java.util.concurrent.ConcurrentLinkedDeque;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

import ae.routines.S;

public class OpenCVThread {

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	private ConcurrentLinkedDeque<Mat> matrixQueue;
	private ConcurrentLinkedDeque<TripCircle> circleQueue;
	private VideoCapture inputVideo;
	private volatile boolean running = true;
	private final Thread workerThread;

	private OpenCVThread() {
		matrixQueue = new ConcurrentLinkedDeque<>();
		circleQueue = new ConcurrentLinkedDeque<>();
		workerThread = new Thread(){
			@Override
			public void run() {
				processData();
			}
		};
		workerThread.start();
	}

	protected void processData() {
		//Init
		try {
			inputVideo = new VideoCapture(0);
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		//Loop
		while(running){
			Mat m = new Mat();

			if (inputVideo.read(m)){

				Mat hc = new Mat();
				Mat gc = new Mat();
				Mat circles = new Mat();
				
				Imgproc.cvtColor(m, gc, Imgproc.COLOR_BGR2GRAY);
				gc.convertTo(gc, -1, 2.2f, -50);
				
				Imgproc.erode(gc, gc, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3,3)));
				
				//Imgproc.equalizeHist(gc, hc);
				
				//Imgproc.GaussianBlur(hc, hc, new Size(3,3), 51);

				//Imgproc.adaptiveThreshold(gc, gc, 255, Imgproc.THRESH_BINARY, Imgproc.ADAPTIVE_THRESH_MEAN_C, 5, 9);
				
				//Imgproc.threshold(hc, hc, 90, 255, Imgproc.THRESH_BINARY);
				Imgproc.blur(gc, hc, new Size(3,3));
				Imgproc.adaptiveThreshold(hc, hc, 254, Imgproc.THRESH_BINARY, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, 7, 11);
				
				//Imgproc.dilate(hc, hc, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3,3)));
				//Imgproc.dilate(hc, hc, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3,3)));
				//Imgproc.erode(gc, hc, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3,3)));		
			
				//List<MatOfPoint> contours = new LinkedList<MatOfPoint>();
				//Mat hierarchy = new Mat();
				//Imgproc.findContours(hc, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);

				//Imgproc.Canny(gc, hc, 80, 40);
				Imgproc.HoughCircles(hc, circles, Imgproc.CV_HOUGH_GRADIENT, 1, m.height()/6, 80, 25, 5, 35);

				//Core.drawContours(m, contours, 4, new Scalar(40, 233, 45,0 ));

				for (int i = 0; i < circles.cols(); i++){
					
					double[] coords = circles.get(0,i);
					
					for(TripCircle c : circleQueue){
						if(c.isCloseTo(coords)){
							c.processNewCoordinates(circles.get(0, i), gc);
							coords = null;
							break;
						}
					}
					
					if(coords != null){
						circleQueue.add(new TripCircle(coords, gc));
					}
					
				}

				matrixQueue.addLast(gc);

			} else {
				S.debug("frame/cam failiure!!1!1");
			}

			if(matrixQueue.size() > 10) matrixQueue.removeFirst();
			if(circleQueue.size() > 200) matrixQueue.removeFirst();

		}

		//De-init
		inputVideo.release();

	}

	static public OpenCVThread start(){
		return new OpenCVThread();
	}

	public void stop(){
		running = false;
	}

	public Mat getRGBFrame(){
		if(!matrixQueue.isEmpty()) return matrixQueue.removeFirst();
		else return null;
	}

	public TripCircle[] getCircles(){
		for(TripCircle c : circleQueue){
			if(c.isDead()) circleQueue.remove(c);
		}
		return circleQueue.toArray(new TripCircle[0]);
	}

}