package coderats.ar;

import org.lwjgl.opengl.GL11;

import ae.gl.GLGraphicRoutines;
import ae.gl.text.GLBitmapFontBlitter;
import ae.gl.texture.GLTextureManager;
import coderats.ar.Command.Type;

public class ARCardSlot extends GLDrawableItem {
	
	private final float SCALE = 1f;
	private final int ACTIVATE_TIME = 500;
	
	float x;
	float y;
	float a;
	
	private float slot_width = 0.4f*SCALE;
	private float slot_heigth = 0.6f*SCALE;
	
	private Command c;
	
	private float activateTime = -1;
		
	public ARCardSlot(float x, float y, float a) {
		this.x = x;
		this.y = y;
		this.a = a;
		this.c = null;
	}

	@Override
	public void glDraw(long time){
		
		float at = (activateTime - time)/ACTIVATE_TIME;
		
		GL11.glPushMatrix();
			GL11.glColor4f(1,1,1,1);
			GL11.glScalef(1,1,1);
			GL11.glTranslatef(x, y, 3);
			GL11.glRotatef(a, 0, 0, 1);
			//GLTextureManager.getInstance().bindTexture("card");
			//GLGraphicRoutines.draw2DRect(-width*0.5f, width*0.5f, -height*0.5f, height*0.5f, 0);
			if(activateTime > time){
				GL11.glColor3f(0, 0, 1);
				GL11.glScalef(1.0f+(float)Math.abs(Math.sin(Math.PI + Math.PI*at)*0.25f), 1.0f+(float)Math.abs(Math.sin(Math.PI + Math.PI*at)*0.25f), 1);
				
				if(activateTime > time){
					GL11.glColor4f(1, 1, 1, at);
					GLGraphicRoutines.drawLineRect(1.0f, -slot_width-(1-at), -slot_heigth-(1-at), slot_width+(1-at), slot_heigth+(1-at), 0);
				}
			}
		
			if(c == null){
				GL11.glColor4f(0.3f,0.3f,0.3f, 1);
				
				GLTextureManager.getInstance().bindTexture("card");
				GLGraphicRoutines.draw2DRect(-slot_width, -slot_heigth, slot_width, slot_heigth, 0);
				GLTextureManager.unbindTexture();
				GLGraphicRoutines.drawLineRect(1.0f, -slot_width, -slot_heigth, slot_width, slot_heigth, 0);
			
				GL11.glTranslatef(0, 0.42f, -0.2f);
				GLBitmapFontBlitter.drawString("NOP", "font_default", 0.2f, 0.4f, GLBitmapFontBlitter.Alignment.CENTERED);
			} else {
				GL11.glTranslatef(0, 0, -5);
				c.GLColorizeLight();
				GLTextureManager.getInstance().bindTexture("card");
				GLGraphicRoutines.draw2DRect(-slot_width, -slot_heigth, slot_width, slot_heigth, 0);
				GLTextureManager.unbindTexture();
				GLGraphicRoutines.drawLineRect(1.0f, -slot_width, -slot_heigth, slot_width, slot_heigth, 0);
				
				GL11.glTranslatef(0, 0.42f, -0.2f);
				GLBitmapFontBlitter.drawString(c.getCommandString(), "font_default", 0.2f, 0.4f, GLBitmapFontBlitter.Alignment.CENTERED);
			}
			
		GL11.glPopMatrix();
	}

	public void activate(long time) {
		activateTime = time + ACTIVATE_TIME;
	}
	
	public boolean hits(ARCard card){
		
		if(a > 180){
			return card.getX() > x-slot_width && card.getX() < x+slot_width*0.5f && 
					card.getY() > y-slot_heigth && card.getY() < y+slot_heigth;
				
		} else {
			return card.getX() > x-slot_width*0.5f && card.getX() < x+slot_width && 
					card.getY() > y-slot_heigth && card.getY() < y+slot_heigth;
		}
	}

	public Type getSlottedCommandType() {
		if(c == null) return Type.NOP;
		return c.getType();
	}

	public void setCommand(Command command) {
		this.c = command;
	}
	
	
	
	
	
	
	
	
	
	
}
