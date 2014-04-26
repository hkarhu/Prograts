package coderats.ar.gameScenes;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.lwjgl.opengl.GL11;

import ae.gl.GLGraphicRoutines;
import ae.gl.GLValues;
import ae.gl.text.GLBitmapFontBlitter;
import ae.gl.texture.GLTextureManager;
import coderats.ar.ARCard;
import coderats.ar.ARCardSlot;
import coderats.ar.GLDrawableItem;
import coderats.ar.GLRatBoard;

public class AssembleScene extends GameScene {
	
	public final static boolean COMPLEX = false;
	private final static int NUM_SLOTS = 5;
	public final static int NUM_LIVES = 3;
	private final static int LOGIC_STEP_DELAY = 500;
	private final static int PROGRAM_RUN_DELAY = 20000;
	private final static int RESET_GAME_DELAY = 5000;
	private final static int START_GAME_DELAY = 10000;
	
	private ArrayList<ARCardSlot> p1CardSlots;
	private ArrayList<ARCardSlot> p2CardSlots;
	
	private ArrayList<ARCardSlot> p1CardSlots2;
	private ArrayList<ARCardSlot> p2CardSlots2;
	
	private GLRatBoard gameBoard;
	
	private List<GLDrawableItem> gameItems;
	
	private List<GLDrawableItem> gameItemsC;
	
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
		
		p1CardSlots = new ArrayList<>();
		p2CardSlots = new ArrayList<>();
		p1CardSlots2 = new ArrayList<>();
		p2CardSlots2 = new ArrayList<>();
		gameBoard = new GLRatBoard();
	
		gameItems = new LinkedList<GLDrawableItem>();
		gameItemsC = new LinkedList<GLDrawableItem>();
		
		for(int i=0; i < NUM_SLOTS; i++){
			p1CardSlots.add(new ARCardSlot(GLValues.glWidth*0.33f-(float)Math.sin((i/(float)(NUM_SLOTS-1))*Math.PI), GLValues.glHeight*0.1f+0.2f*i*GLValues.glHeight, 90));
			p2CardSlots.add(new ARCardSlot(GLValues.glWidth*0.66f+(float)Math.sin((i/(float)(NUM_SLOTS-1))*Math.PI), GLValues.glHeight*0.1f+0.2f*i*GLValues.glHeight, 270));
		}
		
		//TODO: Wheeee
		for(int i=0; i < NUM_SLOTS; i++){
			p1CardSlots2.add(new ARCardSlot(GLValues.glWidth*0.15f-(float)Math.sin((i/(float)(NUM_SLOTS-1))*Math.PI), GLValues.glHeight*0.1f+0.2f*i*GLValues.glHeight, 90));
			p2CardSlots2.add(new ARCardSlot(GLValues.glWidth*0.85f+(float)Math.sin((i/(float)(NUM_SLOTS-1))*Math.PI), GLValues.glHeight*0.1f+0.2f*i*GLValues.glHeight, 270));
		}
		
		for(ARCardSlot c : p1CardSlots) gameItems.add(c);
		for(ARCardSlot c : p2CardSlots) gameItems.add(c);
		
		//TODO complex
		for(ARCardSlot c : p1CardSlots2) gameItemsC.add(c);
		for(ARCardSlot c : p2CardSlots2) gameItemsC.add(c);
		
		gameItems.add(gameBoard);
		
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
		for(int i=0; i < NUM_SLOTS; i++){
			p1CardSlots.get(i).reset();
			p2CardSlots.get(i).reset();
			//TODO complex
			p1CardSlots2.get(i).reset();
			p2CardSlots2.get(i).reset();
		}
		gameBoard.resetGameBoard();
	}

	@Override
	public void glDraw(long time) {
		
		GL11.glPushMatrix();
		if(knownCards.size() >= 1){
			for(Entry<Integer, ARCard> c : knownCards.entrySet()){
				ARCard card = c.getValue();
				if(card.getQuality() >= 0.8f) card.glDraw(time);
			}
		}
		GL11.glPopMatrix();
		
		if(!cardsRemovedFromCenter){
			GL11.glColor4f(0.2f, 0.2f, 0.2f, 1);
			GLTextureManager.unbindTexture();
			GLGraphicRoutines.drawRepeatedBackgroundPlane(1, 1, 1, 1);
			GL11.glColor4f(1,1,1,1);
			GL11.glPushMatrix();
				GL11.glTranslatef(GLValues.glWidth*0.5f, GLValues.glHeight*0.5f, 0);
				GLBitmapFontBlitter.blitSinString("READY?      READY?      ", 0.5f, 0.9f, 1, 1.5f, 0.9f+(float)Math.sin(time*0.003f)*0.2f, "font_code");
				GLBitmapFontBlitter.blitSinString("Get your cards off from the center      Get your cards off from the center      ", 0.5f, 0.9f, 1, 2.5f, 0.9f+(float)(time*0.001f)*0.2f, "font_code");
			GL11.glPopMatrix();
			
			programRoundTime = time + PROGRAM_RUN_DELAY + START_GAME_DELAY;
			progRun = false;
		} else {
			
			for(GLDrawableItem i : gameItems){
				i.glDraw(time);
			}

			//TODO complex
			if(COMPLEX){
				for(GLDrawableItem i : gameItemsC){
					i.glDraw(time);
				}
			}
			
			GL11.glPushMatrix();
				GL11.glTranslatef(GLValues.glWidth*0.5f, GLValues.glHeight*0.5f, 0);
				
				if(progRun){
					GL11.glPushMatrix();
						GL11.glColor4f(0, 1, 0, 0.3f);
						GL11.glTranslatef(0, 0, 4);
						GLBitmapFontBlitter.blitSinString("    " + "    ", 0.5f, 0.75f, 1, 1.5f, (float)(Math.PI-Math.tan(time/(float)LOGIC_STEP_DELAY*Math.PI)*0.2f), "font_default");
					GL11.glPopMatrix();
				}
				
				if(!progRun){
					if((int)((programRoundTime - time)*0.001f + 1) < 5){
						GL11.glColor4f(1, 0, 0, (float)(0.5f*Math.sin(time*0.004f*Math.PI)));
					} else {
						GL11.glColor4f(1, 1, 1, 1);
					}
					GL11.glPushMatrix();
						GL11.glRotatef(90, 0, 0, 1);
						GL11.glTranslatef(0, 1.25f, 0);
						GLBitmapFontBlitter.drawString("["+(int)((programRoundTime - time)*0.001f + 1)+ "]", "font_default", GLValues.glWidth*0.03f, GLValues.glWidth*0.06f, GLBitmapFontBlitter.Alignment.CENTERED);
					GL11.glPopMatrix();
					GL11.glPushMatrix();
						GL11.glRotatef(270, 0, 0, 1);
						GL11.glTranslatef(0, 1.25f, 0);
						GLBitmapFontBlitter.drawString("["+(int)((programRoundTime - time)*0.001f + 1)+ "]", "font_default", GLValues.glWidth*0.03f, GLValues.glWidth*0.06f, GLBitmapFontBlitter.Alignment.CENTERED);
					GL11.glPopMatrix();
				} else if (gameBoard.bothRatsAlive()){
					GL11.glColor4f(0, 1, 0, 1);
					GL11.glPushMatrix();
						GL11.glRotatef(90, 0, 0, 1);
						GL11.glTranslatef(0, 1.5f, -5);
						GLBitmapFontBlitter.drawString("! EXEC !", "font_default", GLValues.glWidth*0.03f, GLValues.glWidth*0.06f, GLBitmapFontBlitter.Alignment.CENTERED);
					GL11.glPopMatrix();
					GL11.glPushMatrix();
						GL11.glRotatef(270, 0, 0, 1);
						GL11.glTranslatef(0, 1.5f, -5);
						GLBitmapFontBlitter.drawString("! EXEC !", "font_default", GLValues.glWidth*0.03f, GLValues.glWidth*0.06f, GLBitmapFontBlitter.Alignment.CENTERED);
					GL11.glPopMatrix();
				}
				
				GL11.glColor4f(1, 1, 1, 1);
				
//				GL11.glPushMatrix();
//					GL11.glRotatef(-90, 0, 0, 1);
//					GL11.glTranslatef(-2, -0.3f, 0);
//					GLBitmapFontBlitter.drawString("", "font_code", GLValues.glWidth*0.014f, GLValues.glWidth*0.02f, GLBitmapFontBlitter.Alignment.CENTERED);
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
					p1CardSlots.get(executeIndex).highlight();
					p2CardSlots.get(4-executeIndex).highlight();
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
						programRoundTime = time + PROGRAM_RUN_DELAY + LOGIC_STEP_DELAY*NUM_SLOTS;
					}
				}
			} else {
				GL11.glPushMatrix();
					GL11.glTranslatef(GLValues.glWidth*0.5f, GLValues.glHeight*0.5f, -5);
					GL11.glColor4f(1, 0, 0, 1);
					if(gameBoard.getRat(0).isAlive()){
						GL11.glPushMatrix();
							GL11.glRotatef(90, 0, 0, 1);
							GL11.glTranslatef(0, 1.5f, 0);
							GLBitmapFontBlitter.drawString("! OFFLINE !", "font_default", GLValues.glWidth*0.03f, GLValues.glWidth*0.06f, GLBitmapFontBlitter.Alignment.CENTERED);
						GL11.glPopMatrix();
					}
					if(gameBoard.getRat(1).isAlive()){
						GL11.glPushMatrix();
							GL11.glRotatef(270, 0, 0, 1);
							GL11.glTranslatef(0, 1.5f, 0);
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
		
		for(ARCardSlot s : p1CardSlots){
			s.bindCard(null);
		}
		
		for(ARCardSlot s : p2CardSlots){
			s.bindCard(null);
		}
		
		if(COMPLEX){
			for(ARCardSlot s : p1CardSlots2){
				s.bindCard(null);
			}
			
			for(ARCardSlot s : p2CardSlots2){
				s.bindCard(null);
			}
		}
		
		if(knownCards.size() >= 1){
			for(Entry<Integer, ARCard> c : knownCards.entrySet()){
				ARCard card = c.getValue();
				
				if(card.getQuality() < 0.8f) continue;
				
				for(ARCardSlot s : p1CardSlots){
					if(s.hits(card)){
						s.bindCard(card);
						break;
					}
				}
				
				for(ARCardSlot s : p2CardSlots){
					if(s.hits(card)){
						s.bindCard(card);
						break;
					}
				}
				
				//TODO complex
				if(COMPLEX){
					for(ARCardSlot s : p1CardSlots2){
						if(s.hits(card)){
							s.bindCard(card);
							break;
						}
					}
					
					for(ARCardSlot s : p2CardSlots2){
						if(s.hits(card)){
							s.bindCard(card);
							break;
						}
					}
				}
				
			}
		}
	}
	
	private void runCommand(long time){
		
		ARCardSlot p1slot = p1CardSlots.get(executeIndex);
		ARCardSlot p2slot = p2CardSlots.get(4-executeIndex);
		p1slot.activate(time);
		p2slot.activate(time);
		
		gameBoard.advanceLogic(p1slot, p2slot, null, null, time);

		if(COMPLEX) {
			ARCardSlot p1slot2 = p1CardSlots.get(executeIndex);
			ARCardSlot p2slot2 = p2CardSlots.get(4-executeIndex);
			p1slot.activate(time);
			p2slot.activate(time);
			gameBoard.advanceLogic(p1slot, p2slot, p1slot2, p2slot2, time);
		}
		
		executeIndex++;
		if(executeIndex >= NUM_SLOTS){
			executeIndex = 0;
			progRun = false;
		}
		
		if(!gameBoard.bothRatsAlive()){
			resetTime = time + RESET_GAME_DELAY;
			progRun = false;
		}
		
	}

	@Override
	public void processInput(int inputKey) {
		switch (inputKey) {
			case 19: gameBoard.resetGameBoard(); break; //R
			case 16: cardsRemovedFromCenter = true; break;//Q
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
