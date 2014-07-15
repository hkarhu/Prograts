package fi.conf.prograts.ar.objects;

import org.lwjgl.opengl.GL11;

public class GLLazor {
	
	public final static float ANIM_LENGTH = 200.0f;
	protected int x = 0;
	protected int y = 0;
	protected int d = 0;
	protected boolean active = false;
	protected long startTime = 0;
	
	public GLLazor(int x, int y, int d) {
		this.x = x;
		this.y = y;
		this.d = d;
	}
	
	public void setTime(long time){
		this.startTime = time;
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

	public int getDirtection() {
		return d;
	}

	public void setDirection(int d) {
		this.d = d;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	public void glDraw(long time) {
		
		float t = (time-startTime)/ANIM_LENGTH;
		
		if(t > 1) return;
		
		GL11.glPushMatrix();
			
			GL11.glColor4f(1, 0, 0, 1-(float)Math.sin(t*0.2f));
			
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, -1);
			GL11.glLineWidth(10-t*6);

			GL11.glBegin( GL11.GL_LINE_STRIP );			
				switch (d) {
				case 0: 
					GL11.glVertex3d(-0.05f, 0, 0);
					GL11.glVertex3d(-0.05f, -getY()*GLRatBoard.SQUARE_SIZE, 0);
					break;
				case 1: 
					GL11.glVertex3d(0, -0.05f, 0);
					GL11.glVertex3d((GLRatBoard.BOARD_SIZE-1)*GLRatBoard.SQUARE_SIZE - getX()*GLRatBoard.SQUARE_SIZE, -0.05f, 0);
					break;
				case 2: 
					GL11.glVertex3d(0.05f, 0, 0);
					GL11.glVertex3d(0.05f, (GLRatBoard.BOARD_SIZE-1)*GLRatBoard.SQUARE_SIZE - getY()*GLRatBoard.SQUARE_SIZE, 0);
					break;
				case 3:
					GL11.glVertex3d(0, 0.05f, 0);
					GL11.glVertex3d(-getX()*GLRatBoard.SQUARE_SIZE, 0.05f, 0);
					break;
				default: break;
				}
				
			GL11.glEnd();
			
		GL11.glPopMatrix();
		
	}
	
	public boolean hitsRat(GLRat r){
		switch (d) {
			case 0: if (r.getX() == getX() && r.getY() < getY()){ r.bumpY(-1); return true; } break;
			case 1: if (r.getY() == getY() && r.getX() > getX()){ r.bumpX(1); return true; } break;
			case 2: if (r.getX() == getX() && r.getY() > getY()){ r.bumpY(1);  return true; } break;
			case 3: if (r.getY() == getY() && r.getX() < getX()){ r.bumpX(-1); return true; } break;
			default: break;
		}	
		return false;
	}

}
