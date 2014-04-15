package coderats.ar;

import org.lwjgl.opengl.GL11;

import ae.gl.GLGraphicRoutines;
import ae.gl.texture.GLTextureManager;

public class GLRat {

	private static float RAT_SIZE = 0.9f;
	
	private int x;
	private int y;
	private int r;
	boolean shooting=false;
	boolean nop = false;
	
	public GLRat(int x, int y, int r) {
		this.x = x;
		this.y = y;
		this.r = r;
	}
	
	public void glDraw(long time){
		RAT_SIZE = 0.3f;
		GLTextureManager.getInstance().bindTexture("rat");
		GL11.glPushMatrix();
			//GL11.glRotatef(90*r+(float) Math.sin(time), 0, 0, 1);
			GLGraphicRoutines.draw2DRect(-RAT_SIZE, -RAT_SIZE, RAT_SIZE, RAT_SIZE, 0);
		GL11.glPopMatrix();
	}
	
	public void stp(){
		switch (r) {
		case 0: y--; break;
		case 1: x++; break;
		case 2: y++; break;
		case 3: x--; break;
		default: break;
		}
	}
	
	public void rol(){
		r--;
		if(r < 0) r=3;
	}
	
	public void ror(){
		r++;
		if(r >= 3) r=0;
	}
	
	public void pew(){
		shooting=true;
	}
	
	public void nop(){
		nop = true;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getRotation() {
		return r;
	}

	public void setRotation(int r) {
		this.r = r;
	}

}
