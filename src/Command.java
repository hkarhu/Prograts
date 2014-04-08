import java.awt.Color;

import org.lwjgl.opengl.GL11;


public class Command {
	
	enum Type {
		STEP, ROTL, ROTR, PEW
	}
	
	private Type type;
	
	public Command(Type type) {
		this.type = type;
	}

	public Color getCommandColor(){
		switch (type) {
			case STEP: return Color.getHSBColor(1, 1, 1);
			case ROTL: return Color.getHSBColor(1, 1, 1);
			case ROTR: return Color.getHSBColor(1, 1, 1);
			case PEW: return Color.getHSBColor(1, 1, 1);
			default: return Color.white;
		}
	}
	
	public void commandGLColorize(){
		GL11.glColor3f(getCommandColor().getRed()/255.0f, getCommandColor().getGreen()/255.0f, getCommandColor().getBlue()/255.0f);
	}
	
	public String getCommandString(){
		switch (type) {
			case STEP: return "STP";
			case ROTL: return "RTL";
			case ROTR: return "RTR";
			case PEW: return "PEW";
			default: return "???";
		}
	}
}
