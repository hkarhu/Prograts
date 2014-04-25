package coderats.ar;
import java.awt.Color;

import org.lwjgl.opengl.GL11;


public class Command {
	
	public enum Type {
		STP, ROL, ROR, PEW, NOP
	}
	
	private Type type;
	
	public Command(Type type) {
		this.type = type;
	}

	public Color getCommandColor(){
		switch (type) {
			case STP: return Color.getHSBColor(0, 0, 1);
			case ROL: return Color.getHSBColor(1, 0, 1);
			case ROR: return Color.getHSBColor(0, 1, 1);
			case PEW: return Color.getHSBColor(1, 0, 0);
			default: return Color.white;
		}
	}
	
	public void GLColorizeDark(){
		switch (type) {
		case STP: GL11.glColor4f(0, 0, 0.5f, 1); break;
		case ROL: GL11.glColor4f(0.5f, 0, 0.5f, 1); break;
		case ROR: GL11.glColor4f(0, 0.5f, 0.5f, 1); break;
		case PEW: GL11.glColor4f(0.5f, 0, 0, 1); break;
		default:  GL11.glColor4f(0.5f, 0.5f, 0.5f, 1); break;
		}
	}
	
	public String getCommandString(){
		switch (type) {
			case STP: return "FWD";
			case ROL: return "ROL";
			case ROR: return "ROR";
			case PEW: return "PEW";
			default: return "NOP";
		}
	}

	public void GLColorizeLight() {
		switch (type) {
		case STP: GL11.glColor4f(0, 0, 1, 1); break;
		case ROL: GL11.glColor4f(1, 0, 1, 1); break;
		case ROR: GL11.glColor4f(0, 1, 1, 1); break;
		case PEW: GL11.glColor4f(1, 0, 0, 1); break;
		default:  GL11.glColor4f(1, 1, 1, 1); break;
		}
	}

	public Type getType() {
		if(type == null) return Type.NOP;
		return type;
	}
}
