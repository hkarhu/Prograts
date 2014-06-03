package coderats.ar.videoproc;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import coderats.ar.ARCardListener;
import coderats.ar.SavedParams;
import coderats.ar.gl.GLValues;
import coderats.ar.objects.ARCard;

public class WebcamImageProcessor extends JFrame implements MouseListener, MouseMotionListener {

	public static void main(String[] args) {
		new WebcamImageProcessor();
	}
	
	private JLabel label;
	
	private final ConcurrentHashMap<Integer, ARCard> knownCards;
	private final List<ARCardListener> listeners;
	
	private BufferedImage cardTrackerDebug;
	private Graphics cardTrackerDebugG;
	
	private BufferedImage webcamDebug;
	private Graphics2D webcamDebugG;
	
	private int wDebugXShift = 192;
	private int wDebugYShift = 24;
	
	int movingCP = -1;
	
	private final OpenCVThread ocvt;
	
	private SavedParams p;
	
	public WebcamImageProcessor() {

		try {
			FileInputStream fileIn = new FileInputStream("calibration.ser");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			p = (SavedParams) in.readObject();
			in.close();
			fileIn.close();

		} catch(Exception e) {
			e.printStackTrace();
			p = new SavedParams();
		}

		listeners = new Vector<>();
		
		addMouseListener(this);
		addMouseMotionListener(this);
		
		cardTrackerDebug = new BufferedImage(64*3, 64*8+24, BufferedImage.TYPE_3BYTE_BGR);
		webcamDebug = new BufferedImage(p.imageWidth, p.imageHeight, BufferedImage.TYPE_4BYTE_ABGR);
		
		cardTrackerDebugG = cardTrackerDebug.getGraphics();
		webcamDebugG = (Graphics2D) webcamDebug.getGraphics();
		
		knownCards = new ConcurrentHashMap<>();
		
		ocvt = new OpenCVThread(p);
		
		Timer t = new Timer(25, new ActionListener() {
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
		
		this.setLayout(null);
		
		Insets i = this.getInsets();
		
		JSlider slider = new JSlider(JSlider.VERTICAL, 0, 5, p.dbg);
		slider.setBounds(wDebugXShift + i.left + 640 + 16, 16 + i.top, 32, 124);
		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				p.dbg = ((JSlider)e.getSource()).getValue();
				refreshLabel();
			}
		});
		this.add(slider);
		
		slider = new JSlider(JSlider.HORIZONTAL, 1, 255, p.par1);
		slider.setBounds(192 + i.left, 480 + i.top, 200, 24);
		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				p.par1 = ((JSlider)e.getSource()).getValue();
				refreshLabel();
			}
		});
		this.add(slider);
		
		slider = new JSlider(JSlider.HORIZONTAL, 1, 255, p.par2);
		slider.setBounds(192 + i.left, 510 + i.top, 200, 24);
		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				p.par2 = ((JSlider)e.getSource()).getValue();
				refreshLabel();
			}
		});
		this.add(slider);
		
		slider = new JSlider(JSlider.HORIZONTAL, 2, 255, p.par3);
		slider.setBounds(392 + i.left, 480 + i.top, 200, 24);
		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				p.par3 = ((JSlider)e.getSource()).getValue();
				refreshLabel();
			}
		});
		this.add(slider);
		
		slider = new JSlider(JSlider.HORIZONTAL, 0, 255, p.par4);
		slider.setBounds(392 + i.left, 510 + i.top, 200, 24);
		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				p.par4 = ((JSlider)e.getSource()).getValue();
				refreshLabel();
			}
		});
		this.add(slider);
	
		slider = new JSlider(JSlider.HORIZONTAL, 2, 255, p.par5);
		slider.setBounds(592 + i.left, 480 + i.top, 200, 24);
		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				p.par5 = ((JSlider)e.getSource()).getValue();
				refreshLabel();
			}
		});
		this.add(slider);
		
		slider = new JSlider(JSlider.HORIZONTAL, 0, 255, p.par6);
		slider.setBounds(592 + i.left, 510 + i.top, 200, 24);
		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				p.par6 = ((JSlider)e.getSource()).getValue();
				refreshLabel();
			}
		});
		this.add(slider);
	
		
		JButton save = new JButton("Save");
		save.setBounds(i.left, 520+i.top, 130, 24);
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					FileOutputStream fileOut = new FileOutputStream("calibration.ser");
					ObjectOutputStream out = new ObjectOutputStream(fileOut);
					out.writeObject(p);
					out.close();
					fileOut.close();
					System.out.printf("Serialized calibration.");
				} catch (IOException ex){
					ex.printStackTrace();
				}
				
			}
		});
		this.add(save);
		
		label = new JLabel("");
		label.setBounds(192 + i.left, 540 + i.top, 600, 24);
		this.add(label);
		
		this.setPreferredSize(new Dimension(920,600));
		this.pack();
		this.setVisible(true);
		
	}
	
	private void refreshLabel(){
		label.setText(" Par1: " + p.par1 + "       BR2 : " + p.par2 + "       CDT : " + p.par3 + "       LT : " + p.par4 + "       5 : " + p.par5 + "       6 : " + p.par6);
	}
	
	public ConcurrentHashMap<Integer, ARCard> getKnownCards(){
		return knownCards;
	}

	protected void processFrame() {


		BufferedImage dbgimg = ocvt.getDebugFrame();
		if(dbgimg  != null){
			webcamDebugG.drawImage(dbgimg, 0, 0, null);
		} else {
			webcamDebugG.setColor(Color.darkGray);
			webcamDebugG.fillRect(0, 0, webcamDebug.getWidth(), webcamDebug.getHeight());
		}
		
		int i=0;
		
		for(RawTripCircleData c : ocvt.getCircles()){
			if(c == null) continue;

			if(knownCards.containsKey(c.getID())){
				knownCards.get(c.getID()).updateValues(getCalibratedGLX(c.getX(),c.getY()), getCalibratedGLY(c.getX(),c.getY()), (float)c.getAngle(), (float)c.getRadius(), c.getQuality());
				informListenersCardUpdated(c.getID());
			} else {
				knownCards.put(c.getID(), new ARCard(getCalibratedGLX(c.getX(),c.getY()), getCalibratedGLY(c.getX(),c.getY()), (float)c.getAngle(), (float)c.getRadius(), c.getID(), c.getQuality()));
				informListenersCardAppeared(c.getID());
			}

			
			webcamDebugG.setColor(Color.getHSBColor(c.getQuality()*0.3f, 1, 1));
			//webcamDebugG.drawOval((int)(c.getX()-25), (int) (c.getY()-25), 50, 50);
			webcamDebugG.setStroke(new BasicStroke(3));
			webcamDebugG.drawOval((int)(c.getX()-c.getRadius()), (int)(c.getY()-c.getRadius()), (int)(c.getRadius()*2), (int)(c.getRadius()*2));

			webcamDebugG.setColor(Color.cyan);
			webcamDebugG.drawLine(p.CPs[0][0],p.CPs[0][1], p.CPs[1][0], p.CPs[1][1]);
			webcamDebugG.drawLine(p.CPs[1][0],p.CPs[1][1], p.CPs[2][0], p.CPs[2][1]);
			webcamDebugG.drawLine(p.CPs[2][0],p.CPs[2][1], p.CPs[3][0], p.CPs[3][1]);
			webcamDebugG.drawLine(p.CPs[3][0],p.CPs[3][1], p.CPs[0][0], p.CPs[0][1]);

			int dbgx = (i/8)*64;
			int dbgy = (i%8)*64+24;

			cardTrackerDebugG.drawImage(c.getTripcode(),dbgx,dbgy,null);

			if(c.getQuality() >= 0.75f){
				cardTrackerDebugG.setColor(Color.green);
				cardTrackerDebugG.fillOval(dbgx+26, dbgy+26, 12, 12);
			}
			
			i++;
		}
		repaint();

	}

	@Override
	public void paint(Graphics g) {
		g.drawImage(cardTrackerDebug,0,0,null);
		//g.drawImage(webcamImage,64*3,24, null);
		g.drawImage(webcamDebug, wDebugXShift, wDebugYShift, null);
	}

	public void shutdown() {
		ocvt.stop();
		this.dispose();
	}

	public void addListener(ARCardListener l){
		listeners.add(l);
	}
	
	private void informListenersCardUpdated(int id){
		for(ARCardListener l : listeners){
			l.cardDataUpdated(id);
		}
	}
	
	private void informListenersCardAppeared(int id){
		for(ARCardListener l : listeners){
			l.cardAppeared(id);
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {

		if(e.getX() < p.imageWidth/2){
			if(e.getY() < p.imageHeight/2){
				movingCP = 0;
			} else {
				movingCP = 3;
			}
		} else {
			if(e.getY() < p.imageHeight/2){
				movingCP = 1;
			} else {
				movingCP = 2;
			}
		}
		
		setCalibrationRectPointCoords(e);
		
	}

	private void setCalibrationRectPointCoords(MouseEvent e){
		if(movingCP >= 0){
			p.CPs[movingCP][0] = e.getX() - wDebugXShift;
			p.CPs[movingCP][1] = e.getY() - wDebugYShift;
		}
	}
	
	private float getCalibratedGLX(float x, float y){
		
		float ymo = (y-(p.CPs[0][1]+p.CPs[3][1])/2.0f)/(float)(p.CPs[3][1]-p.CPs[0][1]);
		float ymi = 1-ymo;
		
		return (((x-p.CPs[0][0])/(float)(p.CPs[1][0]-p.CPs[0][0]))*ymi + 
				((x-p.CPs[3][0])/(float)(p.CPs[2][0]-p.CPs[3][0]))*ymo
				)*GLValues.glWidth;
		//return (1+(imageWidth-CPs[0][0]-CPs[1][0])/(float)imageWidth)*((x-CPs[0][0])/(float)imageWidth)*GLValues.glWidth;
	}
	
	private float getCalibratedGLY(float x, float y){
		float xmo = (x-(p.CPs[0][0]+p.CPs[1][0])/2.0f)/(float)(p.CPs[1][0]-p.CPs[0][0]);
		float xmi = 1-xmo;
		
		return (((y-p.CPs[0][1])/(float)(p.CPs[3][1]-p.CPs[0][1]))*xmi + 
				((y-p.CPs[1][1])/(float)(p.CPs[2][1]-p.CPs[1][1]))*xmo
				)*GLValues.glHeight;
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		movingCP = -1;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		setCalibrationRectPointCoords(e);
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mouseMoved(MouseEvent e) {} 
	
}