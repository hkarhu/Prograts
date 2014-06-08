package coderats.ar.objects;
import java.awt.Color;

import org.lwjgl.opengl.GL11;


public class Command {
	
	public enum Type {
		FWD, ROL, ROR, PEW, NOP, QQQ, BMB, FUU
	}
	
	private Type type;
	
	public Command(Type type) {
		this.type = type;
	}

	public Color getCommandColor(){
		switch (type) {
			case FWD: return Color.getHSBColor(0, 0, 1);
			case ROL: return Color.getHSBColor(1, 0, 1);
			case ROR: return Color.getHSBColor(0, 1, 1);
			case PEW: return Color.getHSBColor(1, 0, 0);
			default: return Color.white;
		}
	}
	
	public void GLColorizeDark(){
		switch (type) {
		case FWD: GL11.glColor4f(0, 0, 0.5f, 1); break;
		case ROL: GL11.glColor4f(0.5f, 0, 0.5f, 1); break;
		case ROR: GL11.glColor4f(0, 0.5f, 0.5f, 1); break;
		case PEW: GL11.glColor4f(0.5f, 0, 0, 1); break;
		case BMB: GL11.glColor4f(0.5f, 0.5f, 0, 1); break;
		case FUU: GL11.glColor4f(0.5f, 0.5f, 0.5f, 1); break;
		case QQQ:
			int c = Color.HSBtoRGB((float) Math.random(), 1, 0.5f);
			GL11.glColor4f(((c & 0x000000FF))/255f,((c & 0x0000FF00) >> 8)/255f,((c & 0x00FF0000) >> 16)/255f, 1);
			break;
		default:  GL11.glColor4f(0.5f, 0.5f, 0.5f, 1); break;
		}
	}
	
	public void GLColorizeLight() {
		switch (type) {
		case FWD: GL11.glColor4f(0, 0, 1, 1); break;
		case ROL: GL11.glColor4f(1, 0, 1, 1); break;
		case ROR: GL11.glColor4f(0, 1, 1, 1); break;
		case PEW: GL11.glColor4f(1, 0, 0, 1); break;
		case BMB: GL11.glColor4f(1, 1, 0, 1); break;
		case FUU: GL11.glColor4f(1, 1, 1, 1); break;
		case QQQ:
			int c = Color.HSBtoRGB((float) Math.random(), 1, 1);
			GL11.glColor4f(((c & 0x000000FF))/255f,((c & 0x0000FF00) >> 8)/255f,((c & 0x00FF0000) >> 16)/255f, 1);
			break;
		default:  GL11.glColor4f(1, 1, 1, 1); break;
		}
	}
	
	public String getCommandString(){
		switch (type) {
			case FWD: return "FWD";
			case ROL: return "ROL";
			case ROR: return "ROR";
			case PEW: return "PEW";
			case BMB: return "BMB";
			case FUU: return "FUU";
			case QQQ: return "QQQ";
			default: return "NOP";
		}
	}

	public Type getType() {
		if(type == null) return Type.NOP;
		return type;
	}
}
	