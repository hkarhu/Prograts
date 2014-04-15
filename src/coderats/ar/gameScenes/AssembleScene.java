package coderats.ar.gameScenes;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import ae.gl.GLValues;
import ae.gl.text.GLBitmapFontBlitter;
import coderats.ar.ARCardSlot;
import coderats.ar.GLDrawableItem;
import coderats.ar.GLRatBoard;

public class AssembleScene extends GameScene {

	private final int LOGIC_STEP_DELAY = 1000;
	
	private ArrayList<ARCardSlot> p1CardSlots;
	private ArrayList<ARCardSlot> p2CardSlots;
	private GLRatBoard gameBoard;
	
	private List<GLDrawableItem> gameItems;
	
	private long logicStepTime = 0;
	private int executeIndex = 0;
	
	public AssembleScene() {
		p1CardSlots = new ArrayList<>();
		p2CardSlots = new ArrayList<>();
		gameBoard = new GLRatBoard();
	
		gameItems = new LinkedList<GLDrawableItem>();
		
		for(int i=0; i < 5; i++){
			p1CardSlots.add(new ARCardSlot(GLValues.glWidth*0.5f - GLValues.glWidth*0.33f+(float)Math.sin((i/4.0f)*Math.PI), GLValues.glHeight*0.4f - 0.2f*i*GLValues.glHeight, 90));
			p2CardSlots.add(new ARCardSlot(GLValues.glWidth*0.5f - GLValues.glWidth*0.66f-(float)Math.sin((i/4.0f)*Math.PI), GLValues.glHeight*0.4f - 0.2f*i*GLValues.glHeight, 270));
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
		
		if(time > logicStepTime){
			stepLogic(time);
			logicStepTime = time + LOGIC_STEP_DELAY;
		}
		
		GL11.glTranslatef(GLValues.glWidth*0.5f, GLValues.glHeight*0.5f, 0);
		
		for(GLDrawableItem i : gameItems){
			i.glDraw(time);
		}
		
		if(time < 3000){
			float in = (3000-time)/3000.0f;
			GL11.glColor4f(1, 1, 1, in);
			
			GLBitmapFontBlitter.blitSinString("ASSEMBLE      ASSEMBLE      ", 0.5f, 0.9f, 1, 1+3*(1-in), 0.9f+(float)Math.sin(time*0.003f)*0.2f, "font_code");
		} else if(time < 10000){
			
		}
		GL11.glPushMatrix();
			GL11.glColor3f(0, 0, 1);
			GLBitmapFontBlitter.blitSinString((char)0x9f+"    ------------"+(char)0x9f+"    ------------", 0.5f, 0.8f, 1, 1.9f, -time*0.001f, "font_default");
		GL11.glPopMatrix();
	}

	private void stepLogic(long time) {
		System.out.println("++");
		executeIndex++;
		if(executeIndex >= 5) executeIndex = 0;
		p1CardSlots.get(executeIndex).activate(time);
		p2CardSlots.get(4-executeIndex).activate(time);
	}

	@Override
	public void processInput(int inputKey) {
	}

}
