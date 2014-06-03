package coderats.ar.videoproc;
import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

import coderats.ar.SavedParams;
import coderats.ar.gl.S;

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
		
		int vi = 0;
		
		//Init
		while(inputVideo == null){
			try {
				inputVideo = new VideoCapture();
				System.out.println("Trying input " + vi);
				inputVideo.open(vi);
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
				vi++;
				if(vi > 5) vi = 0;
			}
		}

		//Loop
		while(running){
			try {
			if (inputVideo.read(in)){
				Mat out = new Mat();
				Mat dbg = null;
				Mat tres = new Mat();
				Mat circ = new Mat();
				
				Imgproc.cvtColor(in, in, Imgproc.COLOR_BGR2GRAY);
				in.convertTo(in, -1, p.par2*0.05f, -p.par1);
				if(p.dbg == 0) dbg = in.clone();
				
				Imgproc.erode(in, out, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3,3)));
				Imgproc.threshold(out, out, p.par3, 255, Imgproc.THRESH_BINARY);
				if(p.dbg == 1) dbg = out.clone();
				
				Imgproc.blur(out, tres, new Size(3,3));
				//out.convertTo(tres, -1, p.par5*0.1f, -p.par6);
				Imgproc.erode(tres, tres, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3,3)));
				if(p.dbg == 2) dbg = tres.clone();
				Imgproc.threshold(tres, tres, 128, 255, Imgproc.THRESH_BINARY);
				
				//Imgproc.GaussianBlur(tres, tres, new Size(3,3), p.par3);
				//Imgproc.dilate(out, out, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5,5)));
				//Imgproc.erode(gc, hc, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3,3)));		
				//Imgproc.adaptiveThreshold(tres, tres, 254, Imgproc.THRESH_BINARY, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, 7, p.par5);
				
				if(p.dbg == 3) dbg = tres.clone();
				Imgproc.HoughCircles(tres, circ, Imgproc.CV_HOUGH_GRADIENT, 1, tres.height()/8, 80, 1+p.par4, p.par5, p.par6);
				
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
				
				if(dbg != null) matrixQueue.addLast(dbg);

				tres.release();
				circ.release();
				
			} else {
				S.debug("frame/cam failiure!!1!1");
			}

			if(matrixQueue.size() > 5) matrixQueue.removeFirst();
			if(circleQueue.size() > 200) circleQueue.removeFirst();
			
			} catch(Exception e){
				e.printStackTrace();
				running = false;
			}
			
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

	public BufferedImage getDebugFrame(){
		Mat dbg = null;
		BufferedImage bi = null;
		if(!matrixQueue.isEmpty()){
			dbg = matrixQueue.removeFirst();
			bi = OpenCVUtils.matToBufferedImage(dbg);
			dbg.release();
		} 
		
		return bi;
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
	
	public void setDebugMode(int i){
		p.dbg = i;
	}

}