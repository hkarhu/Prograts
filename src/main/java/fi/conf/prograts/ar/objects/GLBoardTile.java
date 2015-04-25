package fi.conf.prograts.ar.objects;

import org.lwjgl.opengl.GL11;

import fi.conf.prograts.ar.Globals;
import fi.conf.prograts.ar.gl.GLGraphicRoutines;

public class GLBoardTile extends GLBoardObject {

	public GLBoardTile(int x, int y) {
		this.x = x; 
		this.y = y;
	}

	@Override
	public void glDraw(long time) {
		
		GL11.glPushMatrix();
			
			GL11.glTranslatef(Globals.BOARD_TILE_SIZE*this.x + Globals.BOARD_TILE_SIZE*0.5f, Globals.BOARD_TILE_SIZE*this.y + Globals.BOARD_TILE_SIZE*0.5f, 0);
			
			GL11.glColor3f(0.0f, 0.4f, 0);
			GLGraphicRoutines.drawLineRect(1.0f, -Globals.BOARD_TILE_SIZE*0.5f, -Globals.BOARD_TILE_SIZE*0.5f, Globals.BOARD_TILE_SIZE*0.5f, Globals.BOARD_TILE_SIZE*0.5f, 0);
			GL11.glColor3f(0.0f, 0.1f, 0.0f);
			GLGraphicRoutines.draw2DRect(-Globals.BOARD_TILE_SIZE*0.5f, -Globals.BOARD_TILE_SIZE*0.5f, Globals.BOARD_TILE_SIZE*0.5f, Globals.BOARD_TILE_SIZE*0.5f, 1);
			
		GL11.glPopMatrix();
		
	}

}
