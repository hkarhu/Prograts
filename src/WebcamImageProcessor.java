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
import java.util.LinkedList;
import java.util.List;

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
	
	private BufferedImage webcamImage;
	private BufferedImage cardTrackerDebug;

	private List<ARCardListener> listeners;
	
	static int imageWidth;
	static int imageHeight;

	private final OpenCVThread ocvt;
	
	public WebcamImageProcessor() {
		
		listeners = new LinkedList();
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
		
		cardTrackerDebug.getGraphics().setColor(Color.black);
		cardTrackerDebug.getGraphics().fillRect(0, 0, cardTrackerDebug.getWidth(), cardTrackerDebug.getHeight());
		
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

	@Override
	public void paint(Graphics g) {
		g.setColor(Color.black);
		g.drawImage(cardTrackerDebug,0,0,null);
		g.drawImage(webcamImage,64,0, null);
	}

	public void addListener(ARCardListener listener) {
		listeners.add(listener);
	}

	public void shutdown() {
		ocvt.stop();
		this.dispose();
	}
	
}