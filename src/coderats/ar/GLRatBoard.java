package coderats.ar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.crypto.dsig.keyinfo.RetrievalMethod;

import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;

import coderats.ar.Command.Type;
import coderats.ar.gameScenes.AssembleScene;
import ae.gl.GLGraphicRoutines;
import ae.gl.GLValues;

public class GLRatBoard extends GLDrawableItem {

	public static final int BOARD_SIZE = 6;
	public static final float SQUARE_SIZE = 0.3f;
	
	private long RESET_DELAY;
	private long resetTime = 0;

	private int p1Lives = AssembleScene.NUM_LIVES;
	private int p2Lives = AssembleScene.NUM_LIVES;
	
	private GLRat p1rat;
	private GLRat p2rat;
	private List<GLLazor> lazors;
	
	public GLRatBoard() {
		lazors = new ArrayList<>(2);
		resetGameBoard();
	}
	
	public void resetGameBoard(){
		p1Lives = AssembleScene.NUM_LIVES;
		p2Lives = AssembleScene.NUM_LIVES;
		lazors.clear();
		resetRats();
	}
	
	public void resetRats(){
		
		if(p1rat != null && !p1rat.isAlive()){
			p1Lives--;
		}
		if(p2rat != null && !p2rat.isAlive()){
			p2Lives--;
		}
		p1rat = new GLRat(1,1,1,"A");
		p2rat = new GLRat(BOARD_SIZE-2,BOARD_SIZE-2,3,"B");
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
			
			
			GL11.glPushMatrix();
				GL11.glTranslatef(p1rat.getX()*SQUARE_SIZE+0.5f*SQUARE_SIZE, p1rat.getY()*SQUARE_SIZE+0.5f*SQUARE_SIZE, 0);
				p1rat.glDraw(time);
			GL11.glPopMatrix();
			
			GL11.glPushMatrix();
				GL11.glTranslatef(p2rat.getX()*SQUARE_SIZE+0.5f*SQUARE_SIZE, p2rat.getY()*SQUARE_SIZE+0.5f*SQUARE_SIZE, 0);
				p2rat.glDraw(time);
			GL11.glPopMatrix();
			
			
		GL11.glPopMatrix();
	
	}
	
	public boolean bothRatsAlive(){
		return p1rat.isAlive() && p2rat.isAlive();
	}
	
	public void advanceLogic(ARCardSlot p1Slot, ARCardSlot p2Slot, long time){
		
		lazors.clear();
		
		if(p1rat.isAlive()){
			p1rat.execute(p1Slot.getSlottedCommandType(), time);
			GLLazor p1Lazor = p1rat.getLazor();
			if(p1Lazor != null){
				p1Lazor.setTime(time);
				lazors.add(p1Lazor);
				p1rat.setLazor(null);
			}
		}
		
		if(p2rat.isAlive()){
			p2rat.execute(p2Slot.getSlottedCommandType(), time);
			GLLazor p2Lazor = p2rat.getLazor();
			if(p2Lazor != null){
				p2Lazor.setTime(time);
				lazors.add(p2Lazor);
				p1rat.setLazor(null);
			}
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
		return glRat.getX() >= BOARD_SIZE || glRat.getX() < 0 || glRat.getY() >= BOARD_SIZE || glRat.getY() < 0;
	}

	public boolean returnToAllocate() {
		return p1Lives <= 0 || p2Lives <= 0;
	}

	public int getLives(int p) {
		if(p == 1) return p1Lives; else return p2Lives;
	}

}
