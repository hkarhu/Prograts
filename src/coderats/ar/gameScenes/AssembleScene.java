package coderats.ar.gameScenes;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.lwjgl.opengl.GL11;

import ae.gl.GLValues;
import ae.gl.text.GLBitmapFontBlitter;
import coderats.ar.ARCard;
import coderats.ar.ARCardListener;
import coderats.ar.ARCardSlot;
import coderats.ar.GLDrawableItem;
import coderats.ar.GLRatBoard;

public class AssembleScene extends GameScene {

	private final int LOGIC_STEP_DELAY = 1000;
	
	private ArrayList<ARCardSlot> p1CardSlots;
	private ArrayList<ARCardSlot> p2CardSlots;
	private GLRatBoard gameBoard;
	
	private List<GLDrawableItem> gameItems;
	
	private ConcurrentHashMap<Integer, ARCard> knownCards, p1Cards, p2Cards;
	
	private long logicStepTime = 0;
	private int executeIndex = 0;
	
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
		
		for(GLDrawableItem i : gameItems){
			i.glDraw(time);
		}
		
//		GL11.glPushMatrix();
//		GL11.glRotatef(90, 0, 0, 1);
//		GL11.glTranslatef(-2, -0.3f, 0);
//		GLBitmapFontBlitter.drawString("# Cards", "font_code", GLValues.glWidth*0.014f, GLValues.glWidth*0.02f, GLBitmapFontBlitter.Alignment.CENTERED);
//		GL11.glTranslatef(0, 0.5f, 0);
//		GLBitmapFontBlitter.drawString(p1Cards.size() + "", "font_default", GLValues.glWidth*0.05f, GLValues.glWidth*0.1f, GLBitmapFontBlitter.Alignment.CENTERED);
//		GL11.glPopMatrix();
//		if(time < 3000){
//			float in = (3000-time)/3000.0f;
//			GL11.glColor4f(1, 1, 1, in);
//			
//			GLBitmapFontBlitter.blitSinString("ASSEMBLE      ASSEMBLE      ", 0.5f, 0.9f, 1, 1+3*(1-in), 0.9f+(float)Math.sin(time*0.003f)*0.2f, "font_code");
//		}
//		
//		if(time < 10000){
//			
//		} else {

//			GL11.glPushMatrix();
//				GL11.glColor4f(0, 0, 1, 0.5f);
//				GL11.glTranslatef(0, 0, 4);
//				GLBitmapFontBlitter.blitSinString("    " + "    ", 0.5f, 0.8f, 1, 2.5f, -time*0.001f, "font_default");
//			GL11.glPopMatrix();
//		}
		
		if(time > logicStepTime){
			stepLogic(time);
			logicStepTime = time + LOGIC_STEP_DELAY;
		}
	}

	
	private void stepLogic(long time) {
		copyToMemory();
		runCommand(time);
	}
	
	private void copyToMemory(){
		
		for(ARCardSlot s : p1CardSlots){
			s.setCommand(null);
		}
		
		for(ARCardSlot s : p2CardSlots){
			s.setCommand(null);
		}
		
		if(knownCards.size() >= 1){
			for(Entry<Integer, ARCard> c : knownCards.entrySet()){
				ARCard card = c.getValue();
				
				if(card.getQuality() < 0.8f) continue;
				
				for(ARCardSlot s : p1CardSlots){
					if(s.hits(card)){
						s.setCommand(card.getCommand());
						break;
					}
				}
				
				for(ARCardSlot s : p2CardSlots){
					if(s.hits(card)){
						s.setCommand(card.getCommand());
						break;
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
		gameBoard.advanceLogic(p1slot.getSlottedCommandType(), p2slot.getSlottedCommandType(), time);
		executeIndex++;
		if(executeIndex >= 5) executeIndex = 0;
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

		
	}

}
