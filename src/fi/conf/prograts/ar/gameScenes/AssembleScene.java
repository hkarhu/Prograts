package fi.conf.prograts.ar.gameScenes;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.lwjgl.opengl.GL11;

import fi.conf.prograts.ar.Globals;
import fi.conf.prograts.ar.gl.GLBitmapFontBlitter;
import fi.conf.prograts.ar.gl.GLGraphicRoutines;
import fi.conf.prograts.ar.gl.GLTextureManager;
import fi.conf.prograts.ar.gl.GLValues;
import fi.conf.prograts.ar.objects.ARCard;
import fi.conf.prograts.ar.objects.ARCardProgramSlot;
import fi.conf.prograts.ar.objects.GLRatBoard;
import fi.conf.prograts.ar.objects.Player;

public class AssembleScene extends GameScene {
	
	public final static boolean COMPLEX = false;
	public final static int NUM_LIVES = 3;
	public final static int DANGER_TIME = 5000;
	private final static int LOGIC_STEP_DELAY = 1000;
	private final static int PROGRAM_RUN_DELAY = 15000;
	private final static int RESET_GAME_DELAY = 5000;
	private final static int START_GAME_DELAY = 10000;
	
	private Player p1, p2;
	
	private GLRatBoard gameBoard;
	
	private ConcurrentHashMap<Integer, ARCard> knownCards, p1Cards, p2Cards;
	
	private long logicStepTime = 0;
	private long programRoundTime = 0;
	private long resetTime = 0;
	private boolean progRun = false;
	private int executeIndex = 0;
	private boolean cardsRemovedFromCenter = false;
	
	public AssembleScene(ConcurrentHashMap<Integer, ARCard> knownCards, ConcurrentHashMap<Integer, ARCard> p1Cards, ConcurrentHashMap<Integer, ARCard> p2Cards) {

		this.knownCards = knownCards;
		this.p1Cards = p1Cards;
		this.p2Cards = p2Cards;

		float yShift = GLValues.glHeight*0.5f-((Globals.NUM_SLOTS-1)*0.5f*(Globals.CARD_WIDTH*2+Globals.CARD_ROW_SPACING));
		
		p1 = new Player(GLValues.glWidth*0.33f, yShift, true);
		p2 = new Player(GLValues.glWidth*0.66f, yShift, false);
		
		gameBoard = new GLRatBoard();
		
	}
	
	@Override
	public void init() {
		setRunning(true);
		logicStepTime = 0;
		programRoundTime = 0;
		cardsRemovedFromCenter = false;
		resetTime = 0;
		executeIndex = 0;
		progRun = false;
		p1.reset();
		p2.reset();
		gameBoard.resetGameBoard();
	}

	@Override
	public void glDraw(long time) {
		
		if(!progRun){
			GL11.glPushMatrix();
			if(knownCards.size() >= 1){
				for(Entry<Integer, ARCard> c : knownCards.entrySet()){
					ARCard card = c.getValue();
					if(card.getQuality() >= 0.8f) card.glDraw(time);
				}
			}
			GL11.glPopMatrix();
		}
		
		if(!cardsRemovedFromCenter){
			GL11.glColor4f(0.0f, 0.0f, 0.0f, 1);
			GLTextureManager.unbindTexture();
			GLGraphicRoutines.drawRepeatedBackgroundPlane(1, 1, 1, 1);
			
			GL11.glPushMatrix();
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.5f);
			GL11.glTranslatef(0, 0, 3);
			for(float xi=(float)Math.sin(time*0.0003f)-1f; xi < GLValues.glWidth; xi+=0.3f){
				for(float yi=(float)Math.cos(time*0.0003f)-1f; yi < GLValues.glHeight; yi+=0.3f){
					GL11.glPushMatrix();
					GL11.glTranslatef(xi, yi, 0);
					GL11.glRotatef(time*0.1f, 0, 0, 1);
					GLGraphicRoutines.drawLineRect(1.0f, -0.15f,-0.15f,0.15f,0.15f,0);
					GL11.glPopMatrix();
					//GLGraphicRoutines.drawLineRect(1.0f, xi-0.15f,yi-0.15f,xi+0.15f,yi+0.15f,0);
				}
			}
			GL11.glPopMatrix();
			
			GL11.glColor4f(1,1,1,1);
			GL11.glPushMatrix();
				GL11.glTranslatef(GLValues.glWidth*0.5f, GLValues.glHeight*0.5f, 1);
				GL11.glColor3f(1,1,1);
				GLGraphicRoutines.drawLineCircle(GLValues.glHeight/2, 35, 2f);
				GL11.glColor3f(0,0,0);
				GLGraphicRoutines.drawCircle(GLValues.glHeight/2, 35);
				GL11.glColor3f(1,1,1);
				GL11.glTranslatef(0, 0, -5);
				GLBitmapFontBlitter.blitSinString("Get your cards off from the center      Get your cards off from the center      ", 0.5f, 0.9f, 1, 2.4f, 0.9f+(float)(time*0.001f)*0.2f, "font_code");
				GLBitmapFontBlitter.blitSinString("vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv", 0.5f, 0.9f, 1, 1.25f+(float) (0.25f*(1+Math.sin(time*0.002f))), 0.9f+(float)(time*0.001f)*0.2f, "font_code");
			GL11.glPopMatrix();
			
			programRoundTime = time + PROGRAM_RUN_DELAY + START_GAME_DELAY;
			progRun = false;
		} else {
			
			//Kauhee rainbowbackgroundplasma
//			GL11.glPushMatrix();
//			GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
//			GL11.glTranslatef(0, 0, -GLValues.glDepth);
//			for(float xi=0; xi < GLValues.glWidth; xi+=0.1f){
//				for(float yi=0; yi < GLValues.glHeight; yi+=0.1f){
//					float at = 0.0003f*time;
//					float h = 0.4f;
//					float d = (float) (Math.pow(xi*h+yi*h, Math.log(yi*h)*Math.sin(yi*h+at))+Math.sin(at+xi*h+yi*h));
//					Color C = Color.getHSBColor(d,1,1);
//					GL11.glColor4f(C.getRed()/255.0f, C.getGreen()/255.0f, C.getBlue()/255.0f, 0.4f);
//					GLGraphicRoutines.draw2DRect(xi,yi,xi+0.1f,yi+0.1f,0);
//				}
//			}
//			GL11.glPopMatrix();
			
			gameBoard.glDraw(time);
			
			p1.glDraw(time);
			p2.glDraw(time);
			
			GL11.glPushMatrix();
				GL11.glTranslatef(GLValues.glWidth*0.5f, GLValues.glHeight*0.5f, 0);
				
				if(!progRun){
					if(programRoundTime - time < DANGER_TIME){
						GL11.glColor4f(1, 0, 0, (float)(0.5f*Math.sin(time*0.004f*Math.PI)));
					} else {
						GL11.glColor4f(1, 1, 1, 1);
					}
					GL11.glPushMatrix();
						GL11.glRotatef(90, 0, 0, 1);
						GL11.glTranslatef(0, 1.25f, 0);
						GLBitmapFontBlitter.drawString("A ["+(int)((programRoundTime - time)*0.001f + 1)+ "]", "font_default", GLValues.glWidth*0.03f, GLValues.glWidth*0.06f, GLBitmapFontBlitter.Alignment.CENTERED);
					GL11.glPopMatrix();
					GL11.glPushMatrix();
						GL11.glRotatef(270, 0, 0, 1);
						GL11.glTranslatef(0, 1.25f, 0);
						GLBitmapFontBlitter.drawString("B ["+(int)((programRoundTime - time)*0.001f + 1)+ "]", "font_default", GLValues.glWidth*0.03f, GLValues.glWidth*0.06f, GLBitmapFontBlitter.Alignment.CENTERED);
					GL11.glPopMatrix();
					
				} else if (gameBoard.bothRatsAlive()){
					
					GL11.glPushMatrix();
						GL11.glColor4f(0, 1, 0, 0.3f);
						GL11.glTranslatef(0, 0, 4);
						GLBitmapFontBlitter.blitSinString("    " + "    ", 0.5f, 0.75f, 1, 1.5f, (float)(Math.PI-Math.tan(time/(float)LOGIC_STEP_DELAY*Math.PI)*0.2f), "font_default");
					GL11.glPopMatrix();
					
					GL11.glColor4f(0, 1, 0, 1);
					GL11.glPushMatrix();
						GL11.glRotatef(90, 0, 0, 1);
						GL11.glTranslatef(0, 1.25f, -5);
						GLBitmapFontBlitter.drawString("EXEC", "font_default", GLValues.glWidth*0.03f, GLValues.glWidth*0.06f, GLBitmapFontBlitter.Alignment.CENTERED);
					GL11.glPopMatrix();
					GL11.glPushMatrix();
						GL11.glRotatef(270, 0, 0, 1);
						GL11.glTranslatef(0, 1.25f, -5);
						GLBitmapFontBlitter.drawString("EXEC", "font_default", GLValues.glWidth*0.03f, GLValues.glWidth*0.06f, GLBitmapFontBlitter.Alignment.CENTERED);
					GL11.glPopMatrix();
				}
				
				GL11.glColor4f(1, 1, 1, 1);
				
//				GL11.glPushMatrix();
//					GL11.glRotatef(-90, 0, 0, 1);
//					GL11.glTranslatef(-2, -0.3f, 0);
//					GLBitmapFontBlitter.drawString("Rats left", "font_code", GLValues.glWidth*0.014f, GLValues.glWidth*0.02f, GLBitmapFontBlitter.Alignment.CENTERED);
//					GL11.glTranslatef(0, 0.45f, 0);
//					GLBitmapFontBlitter.drawString(gameBoard.getLives(2) + "", "font_default", GLValues.glWidth*0.05f, GLValues.glWidth*0.1f, GLBitmapFontBlitter.Alignment.CENTERED);
//				GL11.glPopMatrix();
//				
//				GL11.glPushMatrix();
//					GL11.glRotatef(90, 0, 0, 1);
//					GL11.glTranslatef(-2, -0.3f, 0);
//					GLBitmapFontBlitter.drawString("", "font_code", GLValues.glWidth*0.014f, GLValues.glWidth*0.02f, GLBitmapFontBlitter.Alignment.CENTERED);
//					GL11.glTranslatef(0, 0.45f, 0);
//					GLBitmapFontBlitter.drawString(gameBoard.getLives(1) + "", "font_default", GLValues.glWidth*0.05f, GLValues.glWidth*0.1f, GLBitmapFontBlitter.Alignment.CENTERED);
//				GL11.glPopMatrix();
				
			GL11.glPopMatrix();

			if(gameBoard.bothRatsAlive()){
				if(progRun){ //Run Program
					p1.getCardSlot(executeIndex).highlight();
					p2.getCardSlot(Globals.NUM_SLOTS-1-executeIndex).highlight();
					if(time > logicStepTime){
						runCommand(time);
						logicStepTime = time + LOGIC_STEP_DELAY;
					}
				} else { //Plan and assemble
					if(time > logicStepTime){
						copyToMemory();
						logicStepTime = time + LOGIC_STEP_DELAY;
					}
					if(time > programRoundTime){
						progRun = true;
						programRoundTime = time + PROGRAM_RUN_DELAY + LOGIC_STEP_DELAY*Globals.NUM_SLOTS;
					}
				}
			} else {
				GL11.glPushMatrix();
					GL11.glTranslatef(GLValues.glWidth*0.5f, GLValues.glHeight*0.5f, -5);
					GL11.glColor4f(1, 0, 0, 1);
					if(gameBoard.getRat(0).isAlive()){
						GL11.glPushMatrix();
							GL11.glRotatef(90, 0, 0, 1);
							GL11.glTranslatef(0, GLValues.glWidth*0.6f, 0);
							GLBitmapFontBlitter.drawString("! OFFLINE !", "font_default", GLValues.glWidth*0.03f, GLValues.glWidth*0.06f, GLBitmapFontBlitter.Alignment.CENTERED);
						GL11.glPopMatrix();
					}
					if(gameBoard.getRat(1).isAlive()){
						GL11.glPushMatrix();
							GL11.glRotatef(270, 0, 0, 1);
							GL11.glTranslatef(0, GLValues.glWidth*0.6f, 0);
							GLBitmapFontBlitter.drawString("! OFFLINE !", "font_default", GLValues.glWidth*0.03f, GLValues.glWidth*0.06f, GLBitmapFontBlitter.Alignment.CENTERED);
						GL11.glPopMatrix();
					}
				GL11.glPopMatrix();
				if(time > resetTime){
					this.setRunning(false);
				}
			}
		}
	}

	private void copyToMemory(){
		
		for(ARCardProgramSlot s : p1.getCardSlots()){
			s.bindCard(null);
		}
		
		for(ARCardProgramSlot s : p2.getCardSlots()){
			s.bindCard(null);
		}
		
		if(knownCards.size() >= 1){
			for(Entry<Integer, ARCard> c : knownCards.entrySet()){
				ARCard card = c.getValue();
				
				if(card.getQuality() < 0.8f) continue;
				
				for(ARCardProgramSlot s : p1.getCardSlots()){
					if(s.hits(card)){
						s.bindCard(card);
						break;
					}
				}
				
				for(ARCardProgramSlot s : p2.getCardSlots()){
					if(s.hits(card)){
						s.bindCard(card);
						break;
					}
				}
				
			}
		}
	}
	
	private void runCommand(long time){
		
		ARCardProgramSlot p1slot = p1.getCardSlot(executeIndex);
		ARCardProgramSlot p2slot = p2.getCardSlot(Globals.NUM_SLOTS-1-executeIndex);
		p1slot.activate(time);
		p2slot.activate(time);
		
		gameBoard.advanceLogic(p1slot, p2slot, time);
		
		executeIndex++;
		if(executeIndex >= Globals.NUM_SLOTS){
			executeIndex = 0;
			progRun = false;
		}
		
		if(!gameBoard.bothRatsAlive()){
			resetTime = time + RESET_GAME_DELAY;
			progRun = false;
		}
		
	}
	
	private void swapMemory(){
		
	}

	@Override
	public void processInput(int inputKey) {
		switch (inputKey) {
			case 19: gameBoard.resetGameBoard(); break; //R
			case 16: cardsRemovedFromCenter = true; break; //Q
			case 45: progRun = true; break; //X
			default: break;
		} 
	}

	@Override
	public void cardAppeared(int id) {
		cardDataUpdated(id);
	}

	@Override
	public void cardDataUpdated(int id) {
		if(!cardsRemovedFromCenter){
			for(ARCard c : knownCards.values()){
				if(c.getQuality() < 0.8f) continue;
				float d = (float) Math.sqrt(Math.pow(GLValues.glWidth/2.0f - c.getX(), 2) + Math.pow(GLValues.glHeight/2.0f - c.getY(), 2));
				if(d < 2.5f){
					return;
				}
			}
			cardsRemovedFromCenter = true;
			progRun = false;
		}
	}

}
