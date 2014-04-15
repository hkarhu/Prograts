package coderats.ar;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JFrame;
import javax.swing.Timer;

import org.opencv.core.Mat;

public class WebcamImageProcessor extends JFrame {

	public static void main(String[] args) {
		new WebcamImageProcessor();
	}
	
	private ConcurrentHashMap<Integer, ARCard> knownCards;
	private BufferedImage webcamImage;
	private BufferedImage cardTrackerDebug;
	private Graphics cardTrackerDebugG;
	
	static int imageWidth;
	static int imageHeight;

	private final OpenCVThread ocvt;
	
	public WebcamImageProcessor() {
		
		cardTrackerDebug = new BufferedImage(64*3, 64*8+24, BufferedImage.TYPE_3BYTE_BGR);
		
		knownCards = new ConcurrentHashMap<>();
		
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
				shutdown();
			}
		});
		this.setPreferredSize(new Dimension(900,600));
		this.pack();
		this.setVisible(true);

		cardTrackerDebugG = cardTrackerDebug.getGraphics();
		
	}
	
	public ConcurrentHashMap<Integer, ARCard> getKnownCards(){
		return knownCards;
	}

	protected void processFrame() {
		
		cardTrackerDebug.getGraphics().setColor(Color.black);
		cardTrackerDebug.getGraphics().fillRect(0, 0, cardTrackerDebug.getWidth(), cardTrackerDebug.getHeight());
		
		Mat m = ocvt.getRGBFrame();
		if(m != null){
			webcamImage = OpenCVUtils.matToBufferedImage(m);
			int i=0;
			for(RawTripCircleData c : ocvt.getCircles()){
				if(c == null) continue;
				
				if(c.getQuality() >= 1 && knownCards.containsKey(c.getID())){
					knownCards.get(c.getID()).updateValues(c.getX(), c.getY(), c.getAngle());
				} else {
					knownCards.put(c.getID(), new ARCard(c.getX(), c.getY(), c.getAngle(), c.getID()));
				}
				
				Graphics2D g = (Graphics2D) webcamImage.getGraphics();
				g.setColor(Color.green);
				g.drawOval(c.getX()-25, c.getY()-25, 50, 50);
				//g.setStroke(new BasicStroke(3));
				//g.fillOval(c.getX()-c.getRadius(), c.getY()-c.getRadius(), c.getRadius()*2, c.getRadius()*2);
				
				int dbgx = (i/8)*64;
				int dbgy = (i%8)*64+24; 
				
				cardTrackerDebugG.drawImage(c.getTripcode(),dbgx,dbgy,null);

				if(c.getQuality() >=0.8f){
					cardTrackerDebugG.setColor(Color.green);
					cardTrackerDebugG.fillOval(dbgx+26, dbgy+26, 12, 12);
				} 
				i++;
			}
			repaint();
			m.release();
		}
		
	}

	@Override
	public void paint(Graphics g) {
		g.setColor(Color.black);
		g.drawImage(cardTrackerDebug,0,0,null);
		g.drawImage(webcamImage,64*3,24, null);
	}

	public void shutdown() {
		ocvt.stop();
		this.dispose();
	}
	
}