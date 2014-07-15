package fi.conf.prograts.ar.objects;

import org.lwjgl.opengl.GL11;

import fi.conf.prograts.ar.gl.GLBitmapFontBlitter;
import fi.conf.prograts.ar.gl.GLGraphicRoutines;
import fi.conf.prograts.ar.gl.GLTextureManager;
import fi.conf.prograts.ar.gl.GLBitmapFontBlitter.Alignment;
import fi.conf.prograts.ar.objects.Command.Type;

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
	private GLLazor lazor;

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
		lazor = null;
	}

	public void glDraw(long time){

		float at = (anitime-time)/(float)ANIM_LENGTH;
		
		GL11.glPushMatrix();
		GL11.glRotatef(90*r+180, 0, 0, 1);
		GL11.glColor3f(0.4f, 0.4f, 0.4f);
		GLGraphicRoutines.drawLineCircle(0.1f, 3, 2.0f);
		GL11.glRotatef(-270, 0, 0, 1);
		
		if(name == "B"){
			GL11.glColor4f(0,1,1,1);
		} else {
			GL11.glColor4f(0,1,0,1);
		}
		GLTextureManager.getInstance().bindTexture("rat");
		if(damage && at < 0.5f && anitime > time){
			GL11.glColor4f(1,0.5f,0.5f,(float) (0.5f+Math.sin(time*0.1f)));
			GLTextureManager.getInstance().bindTexture("rat_damage");
		} else if(alive && anitime > time || lastCMD.equals(Type.NOP)){
			switch (lastCMD) {
			case FWD: 
				GL11.glTranslatef(-RAT_SIZE*at, 0, 0); 
				GL11.glRotatef((float)Math.sin(at*Math.PI*4)*10, 0,0,1);
				break;
			case ROL:
				GL11.glRotatef(90*at, 0,0,1);
				break;
			case ROR:
				GL11.glRotatef(-90*at, 0,0,1);
				break;
			case QQQ:
				GL11.glPushMatrix();
					GL11.glColor4f((float)(Math.random()*1.0f),(float)(Math.random()*1.0f),(float)(Math.random()*1.0f),at);
					GLTextureManager.unbindTexture();
					GL11.glTranslatef(0, 0, 5);
					GLGraphicRoutines.drawCircle((float) (Math.sin((1-at)*Math.PI)*8), 32);
					GL11.glTranslatef(0, 0, -6);
					GLGraphicRoutines.drawCircle(0.6f*(1-at), 10);
					GL11.glColor4f(1, 1, 1, 1);
					GLGraphicRoutines.drawLineCircle(5*(1-at), 10, 2f);
				GL11.glPopMatrix();
				
				GL11.glRotatef((float) Math.sin(at*50)*20, 0, 0, 1);
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
		lazor = new GLLazor(x, y, r);
	}
	
	private void qqq() {
		lazor = new GLQLazor(x, y, r);
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
		case FWD: stp(); break;
		case ROL: rol(); break;
		case ROR: ror(); break;
		case PEW: pew(); break;
		case QQQ: qqq(); break;
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

	public void takeDamage(long time){
		damage = true;
		anitime = time + ANIM_LENGTH;
	}

	public GLLazor getLazor() {
		return lazor;
	}
	
	public void setLazor(GLLazor l){
		this.lazor = l;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
		//damage = true;
	}

	public void bumpX(int i) {
		x += i;
		//damage = true;
	}

	public void bumpY(int i) {
		y += i;
		//damage = true;
	}

}
