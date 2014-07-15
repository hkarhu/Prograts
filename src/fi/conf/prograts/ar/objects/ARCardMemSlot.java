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
		GL11.glTranslatef(x, y, 3);
		GL11.glRotatef(a, 0, 0, 1);
		
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
		Command tmp = this.command;
		this.card = c;
		if(c != null){
			this.command = c.getCommand();
			c.setCommand(tmp);
		} else {
			this.command = null;
		}
	}

	public void highlight() {
		highlighted = true;
	}


}
