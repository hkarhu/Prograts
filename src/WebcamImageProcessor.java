import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.objdetect.CascadeClassifier;

import tripcodescanner.TRIPCodeScanner;

public class WebcamImageProcessor extends JFrame {

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	private final TRIPCodeScanner tripScanner = new TRIPCodeScanner();
	private BufferedImage webcamImage;
	private VideoCapture inputVideo;
	private Mat inputVideoFrame = new Mat();
	
	static int imageWidth;
	static int imageHeight;
	
	public WebcamImageProcessor() {
		
		this.setPreferredSize(new Dimension(800,600));
		this.pack();
		this.setVisible(true);

		try {
			inputVideo = new VideoCapture(0);
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("\nRunning DetectFaceDemo");

		// Create a face detector from the cascade file
		CascadeClassifier faceDetector = new CascadeClassifier(getClass().getResource("/lbpcascade_frontalface.xml").getPath());
		Mat image = new Mat();

		if(!inputVideo.isOpened()){
			System.out.println("Error");
		}
		else {
			Mat frame = new Mat();
			while(true){
				if (inputVideo.read(frame)){
					System.out.println("Frame Obtained");
					System.out.println("Captured Frame Width " + 
							frame.width() + " Height " + frame.height());
					System.out.println("OK");
					break;
				}
			}	
		}
		inputVideo.release();

		// Detect faces in the image.
		// MatOfRect is a special container class for Rect.
		MatOfRect faceDetections = new MatOfRect();
		faceDetector.detectMultiScale(image, faceDetections);

		System.out.println(String.format("Detected %s faces", faceDetections.toArray().length));

		// Draw a bounding box around each face.
		for (Rect rect : faceDetections.toArray()) {
			Core.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
		}

		MatOfByte bytemat = new MatOfByte();

		Highgui.imencode(".jpg", image, bytemat);

		byte[] bytes = bytemat.toArray();

		InputStream in = new ByteArrayInputStream(bytes);

		try {
			webcamImage = ImageIO.read(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		repaint();

	}

	private static byte[] getGreyscaleImageData() throws IOException {
		
		final BufferedImage inImage;
		
		inImage = ImageIO.read(
//				new File("TRIP0L(32bit).jpg")
//				new File("TRIP1L(32bit).jpg")
//				new File("TRIP1207L(32bit).jpg")
//				new File("TRIP1234978123L(48bit) - 110010012001211101111111.jpg")
//				new File("TRIP10101010101L(48bit) - 112222001221211110221020.jpg")
//				new File("TRIP10101010101L(48bit) - 112222001221211110221020, rotated.jpg")
//				new File("TRIP10101010101L(48bit) - 112222001221211110221020, transformed.jpg")
				new File("TRIP10101010101L(48bit) - 112222001221211110221020, ringed.jpg")
//				new File("TRIP10101010101L(48bit) - 112222001221211110221020, created.jpg")
//				new File("TRIP10101010101L(48bit) - 112222001221211110221020, created, small.jpg")
//				new File("TRIP10101010101L(48bit) - 112222001221211110221020, created, slanted.jpg")
//				new File("TRIP10101010101L(48bit) - 112222001221211110221020, created, mutilated.jpg")
//				new File("TRIP10460353202L(48bit) - 102222222222222222222222.jpg")
//				new File("TRIP10460353203L(48+bit).jpg")
				);
		
		/*
		 * alternatively:
		 * data[i*imageWidth + j] =  (char)(0.3*rgb[0] + 0.59*rgb[1] + 0.11*rgb[2]);
		 */

		final BufferedImage grayImage = new BufferedImage(inImage.getWidth(), inImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		final Graphics grayImageGraphics = grayImage.getGraphics();
		
		grayImageGraphics.drawImage(inImage, 0, 0, null);
		grayImageGraphics.dispose();
		
		imageWidth = grayImage.getWidth();
		imageHeight = grayImage.getHeight();

		return ((DataBufferByte)grayImage.getRaster().getDataBuffer()).getData();
		
	}

	@Override
	public void paint(Graphics g) {
		g.setColor(Color.black);
		g.fillRect(0, 0, getWidth(), getHeight());
	}

}
