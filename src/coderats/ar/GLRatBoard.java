package coderats.ar;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import coderats.ar.Command.Type;
import ae.gl.GLGraphicRoutines;
import ae.gl.GLValues;

public class GLRatBoard extends GLDrawableItem {

	private final int BOARD_SIZE = 8;
	private final float SQUARE_SIZE = 0.3f;

	private List<GLRat> rats;
	
	public GLRatBoard() {
		rats = new ArrayList<>();
		resetGameBoard();
	}
	
	public void resetGameBoard(){
		rats.add(new GLRat(1,1,1));
		rats.add(new GLRat(BOARD_SIZE-2,BOARD_SIZE-2,3));
	}

	@Override
	public void glDraw(long time) {

		GL11.glPushMatrix();

		GL11.glTranslatef(GLValues.glWidth/2, GLValues.glHeight/2, -5f);
		GL11.glTranslatef(-BOARD_SIZE*0.5f*SQUARE_SIZE, -BOARD_SIZE*0.5f*SQUARE_SIZE, 0);
		GL11.glColor4f(1, 1, 1, 1);
		
		for(int x=0; x < BOARD_SIZE; x++){
			GL11.glPushMatrix();
			GL11.glTranslatef(SQUARE_SIZE*x + SQUARE_SIZE*0.5f, SQUARE_SIZE*0.5f, 0);
			for(int y=0; y < BOARD_SIZE; y++){
				GL11.glColor3f(0.1f, 0.4f, 0.1f);
				GLGraphicRoutines.drawLineRect(1.0f, -SQUARE_SIZE*0.5f, -SQUARE_SIZE*0.5f, SQUARE_SIZE*0.5f, SQUARE_SIZE*0.5f, 0);
				GL11.glTranslatef(0, SQUARE_SIZE, 0);
			}
			GL11.glPopMatrix();
		}
		
		for(GLRat r : rats){
			GL11.glPushMatrix();
				GL11.glTranslatef(r.getX()*SQUARE_SIZE+0.5f*SQUARE_SIZE, r.getY()*SQUARE_SIZE+0.5f*SQUARE_SIZE, 0);
				r.glDraw(time);
			GL11.glPopMatrix();
		}
		
		GL11.glPopMatrix();

	}
	
	public void advanceLogic(Type p1Command, Type p2Command, long time){
		rats.get(0).execute(p1Command, time);
		rats.get(1).execute(p2Command, time);
		
		
		
	}

}
