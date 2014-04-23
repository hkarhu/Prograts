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
import coderats.ar.ARCardListener;
import coderats.ar.ARCardSlot;
import coderats.ar.Command;
import coderats.ar.GLDrawableItem;
import coderats.ar.GLRatBoard;

public class AssembleScene extends GameScene {

	private final int LOGIC_STEP_DELAY = 1500;
	private final int PROGRAM_SWEEP_DELAY = 5000;
	private final int START_DELAY = 10000;

	private ArrayList<ARCardSlot> p1CardSlots;
	private ArrayList<ARCardSlot> p2CardSlots;
	private GLRatBoard gameBoard;
	
	private long startingTime = 0;
	
	private List<GLDrawableItem> gameItems;
	
	private ConcurrentHashMap<Integer, ARCard> knownCards, p1Cards, p2Cards;
	
	private long logicStepTime = 0;
	private long roundTime = 0;
	private int executeIndex = 0;
	private boolean cardsRemovedFromCenter = false;
	
	public AssembleScene(ConcurrentHashMap<Integer, ARCard> knownCards, ConcurrentHashMap<Integer, ARCard> p1Cards, ConcurrentHashMap<Integer, ARCard> p2Cards) {

		this.knownCards = knownCards;
		this.p1Cards = p1Cards;
		this.p2Cards = p2Cards;
		
		p1CardSlots = new ArrayList<>();
		p2CardSlots = new ArrayList<>();
		gameBoard = new GLRatBoard();
	
		gameItems = new LinkedList<GLDrawableItem>();
		
		for(int i=0; i < 5; i++){
			p1CardSlots.add(new ARCardSlot(GLValues.glWidth*0.33f-(float)Math.sin((i/4.0f)*Math.PI), GLValues.glHeight*0.1f+0.2f*i*GLValues.glHeight, 90));
			p2CardSlots.add(new ARCardSlot(GLValues.glWidth*0.66f+(float)Math.sin((i/4.0f)*Math.PI), GLValues.glHeight*0.1f+0.2f*i*GLValues.glHeight, 270));
		}
		
		for(ARCardSlot c : p1CardSlots) gameItems.add(c);
		for(ARCardSlot c : p2CardSlots) gameItems.add(c);
		
		gameItems.add(gameBoard);
		
	}
	
	@Override
	public void init() {
		setRunning(true);
		logicStepTime = 0;
		roundTime = 0;
		cardsRemovedFromCenter = false;
		startingTime = 0;
		executeIndex = 0;
		for(int i=0; i < 5; i++){
			p1CardSlots.get(i).reset();
			p2CardSlots.get(i).reset();
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
			startingTime = time;
			GL11.glColor4f(0.2f, 0.2f, 0.2f, 1);
			GLTextureManager.unbindTexture();
			GLGraphicRoutines.drawRepeatedBackgroundPlane(1, 1, 1, 1);
			GL11.glColor4f(1,1,1,1);
			GL11.glPushMatrix();
				GL11.glTranslatef(GLValues.glWidth*0.5f, GLValues.glHeight*0.5f, 0);
				GLBitmapFontBlitter.blitSinString("READY?      READY?      ", 0.5f, 0.9f, 1, 1.5f, 0.9f+(float)Math.sin(time*0.003f)*0.2f, "font_code");
				GLBitmapFontBlitter.blitSinString("Get your cards off from the center      Get your cards off from the center      ", 0.5f, 0.9f, 1, 2.5f, 0.9f+(float)(time*0.001f)*0.2f, "font_code");
			GL11.glPopMatrix();
		} else if(time-startingTime < START_DELAY){
			
			for(ARCardSlot s : p1CardSlots){
				s.glDraw(time);
			}
			
			for(ARCardSlot s : p2CardSlots){
				s.glDraw(time);
			}
			
			float in = (START_DELAY-time-startingTime)/(float)START_DELAY;
			
			GL11.glPushMatrix();
				GL11.glTranslatef(GLValues.glWidth*0.5f, GLValues.glHeight*0.5f, 0);
				GL11.glColor4f(1, 1, 1, 1f);
				GL11.glTranslatef(0, 0, -4);
				GLBitmapFontBlitter.blitSinString("Running programs in " + (int)(in*30) + " seconds... " + "Running programs in " + (int)(in*30) + " seconds... ", 0.5f, 0.9f, 1, 1.5f, 0.9f+(float)(time*0.003f)*0.2f, "font_code");
				GL11.glColor4f(1, 1, 1, 0.3f);
				GLBitmapFontBlitter.blitSinString("                  ", 0.5f, 0.8f, 1, 2f, -time/(float)LOGIC_STEP_DELAY*0.5f, "font_default");
			GL11.glPopMatrix();
			
		} else {
			
			for(GLDrawableItem i : gameItems){
				i.glDraw(time);
			}

			GL11.glPushMatrix();
				GL11.glTranslatef(GLValues.glWidth*0.5f, GLValues.glHeight*0.5f, 0);
				
				GL11.glPushMatrix();
					GL11.glColor4f(0, 1, 0, 0.3f);
					GL11.glTranslatef(0, 0, 4);
					GLBitmapFontBlitter.blitSinString("    " + "    ", 0.5f, 0.8f, 1, 2f, (float)(Math.PI-Math.tan(time/(float)LOGIC_STEP_DELAY*Math.PI)*0.2f), "font_default");
				GL11.glPopMatrix();
				
				GL11.glColor4f(1, 1, 1, 1);
				GL11.glPushMatrix();
					GL11.glRotatef(90, 0, 0, 1);
					GL11.glTranslatef(-2, -0.3f, 0);
					GLBitmapFontBlitter.drawString("# Cards", "font_code", GLValues.glWidth*0.014f, GLValues.glWidth*0.02f, GLBitmapFontBlitter.Alignment.CENTERED);
					GL11.glTranslatef(0, 0.45f, 0);
					GLBitmapFontBlitter.drawString(p2Cards.keySet().size() + "", "font_default", GLValues.glWidth*0.05f, GLValues.glWidth*0.1f, GLBitmapFontBlitter.Alignment.CENTERED);
				GL11.glPopMatrix();
				GL11.glPushMatrix();
					GL11.glRotatef(-90, 0, 0, 1);
					GL11.glTranslatef(-2, -0.3f, 0);
					GLBitmapFontBlitter.drawString("# Cards", "font_code", GLValues.glWidth*0.014f, GLValues.glWidth*0.02f, GLBitmapFontBlitter.Alignment.CENTERED);
					GL11.glTranslatef(0, 0.45f, 0);
					GLBitmapFontBlitter.drawString(p1Cards.keySet().size() + "", "font_default", GLValues.glWidth*0.05f, GLValues.glWidth*0.1f, GLBitmapFontBlitter.Alignment.CENTERED);
				GL11.glPopMatrix();
			GL11.glPopMatrix();
			
			if(time > roundTime){
				if(time > logicStepTime){
					stepLogic(time);
					logicStepTime = time + LOGIC_STEP_DELAY;
				}
			}
		}
	}


	private void stepLogic(long time) {
		copyToMemory();
		runCommand(time);
	}
	
	private void copyToMemory(){
		
		for(ARCardSlot s : p1CardSlots){
			s.bindCard(null);
		}
		
		for(ARCardSlot s : p2CardSlots){
			s.bindCard(null);
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
				
			}
		}
	}
	
	private void runCommand(long time){
		if(gameBoard.returnToAllocate()){
			setRunning(false);
		}
		ARCardSlot p1slot = p1CardSlots.get(executeIndex);
		ARCardSlot p2slot = p2CardSlots.get(4-executeIndex);
		p1slot.activate(time);
		p2slot.activate(time);
		gameBoard.advanceLogic(p1slot, p2slot, time);
		executeIndex++;
		if(executeIndex >= 5) executeIndex = 0;
		
		p1CardSlots.get(executeIndex).highlight();
		p2CardSlots.get(4-executeIndex).highlight();
		
	}

	@Override
	public void processInput(int inputKey) {
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
		}
	}

}
