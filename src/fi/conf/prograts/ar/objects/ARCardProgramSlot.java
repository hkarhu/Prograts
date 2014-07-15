package fi.conf.prograts.ar.objects;

import org.lwjgl.opengl.GL11;

import fi.conf.prograts.ar.Globals;
import fi.conf.prograts.ar.gl.GLBitmapFontBlitter;
import fi.conf.prograts.ar.gl.GLGraphicRoutines;
import fi.conf.prograts.ar.gl.GLTextureManager;
import fi.conf.prograts.ar.gl.GLValues;
import fi.conf.prograts.ar.objects.Command.Type;

public class ARCardProgramSlot extends ARCardSlot {
	
	public ARCardProgramSlot(float x, float y, float a) {
		super(x, y, a);
	}
	
	private float activateTime = -1;
	private float breakTime = -1;
	private boolean highlighted = false;

	public void glDraw(long time){
		
		float at = (activateTime - time)/ACTIVATE_TIME;
		
		GL11.glPushMatrix();
			GL11.glColor4f(1,1,1,1);
			GL11.glScalef(1,1,1);
			GL11.glTranslatef(x, y, 3);
			GL11.glRotatef(a, 0, 0, 1);
			//GLTextureManager.getInstance().bindTexture("card");
			//GLGraphicRoutines.draw2DRect(-width*0.5f, width*0.5f, -height*0.5f, height*0.5f, 0);
			
			if(activateTime > time){
				GL11.glColor3f(0, 0, 1);
				GL11.glScalef(1.0f+(float)Math.abs(Math.sin(Math.PI + Math.PI*at)*0.25f), 1.0f+(float)Math.abs(Math.sin(Math.PI + Math.PI*at)*0.25f), 1);
				
				GL11.glColor4f(1, 1, 1, at);
				GLGraphicRoutines.drawLineRect(1.0f, -slot_width-(1-at)*0.2f, -slot_heigth-(1-at)*0.2f, slot_width+(1-at)*0.2f, slot_heigth+(1-at)*0.2f, 0);
			}
		
			if(breakTime > time){
				GL11.glPushMatrix();
				GL11.glColor4f(1, 0, 0, 0.5f+at);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, -1);
				GL11.glLineWidth((10-at*6)*Globals.CARD_SCALE);
				GL11.glTranslatef(0, -0.025f*Globals.CARD_SCALE*GLValues.glWidth, -5f);
				GL11.glRotatef(time*0.03f, 0, 0, 1);
				for(float i=0; i < Math.PI*2; i += Math.PI*0.2f){
					GL11.glBegin( GL11.GL_LINE_STRIP );			
						GL11.glVertex3d(0, 0, 0);
						GL11.glVertex3d((float)Math.sin(i-Math.random()*0.1f)*0.25f*Globals.CARD_SCALE, (float)Math.cos(i-Math.random()*0.1f)*0.25f*Globals.CARD_SCALE, 0);
						GL11.glVertex3d((float)Math.sin(i+Math.random()*0.2f)*0.3f*Globals.CARD_SCALE, (float)Math.cos(i+Math.random()*0.2f)*0.3f*Globals.CARD_SCALE, 0);
					GL11.glEnd();
				}
				GL11.glPopMatrix();
				GLTextureManager.getInstance().bindTexture("card_broken");
			}
			
			GLTextureManager.getInstance().bindTexture("card");
			
			if(command == null){
				GL11.glColor4f(0.3f,0.3f,0.3f, 1);
				GLGraphicRoutines.draw2DRect(-slot_width, -slot_heigth, slot_width, slot_heigth, 0);
				GLTextureManager.unbindTexture();
				GLGraphicRoutines.drawLineRect(1.0f*Globals.CARD_SCALE, -slot_width, -slot_heigth, slot_width, slot_heigth, 0);
			
				GL11.glTranslatef(0, 0.42f*Globals.CARD_SCALE, -0.2f);
				GLBitmapFontBlitter.drawString("NOP", "font_default", 0.2f*Globals.CARD_SCALE, 0.4f*Globals.CARD_SCALE, GLBitmapFontBlitter.Alignment.CENTERED);
			} else {
				GL11.glTranslatef(0, 0, -5);
				GLTextureManager.getInstance().bindTexture("card_"+command.getCommandString().toLowerCase());
				command.GLColorizeLight();
				GLGraphicRoutines.draw2DRect(-slot_width, -slot_heigth, slot_width, slot_heigth, 0);
				GLTextureManager.unbindTexture();
				GLGraphicRoutines.drawLineRect(1.0f, -slot_width, -slot_heigth, slot_width, slot_heigth, 0);
				
//				GL11.glTranslatef(0, 0.42f, -0.2f);
//				GLBitmapFontBlitter.drawString(command.getCommandString(), "font_default", 0.2f, 0.4f, GLBitmapFontBlitter.Alignment.CENTERED);
			}
			
			if(highlighted){
				
				GL11.glColor4f(0,1,0, 1);
				GL11.glTranslatef(0, 0, -5);
				GLTextureManager.unbindTexture();
				GLGraphicRoutines.drawLineRect(2.0f, -slot_width, -GLValues.glHeight*0.04f, slot_width, GLValues.glHeight*0.04f, 0);

				GL11.glTranslatef(0, GLValues.glHeight*0.06f*Globals.CARD_SCALE, 0);
				GLBitmapFontBlitter.drawString("< RUN >", "font_default", 0.1f*Globals.CARD_SCALE, 0.2f*Globals.CARD_SCALE, GLBitmapFontBlitter.Alignment.CENTERED);
				
				//Loading circle
				GL11.glPushMatrix();
					GL11.glColor4f(1, 1, 1, 0.5f);
					GL11.glTranslatef(0, -GLValues.glHeight*0.1825f*Globals.CARD_SCALE, 0);
					GL11.glRotatef(time*0.3f, 0, 0, 1);
					GLGraphicRoutines.drawLineCircle(0.4f*Globals.CARD_SCALE, 20*Globals.CARD_SCALE, 2.0f*Globals.CARD_SCALE);
				
						for(float i=0; i < Math.PI*2; i += Math.PI*0.5f){
							GL11.glBegin( GL11.GL_LINE_STRIP );			
								GL11.glVertex3d((float)Math.sin(i)*0.15f*Globals.CARD_SCALE, (float)Math.cos(i)*0.15f*Globals.CARD_SCALE, 0);
								GL11.glVertex3d((float)Math.sin(i)*0.25f*Globals.CARD_SCALE, (float)Math.cos(i)*0.25f*Globals.CARD_SCALE, 0);
								
							GL11.glEnd();
						}
				GL11.glPopMatrix();

			}
			
		GL11.glPopMatrix();
	}
	
	public void reset(){
		super.reset();
		activateTime = 0;
		breakTime = 0;
		highlighted = false;
	}

	public void activate(long time) {
		activateTime = time + ACTIVATE_TIME;
		highlighted = false;
	}
	
	int c1id = 0;
	int c2id = 0;
	boolean c1p = false;
	boolean c2p = false;
	
	public void bindCard(ARCard c) {
		
		this.card = c;
		if(c != null){
			this.command = c.getCommand();
			c2p = c1p;
			if(this.command != null){
				if(Type.PEW.equals(this.command.getType())){
					c1p = true;
				} else {
					c1p = c2p = false;
				}
			}
			
			c2id = c1id;
			c1id = c.getID();
			
		} else {
			this.command = null;
		}
		
		//Eastereggi
		if(c != null && c1id != c2id && c2id  < c1id && (c1id - c2id) % 7 == 0 && c1p && c2p){
			c.setCommand(new Command(Type.QQQ));
			System.out.println("???!" + c1id + " " + c2id);
			c1id = c2id = 0;
			c1p = c2p = false;
		}
		//c.setCommand(new Command(Type.QQQ));
	}

	public void breakContainedCard(long time) {
		breakTime = time + BREAK_TIME;
		if(card != null) card.setBroken(true);
		bindCard(null);
		highlighted = false;
	}

	public void highlight() {
		highlighted = true;
	}
	
}
