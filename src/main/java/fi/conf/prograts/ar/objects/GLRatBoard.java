package fi.conf.prograts.ar.objects;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import fi.conf.prograts.ar.Globals;
import fi.conf.prograts.ar.gameScenes.AssembleScene;
import fi.conf.prograts.ar.gl.GLGraphicRoutines;
import fi.conf.prograts.ar.gl.GLValues;

public class GLRatBoard {

	public static final int BOARD_SIZE = 6;
	
	private long RESET_DELAY;
	private long resetTime = 0;

	private GLRat p1rat;
	private GLRat p2rat;
	
	private List<GLBoardTile> tiles;
	private List<GLPowerup> powerups;
	private List<GLLazor> lazors;
	
	private int boardWidth = 6;
	private int boardHeight = 6;
	
	public GLRatBoard() {
		tiles = new LinkedList<>();
		powerups = new LinkedList<>();
		lazors = new ArrayList<>(4);
		resetGameBoard();
	}
	
	public void resetGameBoard(){
		lazors.clear();
		resetRats();
		
		tiles.clear();
		
		for(int x=0; x < BOARD_SIZE; x++){
			for(int y=0; y < BOARD_SIZE; y++){
				tiles.add(new GLBoardTile(x, y));
			}
		}
		
	}
	
	public void generatePowerups(){
		
	}
	
	public void resetRats(){
		p1rat = new GLRat(1,1,1,"A");
		p2rat = new GLRat(BOARD_SIZE-2,BOARD_SIZE-2,3,"B");
	}

	public void glDraw(long time) {

		GL11.glPushMatrix();

			GL11.glTranslatef(GLValues.glWidth/2, GLValues.glHeight/2, -5f);
			GL11.glTranslatef(-boardWidth*0.5f*Globals.BOARD_TILE_SIZE, -boardHeight*0.5f*Globals.BOARD_TILE_SIZE, 0);
			GL11.glColor4f(1, 1, 1, 1);
			
			for(GLBoardTile t : tiles){
				t.glDraw(time);
			}
			
			for(GLLazor l : lazors){
				GL11.glPushMatrix();
					GL11.glTranslatef(l.getX()*Globals.BOARD_TILE_SIZE+0.5f*Globals.BOARD_TILE_SIZE, l.getY()*Globals.BOARD_TILE_SIZE+0.5f*Globals.BOARD_TILE_SIZE, 0);
					l.glDraw(time);
				GL11.glPopMatrix();
			}
			
			
			GL11.glPushMatrix();
				GL11.glTranslatef(p1rat.getX()*Globals.BOARD_TILE_SIZE+0.5f*Globals.BOARD_TILE_SIZE, p1rat.getY()*Globals.BOARD_TILE_SIZE+0.5f*Globals.BOARD_TILE_SIZE, 0);
				p1rat.glDraw(time);
			GL11.glPopMatrix();
			
			GL11.glPushMatrix();
				GL11.glTranslatef(p2rat.getX()*Globals.BOARD_TILE_SIZE+0.5f*Globals.BOARD_TILE_SIZE, p2rat.getY()*Globals.BOARD_TILE_SIZE+0.5f*Globals.BOARD_TILE_SIZE, 0);
				p2rat.glDraw(time);
			GL11.glPopMatrix();
			
			
		GL11.glPopMatrix();
	
	}
	
	public boolean bothRatsAlive(){
		return p1rat.isAlive() && p2rat.isAlive();
	}
	
	public void advanceLogic(ARCardProgramSlot p1Slot, ARCardProgramSlot p2Slot, long time){
		
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
				p2rat.setLazor(null);
			}
			
		}
		
		for(GLLazor l : lazors){
			if(l.hitsRat(p1rat)){
				p1Slot.breakContainedCard(time);
				//p1rat.takeDamage(time);
			}
			if(l.hitsRat(p2rat)){
				p2Slot.breakContainedCard(time);
				//p2rat.takeDamage(time);
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

	public GLRat getRat(int i) {
		if(i == 1) return p1rat; else return p2rat;
	}

}
