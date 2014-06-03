package coderats.ar.gameScenes;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.lwjgl.opengl.GL11;

import coderats.ar.gl.GLBitmapFontBlitter;
import coderats.ar.gl.GLGraphicRoutines;
import coderats.ar.gl.GLTextureManager;
import coderats.ar.gl.GLValues;
import coderats.ar.objects.ARCard;
import coderats.ar.objects.Command;

public class AllocateScene extends GameScene {

	private static final int ALLOCATE_TIME = 20000;
	private long allocateTimer;
	private long exitTime;
	private AllocateHalf p1Allocate;
	private AllocateHalf p2Allocate;
	private boolean allocate_broken = true;
	
	private ConcurrentHashMap<Integer, ARCard> knownCards;
	private ConcurrentHashMap<Integer, ARCard> p1Cards, p2Cards;

	public AllocateScene(ConcurrentHashMap<Integer, ARCard> knownCards, ConcurrentHashMap<Integer, ARCard> p1Cards, ConcurrentHashMap<Integer, ARCard> p2Cards) {
		
		this.knownCards = knownCards;
		
		p1Allocate = new AllocateHalf(true);
		p2Allocate = new AllocateHalf(false);
		allocateTimer = -1;
		exitTime = -1;
		
		this.p1Cards = p1Cards;
		this.p2Cards = p2Cards;
		
	}

	@Override
	public void init() {
		setRunning(true);
		exitTime = -1;
		allocateTimer = -1;
		p1Allocate.activate(100);
		p2Allocate.activate(100);
		p1Cards.clear();
		p2Cards.clear();
	}

	@Override
	public void glDraw(long time) {

		p1Allocate.glDraw(time);
		p2Allocate.glDraw(time);

		if(knownCards.size() >= 1){
			for(Entry<Integer, ARCard> c : knownCards.entrySet()){
				ARCard card = c.getValue();
				if(card.getQuality() >= 0.8f) card.glDraw(time);
			}
		}
		
		GLTextureManager.unbindTexture();

		if(time < 3000){
			float in = (3000-time)/3000.0f;
			GL11.glColor4f(1, 1, 1, in);
			
			GL11.glTranslatef(GLValues.glWidth*0.5f, GLValues.glHeight*0.5f, -5);
			GLBitmapFontBlitter.blitSinString("ALLOCATE      ALLOCATE      ", 0.5f, 0.9f, 1, 1+3*(1-in), 0.9f+(float)Math.sin(time*0.003f)*0.2f, "font_code");
			
			allocateTimer = time+ALLOCATE_TIME;

		} else {

			float q = 1-(allocateTimer-time)/(float)ALLOCATE_TIME;

			GLTextureManager.unbindTexture();
			GL11.glColor4f(1, 1, 1, 1);
			if(exitTime == -1){
				if (q >= 1){
					p1Allocate.deactivate(time);
					p2Allocate.deactivate(time);
					exitTime = time + 501;
				} else if(q > 0){
					GL11.glPushMatrix();
						GLGraphicRoutines.drawLineRect(1.0f, GLValues.glWidth*0.49f, GLValues.glHeight*0.15f, GLValues.glWidth*0.51f, GLValues.glHeight*0.85f, 0);
						GLGraphicRoutines.draw2DRect(GLValues.glWidth*0.492f, GLValues.glHeight*(0.154f + 0.346f*q), GLValues.glWidth*0.508f, GLValues.glHeight*(0.846f - 0.354f*q), 0);
						
						GL11.glPushMatrix();
							GL11.glTranslatef(GLValues.glWidth*0.5f, GLValues.glHeight*0.075f, 0);
							GL11.glRotatef(90, 0, 0, 1);
							GLBitmapFontBlitter.drawString(999-(int)(999*q)+"", "font_code", GLValues.glWidth*0.02f, GLValues.glWidth*0.02f, GLBitmapFontBlitter.Alignment.CENTERED);
						GL11.glPopMatrix();
						
						GL11.glPushMatrix();
							GL11.glTranslatef(GLValues.glWidth*0.5f, GLValues.glHeight*0.925f, 0);
							GL11.glRotatef(270, 0, 0, 1);
							GLBitmapFontBlitter.drawString(999-(int)(999*q)+"", "font_code", GLValues.glWidth*0.02f, GLValues.glWidth*0.02f, GLBitmapFontBlitter.Alignment.CENTERED);
						GL11.glPopMatrix();
		
					GL11.glPopMatrix();
				}
				
			} else if(time >= exitTime) setRunning(false);
		}

	}

	@Override
	public void processInput(int inputKey) {
		if(inputKey == 19) setRunning(false);
	}

	@Override
	public void cardDataUpdated(int id) {
		if(!isRunning()) return;
		ARCard c = knownCards.get(id);
		
		if(c.getQuality() < 0.8f){
			System.out.println("Allocate: Removed low quality card " + c.getID());
			p1Cards.remove(id);
			p2Cards.remove(id);
			return;
		}
		
		if(c.isBroken() && !allocate_broken) return; //Don't allocate if the card is broken.
		
		if(c.getX() < GLValues.glWidth/2){
			//P2 Cards
			if(c.getY() < GLValues.glHeight/2){
				if(c.getX() < GLValues.glWidth*0.25f){
					c.setCommand(new Command(Command.Type.PEW));
				} else {
					c.setCommand(new Command(Command.Type.ROL));
				}
			} else {
				if(c.getX() < GLValues.glWidth*0.25f){
					c.setCommand(new Command(Command.Type.FWD));
				} else {
					c.setCommand(new Command(Command.Type.ROR));
				}
			}
			
			p1Cards.remove(id);
			p2Cards.put(id, c);
			
		} else {
			//P1 Cards
			if(c.getY() > GLValues.glHeight/2){
				if(c.getX() > GLValues.glWidth*0.75f){
					c.setCommand(new Command(Command.Type.PEW));
				} else {
					c.setCommand(new Command(Command.Type.ROL));
				}
			} else {
				if(c.getX() > GLValues.glWidth*0.75f){
					c.setCommand(new Command(Command.Type.FWD));
				} else {
					c.setCommand(new Command(Command.Type.ROR));
				}
			}
			
			p2Cards.remove(id);
			p1Cards.put(id, c);
			
		}
		
	}

	@Override
	public void cardAppeared(int id) {
		cardDataUpdated(id);
	}
}