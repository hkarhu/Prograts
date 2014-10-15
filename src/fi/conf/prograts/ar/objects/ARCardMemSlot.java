package fi.conf.prograts.ar.objects;

import org.lwjgl.opengl.GL11;

import fi.conf.prograts.ar.Globals;
import fi.conf.prograts.ar.gl.GLBitmapFontBlitter;
import fi.conf.prograts.ar.gl.GLGraphicRoutines;
import fi.conf.prograts.ar.gl.GLTextureManager;
import fi.conf.prograts.ar.gl.GLValues;
import fi.conf.prograts.ar.objects.Command.Type;

public class ARCardMemSlot extends ARCardSlot {

	private float activateTime = -1;
	private float breakTime = -1;
	private boolean highlighted = false;

	public ARCardMemSlot(float x, float y, float a) {
		super(x, y, a);
	}
	
	public void glDraw(long time){

		float at = (activateTime - time)/ACTIVATE_TIME;

		GL11.glPushMatrix();
			
			GL11.glColor4f(1,1,1,1);
			GL11.glScalef(1,1,1);
			GL11.glTranslatef(x, y, 0);
			GL11.glRotatef(a, 0, 0, 1);
			
			GLTextureManager.getInstance().bindTexture("card");
			
			if(command == null){
				GL11.glColor4f(0.3f,0.3f,0.3f, 1);
				GLGraphicRoutines.draw2DRect(-slot_width, -slot_heigth, slot_width, slot_heigth, 0);
				GLTextureManager.unbindTexture();
				GLGraphicRoutines.drawLineRect(1.0f*Globals.CARD_SCALE, -slot_width, -slot_heigth, slot_width, slot_heigth, 0);
			
				GL11.glTranslatef(0, 0.42f*Globals.CARD_SCALE, -0.2f);
				GLBitmapFontBlitter.drawString("NOP", "font_default", 0.2f*Globals.CARD_SCALE, 0.4f*Globals.CARD_SCALE, GLBitmapFontBlitter.Alignment.CENTERED);
			} else {
				GL11.glTranslatef(0, 0, -5);
				GLTextureManager.getInstance().bindTexture("card_"+command.getCommandString().toLowerCase());
				command.GLColorizeLight();
				GLGraphicRoutines.draw2DRect(-slot_width, -slot_heigth, slot_width, slot_heigth, 0);
				GLTextureManager.unbindTexture();
				GLGraphicRoutines.drawLineRect(1.0f, -slot_width, -slot_heigth, slot_width, slot_heigth, 0);
				
//				GL11.glTranslatef(0, 0.42f, -0.2f);
//				GLBitmapFontBlitter.drawString(command.getCommandString(), "font_default", 0.2f, 0.4f, GLBitmapFontBlitter.Alignment.CENTERED);
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

	public boolean hits(ARCard c){

		if(a > 180){
			return c.getX() > x-slot_width && c.getX() < x+slot_width*0.5f && 
					c.getY() > y-slot_heigth && c.getY() < y+slot_heigth;

		} else {
			return c.getX() > x-slot_width*0.5f && c.getX() < x+slot_width && 
					c.getY() > y-slot_heigth && c.getY() < y+slot_heigth;
		}

	}

	public Type getSlottedCommandType() {
		if(command == null) return Type.NOP;
		return command.getType();
	}

	public void bindCard(ARCard c) {
		this.card = c;
		if(c != null){
			this.command = c.getCommand();
		} else {
			this.command = null;
		}
	}

	public void highlight() {
		highlighted = true;
	}


}
