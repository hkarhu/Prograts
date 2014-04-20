package coderats.ar.gameScenes;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

import javax.swing.text.html.parser.Entity;

import org.lwjgl.opengl.GL11;

import coderats.ar.ARCard;
import ae.gl.GLGraphicRoutines;
import ae.gl.GLValues;
import ae.gl.text.GLBitmapFontBlitter;

public class IntroScene extends GameScene {

	private ParticleCloud particleCloud;
	final static int EXIT_DURATION = 1500;
	
	private long exitTime = -1;
	private long currentTime = -1;
	private boolean exit = false;
	
	private ConcurrentHashMap<Integer, ARCard> knownCards;

	public IntroScene(ConcurrentHashMap<Integer, ARCard> knownCards) {

		particleCloud = new ParticleCloud();
		particleCloud.setNumParticles(2000);

		this.knownCards = knownCards;
		
		System.out.println(knownCards.size());
		
		for(int i=0; i<particleCloud.getNumParticles(); i++) particleCloud.advance();

	}

	@Override
	public void init(){
		exit = false;
		exitTime = -1;
	}

	@Override
	public void glDraw(long time) {

		if(exitTime == -1){
			GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
			GLGraphicRoutines.drawRepeatedBackgroundPlane(-GLValues.glWidth*0.49f, -GLValues.glHeight*0.49f, GLValues.glWidth*0.49f, GLValues.glHeight*0.49f);
			GL11.glPushMatrix();
	
				GL11.glColor3f(0, 0.5f, 0);
		
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, -1);
				GL11.glTranslatef(GLValues.glWidth*0.5f, GLValues.glHeight*0.5f, 0);
		
				GL11.glPushMatrix();
					particleCloud.advance();
					for(Particle p : particleCloud.getParticles()){
						GL11.glColor4f(0, 1, 0, 0.5f+(float)Math.sin(Math.PI*(p.getLife()/500.0f)+Math.PI*0.25f)*0.5f);
						GLGraphicRoutines.draw2DRect(p.getX()-0.02f, p.getY()-0.02f, p.getX()+0.01f, p.getY()+0.01f, 0);
					}
				GL11.glPopMatrix();
		
				GL11.glPushMatrix();
					GL11.glColor3f(0, 1, 0);
					GLGraphicRoutines.drawLineCircle(1.76f, 50, 3f);
					GLGraphicRoutines.drawLineRect(1.0f, -GLValues.glWidth*0.49f, -GLValues.glHeight*0.49f, GLValues.glWidth*0.49f, GLValues.glHeight*0.49f, 0);
					GLBitmapFontBlitter.blitSinString("CodeRats    CodeRats    CodeRats    CodeRats    CodeRats    ", 0.1f, 0.9f, 1, 2.2f+(float)Math.sin(time*0.001f)*0.2f, time*0.0003f, "font_code");
				GL11.glPopMatrix();
	
			GL11.glPopMatrix();
			currentTime = time;
		} else {
			float st = 1-(exitTime-time)/(float)EXIT_DURATION;
			
			GL11.glColor4f(0,0,0,1);
			GLGraphicRoutines.drawRepeatedBackgroundPlane(-GLValues.glWidth*0.49f, -GLValues.glHeight*0.49f, GLValues.glWidth*0.49f, GLValues.glHeight*0.49f);
			GL11.glPushMatrix();
		
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, -1);
				
				GL11.glTranslatef(GLValues.glWidth*0.5f, GLValues.glHeight*0.5f, 0);
				GL11.glRotatef(-st*90, 0, 0, 1);
				
				GL11.glColor3f(st, 0.5f, st);
				
				GL11.glPushMatrix();
					particleCloud.advance();
					for(Particle p : particleCloud.getParticles()){
						GL11.glColor4f(st, 1, st, 0.5f+(float)Math.sin(Math.PI*(p.getLife()/500.0f)+Math.PI*0.25f)*0.5f);
						GLGraphicRoutines.draw2DRect(p.getX()-0.02f, p.getY()-0.02f, p.getX()+0.01f, p.getY()+0.01f, 0);
					}
				GL11.glPopMatrix();
		
				GL11.glPushMatrix();
					GL11.glColor3f(1, 0, 0);
					//GLGraphicRoutines.drawLineRect(1.0f, -GLValues.glWidth*0.49f, -GLValues.glHeight*0.49f*(1-st), GLValues.glWidth*0.49f, GLValues.glHeight*0.49f*(1-st), 0);		
					GLGraphicRoutines.drawLineRect(1.0f, -GLValues.glWidth*0.49f*(1-st), -GLValues.glHeight*0.49f, GLValues.glWidth*0.49f*(1-st), GLValues.glHeight*0.49f, 0);
					GLGraphicRoutines.drawLineCircle(1.76f*(1-st), 50, 3f);
					GL11.glColor4f(1, 0, 0, 1-st);
					GLBitmapFontBlitter.blitSinString("START!    START!    START!    START!    ", 0.5f, 0.9f, 1, 2.1f, time*0.002f, "font_code");
				GL11.glPopMatrix();
	
			GL11.glPopMatrix();
			
			if(st > 1) exit = true;
		}
		
		if(knownCards.size() >= 1){
			for(Entry<Integer, ARCard> c : knownCards.entrySet()){
				ARCard card = c.getValue();
				if(card.getQuality() >= 0.8f) card.glDrawDebug(time);
			}
		}
		
	}

	@Override
	public boolean isRunning() {
		return !exit;
	}

	@Override
	public void processInput(int inputKey) {
		exitTime = currentTime + EXIT_DURATION;
	}

	@Override
	public void cardAppeared(int id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cardDataUpdated(int id) {
		// TODO Auto-generated method stub
		
	}
	
}
