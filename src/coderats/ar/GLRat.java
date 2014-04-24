package coderats.ar;

import org.lwjgl.opengl.GL11;

import ae.gl.GLGraphicRoutines;
import ae.gl.text.GLBitmapFontBlitter;
import ae.gl.text.GLBitmapFontBlitter.Alignment;
import ae.gl.texture.GLTextureManager;
import coderats.ar.Command.Type;

public class GLRat {

	private static final float RAT_SIZE = 0.3f;
	private static final int ANIM_LENGTH = 500;

	private String name;
	private int x;
	private int y;
	private int r;
	private Type lastCMD;
	long anitime = -1;
	private boolean alive;
	private boolean damage;

	public GLRat(int x, int y, int r, String name) {
		this.x = x;
		this.y = y;
		this.r = r;
		this.name = name;
		lastCMD = Type.NOP;
		reset();
	}

	public void reset(){
		alive = true;
	}

	public void glDraw(long time){

		float at = (anitime-time)/(float)ANIM_LENGTH;
		
		GL11.glPushMatrix();
		GL11.glRotatef(90*r+180, 0, 0, 1);
		GL11.glColor3f(0.4f, 0.4f, 0.4f);
		GLGraphicRoutines.drawLineCircle(0.1f, 3, 2.0f);
		GL11.glRotatef(-270, 0, 0, 1);
		
		GL11.glColor4f(1,1,1,1);
		GLTextureManager.getInstance().bindTexture("rat");
		if(damage && at < 0.5f){
			GL11.glColor4f(1,0.5f,0.5f,(float) (0.5f+Math.sin(time*0.1f)));
			GLTextureManager.getInstance().bindTexture("rat_damage");
		} else if(alive && anitime > time || lastCMD.equals(Type.NOP)){
			switch (lastCMD) {
			case STP: 
				GL11.glTranslatef(-RAT_SIZE*at, 0, 0); 
				GL11.glRotatef((float)Math.sin(at*Math.PI*4)*10, 0,0,1);
				break;
			case ROL:
				GL11.glRotatef(90*at, 0,0,1);
				break;
			case ROR:
				GL11.glRotatef(-90*at, 0,0,1);
				break;
			case PEW: 
				if(time%2 == 0){
					GLTextureManager.getInstance().bindTexture("rat_fire0");
				} else {
					GLTextureManager.getInstance().bindTexture("rat_fire1");
				}
				break;
			default:
				//GL11.glRotatef(time*0.03f, 0, 0, 1);
				if(Math.sin(time*0.01f) < 0){
					GLTextureManager.getInstance().bindTexture("rat_nop0");
				} else {
					GLTextureManager.getInstance().bindTexture("rat_nop1");
				}
				break;
			}
		} else if(!alive) {
			GL11.glColor4f(1,1,1,(float) (0.5f+Math.sin(time*0.1f)));
			GLTextureManager.getInstance().bindTexture("rat_dead");
		}
		GLGraphicRoutines.draw2DRect(-RAT_SIZE, -RAT_SIZE, RAT_SIZE, RAT_SIZE, -2);
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glTranslatef(-0.1f, -0.03f, -5);
		GLBitmapFontBlitter.drawString(name, "font_code", 0.08f, 0.08f, Alignment.CENTERED);

		GL11.glPopMatrix();
	}

	private void stp(){
		switch (r) {
		case 0: y--; break;
		case 1: x++; break;
		case 2: y++; break;
		case 3: x--; break;
		default: break;
		}
	}

	private void rol(){
		r--;
		if(r < 0) r=3;
	}

	private void ror(){
		r++;
		if(r > 3) r=0;
	}

	private void pew(){

	}

	private void nop(){

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

	public void execute(Type cmd, long time) {
		if(!alive) return;
		switch (cmd) {
		case STP: stp(); break;
		case ROL: rol(); break;
		case ROR: ror(); break;
		case PEW: pew(); break;
		case NOP: nop(); break;
		default: 
			alive = false; 
			System.out.println("Funny command detected. Killing rat!");
			break;
		}
		lastCMD = cmd;
		anitime = time + ANIM_LENGTH;
	}

	public boolean isAlive() {
		return alive;
	}

	public void takeDamage(){
		damage = true;
	}

	public boolean isShooting() {
		return Type.PEW.equals(lastCMD);
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}

}
