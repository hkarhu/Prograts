package tripcardgenarator;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class GenerateARCards extends JFrame {
	
	public static void main(String[] args) {
		new GenerateARCards();
	}

	private final int TRIPCODE_SIZE = 250;
	private final int CARD_WIDTH = 620;
	private final int CARD_HEIGHT = 880;
	private final int CARD_BOUNDS = 10;
	private final int CARD_CORNER_RADIUS = 60;
	
	private BufferedImage cardImage;
	
	public GenerateARCards() {
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setPreferredSize(new Dimension(CARD_WIDTH+32, CARD_HEIGHT+64));
		this.pack();
		this.setVisible(true);
		
		cardImage = new BufferedImage(CARD_WIDTH, CARD_HEIGHT,BufferedImage.TYPE_3BYTE_BGR);
		int code = 0;
		
		while(true){
			code += 1;
			Graphics2D cardGraphics = (Graphics2D)cardImage.getGraphics();
			drawARCard(cardGraphics, code);
			repaint();
			System.out.println("code " + code);
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	private void drawARCard(Graphics2D g, int cardCode){
		
		g.setColor(Color.black);
		g.fillRect(0, 0, CARD_WIDTH, CARD_HEIGHT);

		g.setColor(Color.white);
		g.fillRect(CARD_BOUNDS, CARD_BOUNDS, CARD_WIDTH-CARD_BOUNDS*2, CARD_HEIGHT-CARD_BOUNDS*2);
		
		g.setColor(Color.black);
		Arc2D.Double ring = new Arc2D.Double();
		g.setStroke(new BasicStroke(CARD_BOUNDS));
		ring.setArc(CARD_BOUNDS/2-1, CARD_BOUNDS/2-1, CARD_CORNER_RADIUS, CARD_CORNER_RADIUS, 90, 90, Arc2D.OPEN);
		g.draw(ring);
		ring.setArc(CARD_WIDTH-CARD_BOUNDS/2-CARD_CORNER_RADIUS, CARD_BOUNDS/2-1, CARD_CORNER_RADIUS, CARD_CORNER_RADIUS, 0, 90, Arc2D.OPEN);
		g.draw(ring);
		ring.setArc(CARD_BOUNDS/2-1, CARD_HEIGHT-CARD_BOUNDS/2-CARD_CORNER_RADIUS, CARD_CORNER_RADIUS, CARD_CORNER_RADIUS, 180, 90, Arc2D.OPEN);
		g.draw(ring);
		ring.setArc(CARD_WIDTH-CARD_BOUNDS/2-CARD_CORNER_RADIUS, CARD_HEIGHT-CARD_BOUNDS/2-CARD_CORNER_RADIUS, CARD_CORNER_RADIUS, CARD_CORNER_RADIUS, 270, 90, Arc2D.OPEN);
		g.draw(ring);
		
		g.drawString("CID:" + cardCode, CARD_CORNER_RADIUS/2, CARD_HEIGHT - CARD_CORNER_RADIUS/2);
		TRIPCodeGenerator.drawTRIPcode((int)(CARD_WIDTH/2 - TRIPCODE_SIZE - TRIPCODE_SIZE*0.076f), CARD_BOUNDS*3, TRIPCODE_SIZE, TRIPCodeGenerator.encodeToTRIPcode(cardCode, 4), g);
		
		
		//g.setColor(Color.white);
		g.setStroke(new BasicStroke(40));
		g.drawLine(CARD_WIDTH/2, (int)(CARD_HEIGHT/2-TRIPCODE_SIZE/2 - TRIPCODE_SIZE*0.076f), (int)(TRIPCODE_SIZE*2.2f), (int)(CARD_HEIGHT/2-TRIPCODE_SIZE/2 - TRIPCODE_SIZE*0.076f)-40);
		
	}
	
	@Override
	public void paint(Graphics g) {
		g.drawImage(cardImage, 16, 32, CARD_WIDTH, CARD_HEIGHT, null);		
	}
	
}
