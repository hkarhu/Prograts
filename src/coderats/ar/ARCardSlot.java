package coderats.ar;

import org.lwjgl.opengl.GL11;

import ae.gl.GLGraphicRoutines;
import ae.gl.texture.GLTextureManager;

public class ARCardSlot extends GLDrawableItem {
	
	private final float SCALE = 1f;
	private final int ACTIVATE_TIME = 100;
	
	float x;
	float y;
	float a;
	
	private float slot_width = 0.4f*SCALE;
	private float slot_heigth = 0.6f*SCALE;
	
	private Command card;
	
	private float activateTime = -1;
		
	public ARCardSlot(float x, float y, float a) {
		this.x = x;
		this.y = y;
		this.a = a;
	}

	@Override
	public void glDraw(long time){
		
		GL11.glPushMatrix();
			GL11.glColor4f(1,1,1,1);
			GL11.glScalef(1,1,1);
			GL11.glTranslatef(x, y, 0);
			GL11.glRotatef(a, 0, 0, 1);
			//GLTextureManager.getInstance().bindTexture("card");
			//GLGraphicRoutines.draw2DRect(-width*0.5f, width*0.5f, -height*0.5f, height*0.5f, 0);
			if(activateTime > time){
				float at = (activateTime - time)/ACTIVATE_TIME;
				GL11.glColor3f(0, 0, 1);
				GL11.glScalef(1.0f+(float)Math.abs(Math.sin(Math.PI + 2*Math.PI*at)*0.25f), 1.0f+(float)Math.abs(Math.sin(Math.PI + 2*Math.PI*at)*0.25f), 1);
				
			}
			GLTextureManager.unbindTexture();
			GLGraphicRoutines.drawLineRect(1.0f, -slot_width, -slot_heigth, slot_width, slot_heigth, 0);
		GL11.glPopMatrix();
	}

	public void activate(long time) {
		activateTime = time + ACTIVATE_TIME;
	}
	
	public boolean hits(ARCard card){
		return false;
	}

	public void setARCard(ARCard card){
		this.card = card;
	}
	
}
