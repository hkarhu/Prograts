package coderats.ar.gameScenes;
import org.lwjgl.opengl.GL11;

import ae.gl.GLGraphicRoutines;
import ae.gl.GLValues;
import ae.gl.text.GLBitmapFontBlitter;
import ae.gl.texture.GLTextureManager;


public class AllocateHalf {
	
	private boolean rotate = false;
	private long aniTime = 0;
	private boolean activate = false;
	private boolean deactivate = false;
	
	public AllocateHalf(boolean b) {
		this.rotate = b;
	}
	
	public void glDraw(long time){
		
		GL11.glPushMatrix();
			GLTextureManager.unbindTexture();
			
			GL11.glColor3f(0.5f, 0.5f, 0.5f);
		
			if(rotate){
				GL11.glTranslatef(GLValues.glWidth*0.25f, GLValues.glHeight/2, 0);
			} else {
				GL11.glTranslatef(GLValues.glWidth*0.75f, GLValues.glHeight/2, 0);
				GL11.glRotatef(180, 0, 0, 1);
			}
			
			if(activate && aniTime > time){
				float r = 1-((aniTime-time)/1000.0f);
				GL11.glColor4f(1,1,1, (1-r*r));
				GL11.glRotatef((float) (Math.cos(Math.pow(r, 2)*999))*(20*(1-r)), 0, 0, 1);
				GLGraphicRoutines.draw2DRect(-GLValues.glWidth*0.5f, -GLValues.glHeight, GLValues.glWidth*0.25f, GLValues.glHeight, 0);
				GL11.glScalef(r, 1-(float) Math.sin(r*Math.PI)*0.25f, 1);
				GL11.glColor4f(1,1,1, r);
			} else {
				activate = false;
			}
			
			GLTextureManager.getInstance().bindTexture("allocate_bg");

			if(deactivate && aniTime > time){
				GLTextureManager.unbindTexture();
				float r = 1-((aniTime-time)/500.0f);				
				GL11.glRotatef((float)Math.tan(r), 0, 0, 1);
				GL11.glTranslatef(-r*GLValues.glWidth*0.1f, 0, 0	);
				GL11.glColor4f(1,1,1, (r*r));
				GL11.glScalef(1-r, 1-(float) Math.sin(r*Math.PI)*0.25f, 1);
			} else {
				deactivate = false;
			}
			
			GLGraphicRoutines.draw2DRect(-GLValues.glWidth*0.25f, -GLValues.glWidth*0.3f, GLValues.glWidth*0.25f, GLValues.glWidth*0.3f, 0);
			
			GL11.glPushMatrix();
				GL11.glColor3f(0,0,1);
				GLGraphicRoutines.drawLineRect(2.0f, -GLValues.glWidth*0.23f , GLValues.glHeight*0.02f, -GLValues.glWidth*0.015f, GLValues.glHeight*0.465f, 0);
				GL11.glColor3f(0,1,1);
				GLGraphicRoutines.drawLineRect(2.0f, GLValues.glWidth*0.23f , GLValues.glHeight*0.02f, GLValues.glWidth*0.015f, GLValues.glHeight*0.465f, 0);
				GL11.glColor3f(1,0,1);
				GLGraphicRoutines.drawLineRect(2.0f, GLValues.glWidth*0.23f , -GLValues.glHeight*0.02f, GLValues.glWidth*0.015f, -GLValues.glHeight*0.465f, 0);
				GL11.glColor3f(1,0,0);
				GLGraphicRoutines.drawLineRect(2.0f, -GLValues.glWidth*0.23f , -GLValues.glHeight*0.02f, -GLValues.glWidth*0.015f, -GLValues.glHeight*0.465f, 0);
			GL11.glPopMatrix();
			
			GL11.glPushMatrix();
				GL11.glTranslatef(-GLValues.glWidth*0.227f, GLValues.glHeight*0.33f, 0);
				GL11.glRotatef(90, 0, 0, 1);
				GL11.glColor3f(0.2f, 0.2f, 0.2f);
				GLTextureManager.unbindTexture();			
				GLGraphicRoutines.draw2DRect(-GLValues.glWidth*0.06f, -GLValues.glHeight*0.02f, GLValues.glWidth*0.06f, GLValues.glHeight*0.02f, 0);
				GL11.glColor3f(0, 0, 1);
				GLBitmapFontBlitter.drawString("< STP >", "font_default", 0.02f*GLValues.glWidth, 0.06f*GLValues.glHeight, GLBitmapFontBlitter.Alignment.CENTERED);
			GL11.glPopMatrix();

			GL11.glPushMatrix();
				GL11.glTranslatef(-GLValues.glWidth*0.227f, -GLValues.glHeight*0.33f, 0);
				GL11.glRotatef(90, 0, 0, 1);
				GL11.glColor3f(0.2f, 0.2f, 0.2f);
				GLTextureManager.unbindTexture();			
				GLGraphicRoutines.draw2DRect(-GLValues.glWidth*0.06f, -GLValues.glHeight*0.02f, GLValues.glWidth*0.06f, GLValues.glHeight*0.02f, 0);
				GL11.glColor3f(1, 0, 0);
				GLBitmapFontBlitter.drawString("< PEW >", "font_default", 0.02f*GLValues.glWidth, 0.06f*GLValues.glHeight, GLBitmapFontBlitter.Alignment.CENTERED);
			GL11.glPopMatrix();
			
			GL11.glPushMatrix();
				GL11.glTranslatef(GLValues.glWidth*0.018f, -GLValues.glHeight*0.33f, 0);
				GL11.glRotatef(90, 0, 0, 1);
				GL11.glColor3f(0.2f, 0.2f, 0.2f);
				GLTextureManager.unbindTexture();			
				GLGraphicRoutines.draw2DRect(-GLValues.glWidth*0.06f, -GLValues.glHeight*0.02f, GLValues.glWidth*0.06f, GLValues.glHeight*0.02f, 0);
				GL11.glColor3f(1, 0, 1);
				GLBitmapFontBlitter.drawString("< ROL >", "font_default", 0.02f*GLValues.glWidth, 0.06f*GLValues.glHeight, GLBitmapFontBlitter.Alignment.CENTERED);
			GL11.glPopMatrix();
			
			GL11.glPushMatrix();
				GL11.glTranslatef(GLValues.glWidth*0.018f, GLValues.glHeight*0.33f, 0);
				GL11.glRotatef(90, 0, 0, 1);
				GL11.glColor3f(0.2f, 0.2f, 0.2f);
				GLTextureManager.unbindTexture();			
				GLGraphicRoutines.draw2DRect(-GLValues.glWidth*0.06f, -GLValues.glHeight*0.02f, GLValues.glWidth*0.06f, GLValues.glHeight*0.02f, 0);
				GL11.glColor3f(0, 1, 1);
				GLBitmapFontBlitter.drawString("< ROR >", "font_default", 0.02f*GLValues.glWidth, 0.06f*GLValues.glHeight, GLBitmapFontBlitter.Alignment.CENTERED);
			GL11.glPopMatrix();
			
		GL11.glPopMatrix();
	}
	
	public void activate(long time){
		activate = true;
		aniTime = time+1000;
	}

	public void deactivate(long time) {
		deactivate = true;
		aniTime = time+500;
	}
	
	public boolean isAnimating(){
		return activate || deactivate;
	}
	
}
