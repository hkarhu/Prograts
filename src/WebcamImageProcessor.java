import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.Timer;

import org.opencv.core.Mat;

import tripcodescanner.TRIPCodeScanner;
import ae.routines.S;

public class WebcamImageProcessor extends JFrame {

	public static void main(String[] args) {
		new WebcamImageProcessor();
	}
	
	private final TRIPCodeScanner tripScanner = new TRIPCodeScanner();
	private BufferedImage webcamImage;
	private BufferedImage cardTrackerDebug;

	static int imageWidth;
	static int imageHeight;

	private final OpenCVThread ocvt;
	
	public WebcamImageProcessor() {
		
		cardTrackerDebug = new BufferedImage(64, 600, BufferedImage.TYPE_3BYTE_BGR);
		
		ocvt = OpenCVThread.start();
		
		Timer t = new Timer(20, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				processFrame();
			}
		});
		
		t.setRepeats(true);
		t.start();
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				ocvt.stop();
			}
		});
		this.setPreferredSize(new Dimension(800,600));
		this.pack();
		this.setVisible(true);

	}

	protected void processFrame() {
		
		Mat m = ocvt.getRGBFrame();
		if(m != null){
			webcamImage = OpenCVUtils.matToBufferedImage(m);
			int i=0;
			for(TripCircle c : ocvt.getCircles()){
				if(c == null) continue;
				Graphics2D g = (Graphics2D) webcamImage.getGraphics();
				g.setColor(Color.green);
				g.drawOval(c.getX()-25, c.getY()-25, 50, 50);
				//g.setStroke(new BasicStroke(3));
				//g.fillOval(c.getX()-c.getRadius(), c.getY()-c.getRadius(), c.getRadius()*2, c.getRadius()*2);
				cardTrackerDebug.getGraphics().drawImage(c.getTripcode(),0,i*64+32,null);
				i++;
			}
			repaint();
		}
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
		g.drawImage(cardTrackerDebug,0,0,null);
		g.drawImage(webcamImage,64,0, null);
	}
	
}
