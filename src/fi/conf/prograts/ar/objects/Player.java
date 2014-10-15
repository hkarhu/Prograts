package fi.conf.prograts.ar.objects;

import java.util.ArrayList;

import fi.conf.prograts.ar.Globals;

public class Player {
	
	private ArrayList<ARCardProgramSlot> cardSlots = new ArrayList<>();
	private ARCardMemSlot memSlot;
	
	private float xShift, yShift, rotation;
	
	public Player(float xShift, float yShift, boolean side) {
		this.xShift = xShift;
		this.yShift = yShift;
		this.rotation = (side ? 90 : 270);
		
		if(side){
			memSlot = new ARCardMemSlot(xShift-Globals.CARD_HEIGTH*3, yShift-Globals.CARD_WIDTH, rotation);
			for(int i=0; i < Globals.NUM_SLOTS; i++){
				cardSlots.add(new ARCardProgramSlot(xShift - (float)Math.sin((i/(float)(Globals.NUM_SLOTS-1))*Math.PI)*Globals.CARD_ROW_ARCH, yShift + i*(Globals.CARD_WIDTH*2+Globals.CARD_ROW_SPACING), rotation));
			}
		} else {
			memSlot = new ARCardMemSlot(xShift+Globals.CARD_HEIGTH*3, yShift-Globals.CARD_WIDTH, rotation);
			for(int i=0; i < Globals.NUM_SLOTS; i++){
				cardSlots.add(new ARCardProgramSlot(xShift + (float)Math.sin((i/(float)(Globals.NUM_SLOTS-1))*Math.PI)*Globals.CARD_ROW_ARCH, yShift + i*(Globals.CARD_WIDTH*2+Globals.CARD_ROW_SPACING), rotation));
			}	
		}
		
	}
	
	public void addProgramSlot(ARCardProgramSlot programSlot){
		this.cardSlots.add(programSlot);
	}
	
	public void setMemSlot(ARCardMemSlot memSlot) {
		this.memSlot = this.memSlot;
	}
	
	public void glDraw(long time){
		
		for(ARCardProgramSlot i : cardSlots){
			i.glDraw(time);
		}
		memSlot.glDraw(time);
	}

	public ARCardProgramSlot getCardSlot(int index) {
		return cardSlots.get(index);
	}

	public void reset() {
		for(int i=0; i < Globals.NUM_SLOTS; i++){
			cardSlots.get(i).reset();
		}
		memSlot.reset();
	}

	public ArrayList<ARCardProgramSlot> getCardSlots() {
		return cardSlots;
	}

}
