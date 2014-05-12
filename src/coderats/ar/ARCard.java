package coderats.ar;
import org.lwjgl.opengl.GL11;

import ae.gl.GLGraphicRoutines;
import ae.gl.text.GLBitmapFontBlitter;
import ae.gl.texture.GLTextureManager;

public class ARCard {

	private float x;
	private float y;
	int id;
	float angle;
	float quality;
	long qtime;

	Command cmd;

	boolean broken = false;

	float c1x;
	float c1y;
	float c2x;
	float c2y;
	float c3x;
	float c3y;
	float c4x;
	float c4y;

	public ARCard(float x, float y, float angle, int id, float quality) {
		
		cmd = null;

		this.x = x;
		this.y = y;
		this.angle = angle;
		this.id = id;
		this.quality = quality;

		c1x = c2x = -Globals.CARD_WIDTH*0.5f;
		c3x = c4x = Globals.CARD_WIDTH*0.5f;
		c2y = c3y = -0.3448f*Globals.CARD_HEIGTH;
		c1y = c4y = 0.6555f*Globals.CARD_HEIGTH;

	}

	public void glDraw(long time) {

		GL11.glPushMatrix();
		
		GL11.glTranslatef(getX(), getY(), 0);
		GL11.glRotatef(-(float) Math.toDegrees(angle)+70, 0, 0, 1);
		
		if(cmd == null){

			GL11.glColor3f(0,1,0);
			
			GLTextureManager.unbindTexture();
			for(float dx=c1x; dx < c3x; dx += (c3x-c1x)*0.2f){
				for(float dy=c2y; dy < c4y; dy += (c4y-c2y)*0.1f){
					GL11.glColor4f(0,(float)Math.random(),0,quality*(1+(float)Math.sin(Math.tan(time*0.003f*id)*Math.cos(dy))));
					GLGraphicRoutines.draw2DRect(dx-(c2y-c4y)*0.15f, dy-(c2y-c4y)*0.1f, dx, dy, 0);
				}
			}

			GL11.glColor3f(1, 1, 1);
			GLTextureManager.unbindTexture();
			GLGraphicRoutines.drawCircle(0.08f, 6);
			
			GLGraphicRoutines.drawLineCircle((float) (0.2f+Math.sin(time*0.002f+id)*0.05f), 10, 1f);
			
			GL11.glColor3f(0, 1, 0);
			
			GLGraphicRoutines.drawLineRect(1.0f,c1x,c2y,c3x,c4y,-1);

			GL11.glTranslatef(0, 0.48f, 0);
			GLBitmapFontBlitter.drawString("???", "font_default", 0.08f*Globals.CARD_SCALE, 0.1f*Globals.CARD_SCALE, GLBitmapFontBlitter.Alignment.CENTERED);
			GL11.glTranslatef(0, 0.18f, 0);
			GLBitmapFontBlitter.drawString(quality + "   #" + (int)id, "font_default", 0.03f*Globals.CARD_SCALE, 0.06f*Globals.CARD_SCALE, GLBitmapFontBlitter.Alignment.CENTERED);			
			//GLGraphicRoutines.drawLineRect(1.0f, -GLValues.glWidth*0.49f, -GLValues.glHeight*0.49f, GLValues.glWidth*0.49f, GLValues.glHeight*0.49f, 0);

		} else {
			
			if(broken){
				GL11.glColor4f(1, 1, 1, 0.6f);	
				GLTextureManager.getInstance().bindTexture("card_broken");
			} else {
				cmd.GLColorizeDark();
				GLTextureManager.getInstance().bindTexture("card_"+cmd.getCommandString().toLowerCase());	
			}

			GL11.glBegin( GL11.GL_QUADS );
				GL11.glNormal3f(0,0,1); GL11.glTexCoord2d(0,0); GL11.glVertex3d(c1x, c2y, 0);	
				GL11.glNormal3f(0,0,1); GL11.glTexCoord2d(0,0.9999999); GL11.glVertex3d(c2x, c1y, 0);
				GL11.glNormal3f(0,0,1); GL11.glTexCoord2d(0.9999999,0.9999999); GL11.glVertex3d(c3x, c4y, 0);
				GL11.glNormal3f(0,0,1); GL11.glTexCoord2d(0.9999999,0); GL11.glVertex3d(c4x, c3y, 0);
			GL11.glEnd();

			GLTextureManager.unbindTexture();
			GL11.glTranslatef(0, 0, -0.2f);
			if(broken){
				GL11.glColor4f(1, 1, 1, 0.8f);	
			} else {
				cmd.GLColorizeLight();
			}
			
//			GL11.glTranslatef(0, 0.58f, 0);
//			GLBitmapFontBlitter.drawString(cmd.getCommandString(), "font_default", 0.1f*SCALE, 0.2f*SCALE, GLBitmapFontBlitter.Alignment.CENTERED);

		}
		GL11.glPopMatrix();
	}
	
	public void glDrawDebug(long time) {

		GL11.glPushMatrix();
		
		GL11.glTranslatef(getX(), getY(), 0);
		GL11.glRotatef(-(float) Math.toDegrees(angle)+70, 0, 0, 1);

		GLTextureManager.getInstance().bindTexture("card");
	
			GL11.glColor3f(0.3f, 0.3f, 0.3f);

			GL11.glBegin( GL11.GL_QUADS );
				GL11.glNormal3f(0,0,1); GL11.glTexCoord2d(0,0); GL11.glVertex3d(c1x, c2y, 0);	
				GL11.glNormal3f(0,0,1); GL11.glTexCoord2d(0,0.9999999); GL11.glVertex3d(c2x, c1y, 0);
				GL11.glNormal3f(0,0,1); GL11.glTexCoord2d(0.9999999,0.9999999); GL11.glVertex3d(c3x, c4y, 0);
				GL11.glNormal3f(0,0,1); GL11.glTexCoord2d(0.9999999,0); GL11.glVertex3d(c4x, c3y, 0);
			GL11.glEnd();

			GL11.glColor3f(0.8f, 0.8f, 0.8f);
			GLTextureManager.unbindTexture();
			GLGraphicRoutines.drawLineRect(1.0f, c1x, c2y, c3x, c4y, 0);
			GLGraphicRoutines.drawCircle(0.08f*Globals.CARD_SCALE, 10);
			GL11.glColor3f(0, 1, 0);

			GL11.glTranslatef(0, 0.5f*Globals.CARD_SCALE, 0);
			GLBitmapFontBlitter.drawString((int)id + "", "font_default", 0.15f*Globals.CARD_SCALE, 0.2f*Globals.CARD_SCALE, GLBitmapFontBlitter.Alignment.CENTERED);
			GL11.glTranslatef(0, 0.18f*Globals.CARD_SCALE, 0);
			GLBitmapFontBlitter.drawString(quality + " " + Math.abs((int)(Math.toDegrees(angle)+70)), "font_default", 0.06f*Globals.CARD_SCALE, 0.1f*Globals.CARD_SCALE, GLBitmapFontBlitter.Alignment.CENTERED);
		
		GL11.glPopMatrix();
	}


	public void updateValues(float x, float y, float a, float quality) {
		this.quality = quality;

//		if(Math.abs(this.getX()-x) > 0.01f) this.x = ((this.getX() + x)/2);
//		if(Math.abs(this.getY()-y) > 0.01f) this.y = ((this.getY() + y)/2);
		
		this.x = x;
		this.y = y;

		this.angle = a;
		this.qtime = System.currentTimeMillis();
	}

	public float getQuality() {
		return quality - (System.currentTimeMillis() - qtime)*0.001f;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public void setCommand(Command cmd){
		this.cmd = cmd;
	}

	public Command getCommand() {
		if(isBroken()) return null;
		return cmd;
	}

	public int getID() {
		return id;
	}

	public void setBroken(boolean broken) {
		this.broken = broken;
	}
	
	public boolean isBroken(){
		return broken;
	}

}

