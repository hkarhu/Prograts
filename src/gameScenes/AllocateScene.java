package gameScenes;

import org.lwjgl.opengl.GL11;

import ae.gl.GLGraphicRoutines;
import ae.gl.GLValues;
import ae.gl.text.GLBitmapFontBlitter;
import ae.gl.texture.GLTextureManager;

public class AllocateScene extends GameScene {


	private long allocateTimer;
	private boolean shuttingDown;
	private AllocateHalf p1Allocate;
	private AllocateHalf p2Allocate;

	public AllocateScene() {
		p1Allocate = new AllocateHalf(true);
		p2Allocate = new AllocateHalf(false);
		allocateTimer = 0;
		shuttingDown = false;
	}

	@Override
	public void initialize() {
		shuttingDown = false;
	}

	@Override
	public void glDraw(long time) {

		if(!shuttingDown){

			p1Allocate.glDraw(time);
			p2Allocate.glDraw(time);
			float q = 1-(allocateTimer-time)/10000.0f;
			GLTextureManager.unbindTexture();
			GL11.glColor3f(1, 1, 1);
			if (q >= 1){
				shuttingDown = true;
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

		} else {
			p1Allocate.glDraw(time);
			p2Allocate.glDraw(time);
		}

	}
}