package gameScenes;

import java.util.concurrent.ConcurrentLinkedDeque;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import ae.gl.GLGraphicRoutines;
import ae.gl.GLValues;
import ae.gl.text.GLBitmapFontBlitter;
import ae.gl.texture.GLTextureManager;
import ae.gl.texture.GLTextureRoutines;

public class IntroScene extends GameScene {

	private ParticleCloud particleCloud;
	final static int memtexsize = 256;
	public static int memTexID;
	
	private ConcurrentLinkedDeque<float[]> trackedCards;

	public IntroScene() {

		particleCloud = new ParticleCloud();
		particleCloud.setNumParticles(1000);

		trackedCards = new ConcurrentLinkedDeque<>();
		
		for(int i=0; i<1000; i++) particleCloud.advance();

	}

	@Override
	public void initialize(){
		System.out.println("Creating memorytexmaps...");
		//Reserve space for "buffer" textures

		memTexID = GLTextureRoutines.allocateGLTextureIDs(1)[0];

		GL11.glBindTexture( GL11.GL_TEXTURE_2D, memTexID );

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);

		GL11.glTexImage2D(
				GL11.GL_TEXTURE_2D,
				0,
				4,
				//(int)(GLValues.getScreenWidth()/2.0f), (int)(GLValues.getScreenHeight()/2.0f),
				memtexsize, memtexsize,
				0,
				GL12.GL_BGRA,
				GL11.GL_UNSIGNED_BYTE,
				BufferUtils.createByteBuffer(memtexsize * memtexsize * 4)
				);
	}

	@Override
	public void glDraw(long time) {

		GL11.glColor4f(0.5f, 0.5f, 0.5f, 0.3f);
		GLGraphicRoutines.drawRepeatedBackgroundPlane(-GLValues.glWidth*0.49f, -GLValues.glHeight*0.49f, GLValues.glWidth*0.49f, GLValues.glHeight*0.49f);
		GL11.glPushMatrix();

			GL11.glColor3f(0, 0.5f, 0);
	
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, -1);
			GL11.glTranslatef(GLValues.glWidth*0.5f, GLValues.glHeight*0.5f, 0);
	
			GL11.glPushMatrix();
				particleCloud.advance();
				for(Particle p : particleCloud.getParticles()){
					GL11.glColor4f(0, 1, 0, p.getLife()/1000.0f);
					GLGraphicRoutines.draw2DRect(p.getX()-0.02f, p.getY()-0.02f, p.getX()+0.01f, p.getY()+0.01f, 0);
				}
			GL11.glPopMatrix();
	
			GL11.glPushMatrix();
				GL11.glColor3f(0, 1, 0);
				GLGraphicRoutines.drawLineCircle(1.76f, 50, 3f);
				GLGraphicRoutines.drawLineRect(1.0f, -GLValues.glWidth*0.49f, -GLValues.glHeight*0.49f, GLValues.glWidth*0.49f, GLValues.glHeight*0.49f, 0);
				GLBitmapFontBlitter.blitSinString("CodeRats    CodeRats    CodeRats    CodeRats    CodeRats    ", 0.1f, 0.9f, 1, 2.0f, time*0.0003f, "font_code");
			GL11.glPopMatrix();

		GL11.glPopMatrix();
		
		for(float[] p : trackedCards){
			GL11.glPushMatrix();
				GL11.glTranslatef(GLValues.glWidth/2, GLValues.glHeight/2, 0);
				GLTextureManager.unbindTexture();
				GL11.glTranslatef(p[0], p[1]-0.2f, 0);
				GL11.glRotatef(time*0.02f, 0, 0, 1f);
				GLGraphicRoutines.drawLineCircle(0.4f, 30, 0.2f);
			GL11.glPopMatrix();
		}
		
	}

	@Override
	public boolean isRunning() {
		return true;
	}
	
}