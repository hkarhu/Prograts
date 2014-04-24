package coderats.ar;

import org.lwjgl.opengl.GL11;

import ae.gl.GLGraphicRoutines;
import ae.gl.GLValues;
import ae.gl.text.GLBitmapFontBlitter;
import ae.gl.texture.GLTextureManager;
import coderats.ar.Command.Type;

public class ARCardSlot extends GLDrawableItem {
	
	private final float SCALE = 1f;
	private final int ACTIVATE_TIME = 500;
	private final int BREAK_TIME = 500;
	
	float x;
	float y;
	float a;
	
	private float slot_width = 0.4f*SCALE;
	private float slot_heigth = 0.6f*SCALE;
	
	private Command command;
	private ARCard card;
	
	private float activateTime = -1;
	private float breakTime = -1;
	private boolean highlighted = false;
		
	public ARCardSlot(float x, float y, float a) {
		this.x = x;
		this.y = y;
		this.a = a;
		this.command = null;
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
				
				GL11.glColor4f(1, 1, 1, at);
				GLGraphicRoutines.drawLineRect(1.0f, -slot_width-(1-at)*0.2f, -slot_heigth-(1-at)*0.2f, slot_width+(1-at)*0.2f, slot_heigth+(1-at)*0.2f, 0);
			}
		
			if(breakTime > time){
				GL11.glPushMatrix();
				GL11.glColor4f(1, 0, 0, 0.5f+at);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, -1);
				GL11.glLineWidth(10-at*6);
				GL11.glTranslatef(0, -0.025f*GLValues.glWidth, -5f);
				GL11.glRotatef(time*0.03f, 0, 0, 1);
				for(float i=0; i < Math.PI*2; i += Math.PI*0.2f){
					GL11.glBegin( GL11.GL_LINE_STRIP );			
						GL11.glVertex3d(0, 0, 0);
						GL11.glVertex3d((float)Math.sin(i-Math.random()*0.1f)*0.25f, (float)Math.cos(i-Math.random()*0.1f)*0.25f, 0);
						GL11.glVertex3d((float)Math.sin(i+Math.random()*0.2f)*0.3f, (float)Math.cos(i+Math.random()*0.2f)*0.3f, 0);
					GL11.glEnd();
				}
				GL11.glPopMatrix();
				GLTextureManager.getInstance().bindTexture("card_broken");
			}
			
			GLTextureManager.getInstance().bindTexture("card");
			
			if(command == null){
				GL11.glColor4f(0.3f,0.3f,0.3f, 1);
				GLGraphicRoutines.draw2DRect(-slot_width, -slot_heigth, slot_width, slot_heigth, 0);
				GLTextureManager.unbindTexture();
				GLGraphicRoutines.drawLineRect(1.0f, -slot_width, -slot_heigth, slot_width, slot_heigth, 0);
			
				GL11.glTranslatef(0, 0.42f, -0.2f);
				GLBitmapFontBlitter.drawString("NOP", "font_default", 0.2f, 0.4f, GLBitmapFontBlitter.Alignment.CENTERED);
			} else {
				GL11.glTranslatef(0, 0, -5);
				GLTextureManager.getInstance().bindTexture("card_"+command.getCommandString().toLowerCase());
				command.GLColorizeLight();
				GLGraphicRoutines.draw2DRect(-slot_width, -slot_heigth, slot_width, slot_heigth, 0);
				GLTextureManager.unbindTexture();
				GLGraphicRoutines.drawLineRect(1.0f, -slot_width, -slot_heigth, slot_width, slot_heigth, 0);
				
				GL11.glTranslatef(0, 0.42f, -0.2f);
				GLBitmapFontBlitter.drawString(command.getCommandString(), "font_default", 0.2f, 0.4f, GLBitmapFontBlitter.Alignment.CENTERED);
			}
			
			if(highlighted){
				
				GL11.glColor4f(0,1,0, 1);
				GL11.glTranslatef(0, 0, -5);
				GLTextureManager.unbindTexture();
				GLGraphicRoutines.drawLineRect(2.0f, -slot_width, -GLValues.glHeight*0.04f, slot_width, GLValues.glHeight*0.04f, 0);

				GL11.glTranslatef(0, GLValues.glHeight*0.06f, 0);
				GLBitmapFontBlitter.drawString("[UPLOAD]", "font_default", 0.1f, 0.2f, GLBitmapFontBlitter.Alignment.CENTERED);
				
				//Loading circle
				GL11.glPushMatrix();
					GL11.glColor4f(1, 1, 1, 0.5f);
					GL11.glTranslatef(0, -GLValues.glHeight*0.1825f, 0);
					GL11.glRotatef(time*0.3f, 0, 0, 1);
					GLGraphicRoutines.drawLineCircle(0.4f, 20, 2.0f);
				
						for(float i=0; i < Math.PI*2; i += Math.PI*0.5f){
							GL11.glBegin( GL11.GL_LINE_STRIP );			
								GL11.glVertex3d((float)Math.sin(i)*0.15f, (float)Math.cos(i)*0.15f, 0);
								GL11.glVertex3d((float)Math.sin(i)*0.25f, (float)Math.cos(i)*0.25f, 0);
								
							GL11.glEnd();
						}
				GL11.glPopMatrix();

			}
			
		GL11.glPopMatrix();
	}
	
	public void reset(){
		activateTime = 0;
		breakTime = 0;
		highlighted = false;
		command = null;
		card = null;
	}

	public void activate(long time) {
		activateTime = time + ACTIVATE_TIME;
		highlighted = false;
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
		if(command == null) return Type.NOP;
		return command.getType();
	}
	
	public void bindCard(ARCard card) {
		this.card = card;
		if(card != null){
			this.command = card.getCommand();
		} else {
			this.command = null;
		}
	}

	public void breakContainedCard(long time) {
		breakTime = time + BREAK_TIME;
		if(card != null) card.setBroken(true);
		bindCard(null);
		highlighted = false;
	}

	public void highlight() {
		highlighted = true;
	}
	
}
