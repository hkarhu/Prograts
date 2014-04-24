package coderats.ar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;

import coderats.ar.Command.Type;
import ae.gl.GLGraphicRoutines;
import ae.gl.GLValues;

public class GLRatBoard extends GLDrawableItem {

	public static final int BOARD_SIZE = 6;
	public static final float SQUARE_SIZE = 0.3f;
	
	private long RESET_DELAY;
	private long resetTime = 0;

	private List<GLRat> rats;
	private List<GLLazor> lazors;
	
	public GLRatBoard() {
		rats = new ArrayList<>(2);
		lazors = new ArrayList<>(2);
		resetGameBoard();
	}
	
	public void resetGameBoard(){
		rats.clear();
		lazors.clear();
		rats.add(new GLRat(1,1,1,"A"));
		rats.add(new GLRat(BOARD_SIZE-2,BOARD_SIZE-2,3,"B"));
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
			
			for(GLLazor l : lazors){
				GL11.glPushMatrix();
					GL11.glTranslatef(l.getX()*SQUARE_SIZE+0.5f*SQUARE_SIZE, l.getY()*SQUARE_SIZE+0.5f*SQUARE_SIZE, 0);
					l.glDraw(time);
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
	
	public void advanceLogic(ARCardSlot p1Slot, ARCardSlot p2Slot, long time){
		
		lazors.clear();
		
		GLRat p1rat = rats.get(0);
		GLRat p2rat = rats.get(1);
		
		if(p1rat.isAlive()){
			p1rat.execute(p1Slot.getSlottedCommandType(), time);
		}
		
		if(p2rat.isAlive()){
			p2rat.execute(p2Slot.getSlottedCommandType(), time);
		}
		
		if(p1rat.isShooting()){
			lazors.add(new GLLazor(p1rat.getX(), p1rat.getY(), p1rat.getRotation(), time));
		}
		
		if(p2rat.isShooting()){
			lazors.add(new GLLazor(p2rat.getX(), p2rat.getY(), p2rat.getRotation(), time));
		}
		
		for(GLLazor l : lazors){
			if(l.hitsRat(p1rat)){
				p1Slot.breakContainedCard(time);
			}
			if(l.hitsRat(p2rat)){
				p2Slot.breakContainedCard(time);
			}
		}
		
		if(ratOutsideBoard(p1rat)){
			p1rat.setAlive(false);
		}
		
		if(ratOutsideBoard(p2rat)){
			p2rat.setAlive(false);
		}
		
	}
	
	private boolean ratOutsideBoard(GLRat glRat) {
		return glRat.getX() >= 8 || glRat.getX() < 0 || glRat.getY() >= 8 || glRat.getY() < 0;
	}

	public boolean returnToAllocate() {
		// TODO Auto-generated method stub
		return false;
	}

}
