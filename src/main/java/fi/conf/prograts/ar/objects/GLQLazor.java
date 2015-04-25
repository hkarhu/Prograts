package fi.conf.prograts.ar.objects;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import fi.conf.prograts.ar.Globals;

public class GLQLazor extends GLLazor {

	public GLQLazor(int x, int y, int d) {
		super(x, y, d);
	}
	
	@Override
	public void glDraw(long time) {
		
		float t = (time-startTime)/500.0f;
		
		if(t > 1) return;
		
		GL11.glPushMatrix();
		
			int lolz = Color.HSBtoRGB(t, 1, 1);
		
			GL11.glColor4f(((lolz & 0x000000FF))/255f,((lolz & 0x0000FF00) >> 8)/255f,((lolz & 0x00FF0000) >> 16)/255f, 1);
			
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, -1);
			GL11.glLineWidth(10-t);
			GL11.glTranslatef(0, (float)(0.5f-Math.random())*0.1f, 0);
			GL11.glBegin( GL11.GL_LINE_STRIP );			
				switch (d) {
				case 0: 
					GL11.glVertex3d(-0.05f, -0.05f, 0);
					GL11.glVertex3d(-0.05f+(float)(0.5f-Math.random())*0.2f, -getY()*Globals.BOARD_TILE_SIZE - Globals.BOARD_TILE_SIZE , 0);
					break;
				case 1: 
					GL11.glVertex3d(0.05f, -0.05f, 0);
					GL11.glVertex3d((GLRatBoard.BOARD_SIZE-1)*Globals.BOARD_TILE_SIZE - getX()*Globals.BOARD_TILE_SIZE + Globals.BOARD_TILE_SIZE, -0.05f+(float)(0.5f-Math.random())*0.2f, 0);
					break;
				case 2: 
					GL11.glVertex3d(0.05f, 0.05f, 0);
					GL11.glVertex3d(0.05f+(float)(0.5f-Math.random())*0.2f, (GLRatBoard.BOARD_SIZE-1)*Globals.BOARD_TILE_SIZE - getY()*Globals.BOARD_TILE_SIZE + Globals.BOARD_TILE_SIZE , 0);
					break;
				case 3:
					GL11.glVertex3d(-0.05f, 0.05f, 0);
					GL11.glVertex3d(-getX()*Globals.BOARD_TILE_SIZE - Globals.BOARD_TILE_SIZE, 0.05f+(float)(0.5f-Math.random())*0.2f, 0);
					break;
				default: break;
				}
				
			GL11.glEnd();
			
		GL11.glPopMatrix();
		
	}
	
	public boolean hitsRat(GLRat r){
		switch (d) {
			case 0: if (r.getX() == getX() && r.getY() < getY()){ r.bumpY(-3); return true; } break;
			case 1: if (r.getY() == getY() && r.getX() > getX()){ r.bumpX(3); return true; } break;
			case 2: if (r.getX() == getX() && r.getY() > getY()){ r.bumpY(3);  return true; } break;
			case 3: if (r.getY() == getY() && r.getX() < getX()){ r.bumpX(-3); return true; } break;
			default: break;
		}	
		return false;
	}

}
