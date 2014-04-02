package tripcardgenarator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class GenerateARCards extends JFrame {
	
	public static void main(String[] args) {
		new GenerateARCards();
	}

	private final int TRIPCODE_SIZE = 196;
	private final int CARD_WIDTH = 768;
	private final int CARD_HEIGHT = 1024;
	
	private BufferedImage cardImage;
	
	public GenerateARCards() {
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
		this.pack();
		this.setVisible(true);
		
		cardImage = new BufferedImage(CARD_WIDTH, CARD_HEIGHT,BufferedImage.TYPE_3BYTE_BGR);
		
		TRIPCodeGenerator.drawTRIPcode(TRIPCODE_SIZE/2+TRIPCODE_SIZE/3, 64, TRIPCODE_SIZE, TRIPCodeGenerator.encodeToTRIPcode(0, 24), (Graphics2D)cardImage.getGraphics());
		
	}
	
	@Override
	public void paint(Graphics g) {

		g.setColor(Color.white);
		g.fillRect(0, 0, CARD_WIDTH, CARD_HEIGHT);
		g.setColor(Color.black);
		g.drawImage(cardImage, 0, 0, CARD_WIDTH, CARD_HEIGHT, null);
		
	}
	
}
