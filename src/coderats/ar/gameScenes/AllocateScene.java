package coderats.ar.gameScenes;

import org.lwjgl.opengl.GL11;

import ae.gl.GLGraphicRoutines;
import ae.gl.GLValues;
import ae.gl.text.GLBitmapFontBlitter;
import ae.gl.texture.GLTextureManager;

public class AllocateScene extends GameScene {

	private long allocateTimer;
	private long exitTime;
	private AllocateHalf p1Allocate;
	private AllocateHalf p2Allocate;

	public AllocateScene() {
		p1Allocate = new AllocateHalf(true);
		p2Allocate = new AllocateHalf(false);
		allocateTimer = -1;
		exitTime = -1;
	}

	@Override
	public void init() {
		setRunning(true);
		exitTime = -1;
		allocateTimer = -1;
		p1Allocate.activate(100);
		p2Allocate.activate(100);
	}

	@Override
	public void glDraw(long time) {

		p1Allocate.glDraw(time);
		p2Allocate.glDraw(time);

		GLTextureManager.unbindTexture();

		if(time < 3000){
			float in = (3000-time)/3000.0f;
			GL11.glColor4f(1, 1, 1, in);
			
			GL11.glTranslatef(GLValues.glWidth*0.5f, GLValues.glHeight*0.5f, -5);
			GLBitmapFontBlitter.blitSinString("ALLOCATE      ALLOCATE      ", 0.5f, 0.9f, 1, 1+3*(1-in), 0.9f+(float)Math.sin(time*0.003f)*0.2f, "font_code");
			
			allocateTimer = time+10000;

		} else {

			float q = 1-(allocateTimer-time)/10000.0f;

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
					GL11.glTranslatef(GLValues.glWidth*0.5f, GLValues.glHeight*0.08f, 0);
					GL11.glRotatef(90, 0, 0, 1);
					GLBitmapFontBlitter.drawString(999-(int)(999*q)+"", "font_code", GLValues.glWidth*0.02f, GLValues.glWidth*0.02f, GLBitmapFontBlitter.Alignment.CENTERED);
					GL11.glTranslatef(GLValues.glWidth*0.5f, 0, 0);
					GL11.glRotatef(180, 0, 0, 1);
					GLBitmapFontBlitter.drawString(999-(int)(999*q)+"", "font_code", GLValues.glWidth*0.02f, GLValues.glWidth*0.02f, GLBitmapFontBlitter.Alignment.CENTERED);
					GL11.glPopMatrix();
				}
			} else if(time > exitTime) setRunning(false);
		}

	}

	@Override
	public void processInput(int inputKey) {

	}
}