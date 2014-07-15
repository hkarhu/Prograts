package fi.conf.prograts.ar.objects;

import fi.conf.prograts.ar.Globals;
import fi.conf.prograts.ar.objects.Command.Type;

public abstract class ARCardSlot {

	protected final int ACTIVATE_TIME = 500;
	protected final int BREAK_TIME = 500;
	
	protected float x;
	protected float y;
	protected float a;

	protected float slot_width = 0.4f*Globals.CARD_SCALE;
	protected float slot_heigth = 0.6f*Globals.CARD_SCALE;

	protected Command command;
	protected ARCard card;

	public ARCardSlot(float x, float y, float a) {
		this.x = x;
		this.y = y;
		this.a = a;
		this.command = null;
	}

	public abstract void glDraw(long time);
	
	public void reset(){
		command = null;
		card = null;
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

}
