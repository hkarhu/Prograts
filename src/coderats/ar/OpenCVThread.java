package coderats.ar;
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
	
	private SavedParams p;
	private ConcurrentLinkedDeque<Mat> matrixQueue;
	private ConcurrentLinkedDeque<RawTripCircleData> circleQueue;
	private VideoCapture inputVideo;
	private volatile boolean running = true;
	private final Thread workerThread;
	
	OpenCVThread(SavedParams p) {
		this.p = p;
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
		
		Mat in = new Mat();
		
		//Init
		while(inputVideo == null){
			try {
				inputVideo = new VideoCapture();
				inputVideo.open(0);
				Thread.sleep(1000);
				inputVideo.set(OpenCVUtils.CAP_PROP_FRAME_WIDTH, 640);
				inputVideo.set(OpenCVUtils.CAP_PROP_FRAME_HEIGHT, 480);
				inputVideo.set(OpenCVUtils.CAP_PROP_FPS, 60);
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			} catch (Exception e) {
				e.printStackTrace();
				inputVideo = null;
			}
		}

		//Loop
		while(running){
			
			if (inputVideo.read(in)){
				Mat out = new Mat();
				Mat tres = new Mat();
				Mat circ = new Mat();
				
				Imgproc.cvtColor(in, in, Imgproc.COLOR_BGR2GRAY);
				in.convertTo(in, -1, p.par2*0.1f, -p.par1);
				
				//Imgproc.equalizeHist(in, in);
				
				Imgproc.erode(in, out, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3,3)));
				
				//Imgproc.GaussianBlur(hc, hc, new Size(3,3), 51);
				
				//Imgproc.threshold(hc, hc, 90, 255, Imgproc.THRESH_BINARY);
								///tres
				Imgproc.blur(out, tres, new Size(3,3));
				out.convertTo(tres, -1, p.par5*0.1f, -p.par6);
				Imgproc.erode(tres, tres, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5,5)));
				//
				
				//Imgproc.dilate(out, out, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5,5)));
				
				Imgproc.threshold(tres, tres, p.par3, 255, Imgproc.THRESH_BINARY);
				//Imgproc.adaptiveThreshold(tres, tres, 254, Imgproc.THRESH_BINARY, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, 7, 9);
				
				//
				
				//Imgproc.erode(gc, hc, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3,3)));		
			
				//List<MatOfPoint> contours = new LinkedList<MatOfPoint>();
				//Mat hierarchy = new Mat();
				//Imgproc.findContours(hc, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);

				//Imgproc.Canny(gc, hc, 80, 40);
				
				Imgproc.HoughCircles(tres, circ, Imgproc.CV_HOUGH_GRADIENT, 1, tres.height()/16, 80, p.par4, 5, 30);
				
				//Core.drawContours(m, contours, 4, new Scalar(40, 233, 45,0 ));

				for (int i = 0; i < circ.cols(); i++){
					
					double[] coords = circ.get(0,i);
					
					if(coords[0] <= 1 && coords[1] <= 1) continue;
					
					for(RawTripCircleData c : circleQueue){
						if(c.isCloseTo(coords)){
							c.processNewCoordinates(circ.get(0, i), out);
							coords = null;
							break;
						}
					}
					
					if(coords != null){
						circleQueue.add(new RawTripCircleData(coords, out));
					}
					
				}
				
				matrixQueue.addLast(out);

				tres.release();
				circ.release();
				
			} else {
				S.debug("frame/cam failiure!!1!1");
			}

			if(matrixQueue.size() > 10) matrixQueue.removeFirst();
			if(circleQueue.size() > 200) matrixQueue.removeFirst();
			
		}

		//De-init
		inputVideo.release();
		in.release();
		
	}

	public void stop(){
		running = false;
		System.out.println("Stopping OpenCV threads...");
		try {
			workerThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("OK!");
	}

	public Mat getRGBFrame(){
		if(!matrixQueue.isEmpty()){
			return matrixQueue.removeFirst();
		}
		else return null;
	}

	public RawTripCircleData[] getCircles(){
		for(RawTripCircleData c : circleQueue){
			if(c.isDead()) circleQueue.remove(c);
		}
		return circleQueue.toArray(new RawTripCircleData[0]);
	}

	public void setParams(SavedParams p) {
		this.p = p;
	}

}