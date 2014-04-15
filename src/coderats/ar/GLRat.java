package coderats.ar;

import ae.gl.GLGraphicRoutines;

public class GLRat {

	private static float RAT_SIZE = 0.1f;
	
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
	
	public void glDraw(){
		GLGraphicRoutines.drawLineRect(1.0f, -RAT_SIZE, -RAT_SIZE, RAT_SIZE, RAT_SIZE, 0);
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
