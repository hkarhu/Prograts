import org.lwjgl.opengl.GL11;

import ae.gl.text.GLBitmapFontBlitter;
import ae.gl.texture.GLTextureManager;

public class ARCard extends GLDrawableItem {

	private static final float SCALE = 1.5f;
	
	float x;
	float y;
	float id;
	float angle;
	Command cmd;
	
	float c1x;
	float c1y;
	float c2x;
	float c2y;
	float c3x;
	float c3y;
	float c4x;
	float c4y;
	
	public ARCard(float x, float y, float angle, float id, Command cmd) {
		this.x = x;
		this.y = y;
		this.angle = angle;
		this.id = id;
		this.cmd = cmd;
		
		c1x = c2x = -0.1575f*SCALE;
		c3x = c4x = 0.2825f*SCALE;
		c2y = c3y = -0.31f*SCALE;
		c1y = c4y = 0.31f*SCALE;
		
	}
	
	@Override
	public void glDraw() {
		GL11.glPushMatrix();
			
			cmd.commandGLColorize();
			GL11.glTranslatef(x, y, 0);
			GL11.glRotatef((float) Math.toDegrees(angle), 0, 0, 1);
			
			GLTextureManager.getInstance().bindTexture("card");
			
			GL11.glBegin( GL11.GL_QUADS );
				GL11.glNormal3f(0,0,1); GL11.glTexCoord2d(0,0); GL11.glVertex3d(c1x, c2y, 0);	
				GL11.glNormal3f(0,0,1); GL11.glTexCoord2d(0,0.9999999); GL11.glVertex3d(c2x, c1y, 0);
				GL11.glNormal3f(0,0,1); GL11.glTexCoord2d(0.9999999,0.9999999); GL11.glVertex3d(c3x, c4y, 0);
				GL11.glNormal3f(0,0,1); GL11.glTexCoord2d(0.9999999,0); GL11.glVertex3d(c4x, c3y, 0);
			GL11.glEnd();
			
			GL11.glTranslatef(0.065f*SCALE, 0.215f*SCALE, 0);
			
			GLBitmapFontBlitter.drawString(cmd.getCommandString(), "font_default", 0.1f*SCALE, 0.2f*SCALE, GLBitmapFontBlitter.Alignment.CENTERED);
			
			//GLGraphicRoutines.drawLineRect(1.0f, -GLValues.glWidth*0.49f, -GLValues.glHeight*0.49f, GLValues.glWidth*0.49f, GLValues.glHeight*0.49f, 0);
		GL11.glPopMatrix();
	}

}
