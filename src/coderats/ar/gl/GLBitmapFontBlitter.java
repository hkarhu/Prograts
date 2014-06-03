/* [LGPL] Copyright 2010, 2011 Irah

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package coderats.ar.gl;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

public class GLBitmapFontBlitter {

	private Integer textureID;
	private BufferedImage fontBitmap;
	
	public GLBitmapFontBlitter(int textureID) {
		this.textureID = textureID;
	}
	
	public GLBitmapFontBlitter(File fontFile) {
		textureID = GLTextureManager.getInstance().blockingLoad(fontFile.toPath(), "fontTexture");
	}
	
	public GLBitmapFontBlitter(Font font){
		this(font, Color.BLACK, 0, Color.BLACK);
	}	
	
	public GLBitmapFontBlitter(Font font, Color fontColor){
		this(font, fontColor, 0, Color.BLACK);
	}
	
	public GLBitmapFontBlitter(Font font, Color fontColor, int outlineWidth, Color outlineColor){
		
		String fontKey = font.getName();
		
		if(font.isBold()){
			fontKey += "b";
		}
		
		if(font.isItalic()){
			fontKey += "i";
		}
		
		fontKey += fontColor.toString() + outlineWidth + outlineColor.toString();
		
		textureID = GLTextureManager.getInstance().getTextureID(fontKey);
		
		if (textureID == null){
			GLTextureManager.getInstance().blockingLoad(createFontBitmap(font, fontColor, outlineWidth, outlineColor), fontKey);
		}
		
		textureID = GLTextureManager.getInstance().getTextureID(fontKey);
		
	}

	public BufferedImage createFontBitmap(Font font, Color fontColor, int outlineWidth, Color outlineColor){

		//Find a better way to do this! ;-;
		fontBitmap = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = fontBitmap.getGraphics();
		g.setFont(font);
		FontMetrics fm = g.getFontMetrics();
		
		int xSpacing = (fm.getAscent() + fm.getDescent() + fm.getLeading())/4;
		int ySpacing = -fm.getHeight()/4;
		
		int fontSpaceWidth = fm.getAscent()+fm.getDescent()+2*outlineWidth;
		int fontSpaceHeight = fm.getHeight()+2*outlineWidth;
		
		fontBitmap = new BufferedImage(16*fontSpaceWidth, 16*fontSpaceHeight, BufferedImage.TYPE_INT_ARGB);
		g = fontBitmap.getGraphics();
		
		g.setColor(fontColor);
		
		//g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
		
		g.setFont(font);
		g.setColor(fontColor);
		
		for(char c=0; c < 256; c++){
			
			int cx = (int)(c%16)*fontSpaceWidth + xSpacing;
			int cy = (int)(c/16)*fontSpaceHeight + fontSpaceHeight + ySpacing;
			
			g.drawString("" + (char)(c), cx, cy);
		}
		
		//Make the outline
		if(outlineWidth > 0){
			for(int x=outlineWidth; x < fontBitmap.getWidth()-outlineWidth; x++){
				for(int y=outlineWidth; y < fontBitmap.getHeight()-outlineWidth; y++){
					if(fontBitmap.getRGB(x, y) != fontColor.getRGB()){
						for(int w=1; w <= outlineWidth; w++){
							if(fontBitmap.getRGB(x-w, y) == fontColor.getRGB()){
								fontBitmap.setRGB(x, y, outlineColor.getRGB());
							}
							
							if(fontBitmap.getRGB(x+w, y) == fontColor.getRGB()){
								fontBitmap.setRGB(x, y, outlineColor.getRGB());
							}
							
							if(fontBitmap.getRGB(x, y-w) == fontColor.getRGB()){
								fontBitmap.setRGB(x, y, outlineColor.getRGB());
							}
							
							if(fontBitmap.getRGB(x, y+w) == fontColor.getRGB()){
								fontBitmap.setRGB(x, y, outlineColor.getRGB());
							}
							
							if(fontBitmap.getRGB(x-w, y-w) == fontColor.getRGB()){
								fontBitmap.setRGB(x, y, outlineColor.getRGB());
							}
							
							if(fontBitmap.getRGB(x+w, y-w) == fontColor.getRGB()){
								fontBitmap.setRGB(x, y, outlineColor.getRGB());
							}
							
							if(fontBitmap.getRGB(x-w, y+w) == fontColor.getRGB()){
								fontBitmap.setRGB(x, y, outlineColor.getRGB());
							}
							
							if(fontBitmap.getRGB(x+w, y+w) == fontColor.getRGB()){
								fontBitmap.setRGB(x, y, outlineColor.getRGB());
							}
							
						}
					}
				}
			}
		}
		
		S.debug("Font sizes: "+fm.getHeight() + " "+ fm.getAscent() + " " + fm.getDescent() + " " + fm.getLeading());
		
	    //Font font = new Font("Monospaced", Font.BOLD ,38);
		
		return fontBitmap;

	}
	
	public void saveFont(Path file) {
		
		try {
			ImageIO.write(fontBitmap, "png", file.toFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void drawString(String string, String textureIdentifier, float charWidth, float charHeight, Alignment align) {
		
		float fix = 0;
		float overlap = 0.01f;
		
		if(align.equals(Alignment.CENTERED)){
			fix = string.length()*charWidth*0.5f;
		} else if(align.equals(Alignment.RIGHT)){
			fix = string.length()*charWidth-overlap;
		}
		
		GL11.glPushMatrix();
		GL11.glTranslatef(0, -0.5f*charHeight,0);
//		System.out.println(textureID);
		GLTextureManager.getInstance().bindTexture(textureIdentifier);
		
		GL11.glBegin( GL11.GL_QUADS );	
		
		for(int i=0; i < string.length(); i++){
			
			char c = string.charAt(i);
			
			float x1 = (c%16f)/16f;
			float x2 = x1 + 1f/16f;
			float y1 = (c/16)/16f;
			float y2 = y1 + 1f/16f;

			GL11.glTexCoord2d(x1+overlap,y1); GL11.glVertex3d(i*charWidth - fix,0,0);
			GL11.glTexCoord2d(x1+overlap,y2); GL11.glVertex3d(i*charWidth - fix,charHeight,0);
			GL11.glTexCoord2d(x2-overlap,y2); GL11.glVertex3d(i*charWidth+charWidth - fix,charHeight,0);
			GL11.glTexCoord2d(x2-overlap,y1); GL11.glVertex3d(i*charWidth+charWidth - fix,0,0);

		}
		
		GL11.glEnd();
		GL11.glPopMatrix();
		
	}
	
	public static void blitScrollerString(String string, float charWidth, float charHeight, float freq, float amplitude, float phase, String font) {

		float overlap = 0.2f;

		float fix = 0;

		GL11.glPushMatrix();

		GLTextureManager.getInstance().bindTexture(font);
		//GL11.glBindTexture(GL11.GL_TEXTURE_2D, fontTextureID);

		GL11.glEnable(GL11.GL_DEPTH_TEST);

		//GLRoutines.drawSprite(0f, 0f, 2f, 2f, 1f);

		GL11.glBegin( GL11.GL_QUADS );	

		for(int i=0; i < string.length(); i++){

			char c = string.charAt(i);
			float s = (float) Math.sin(phase+freq*2*Math.PI*i/string.length())*amplitude;
			float x1 = (c%16f)/16f;
			float x2 = (c%16f)/16f + 1f/16f;
			float y1 = (c/16)/16f;
			float y2 = (c/16)/16f + 1f/16f;

			//drawSprite(x1, y1, x2, y2, 0);

			//GL11.glColor3f((float)Math.random()*1.3f, (float)Math.random()*1.3f, (float)Math.random()*1.3f);

			GL11.glTexCoord2d(x1,y1); GL11.glVertex3d(i*charWidth - fix - overlap,s,0);
			GL11.glTexCoord2d(x1,y2); GL11.glVertex3d(i*charWidth - fix - overlap,charHeight+s,0);
			GL11.glTexCoord2d(x2,y2); GL11.glVertex3d(i*charWidth+charWidth - fix,charHeight+s,0);
			GL11.glTexCoord2d(x2,y1); GL11.glVertex3d(i*charWidth+charWidth - fix,s,0);

		}

		GL11.glEnd();
		GL11.glPopMatrix();
	}
	
	public static void blitSinString(String string, float charWidth, float charHeight, float freq, float amplitude, float phase, String font) {

		float overlap = 0.2f;

		float fix = 0;

		GL11.glPushMatrix();

		GLTextureManager.getInstance().bindTexture(font);
		//GL11.glBindTexture(GL11.GL_TEXTURE_2D, fontTextureID);

		GL11.glEnable(GL11.GL_DEPTH_TEST);

		//GLRoutines.drawSprite(0f, 0f, 2f, 2f, 1f);
		
		//GL11.glTranslatef(charWidth/2, -charHeight/2, 0);
		

		for(int i=0; i < string.length(); i++){

			char c = string.charAt(string.length()-1-i);
			float vx1 = (float) Math.sin(phase+freq*2*Math.PI*i/string.length())*amplitude;
			float vy1 = (float) Math.cos(phase+freq*2*Math.PI*i/string.length())*amplitude;
			float vx2 = (float) Math.sin(phase+freq*2*Math.PI*i/string.length())*amplitude*charHeight;
			float vy2 = (float) Math.cos(phase+freq*2*Math.PI*i/string.length())*amplitude*charHeight;

			float vx3 = (float) Math.sin(phase+freq*2*Math.PI*(i+1)/string.length())*amplitude;
			float vy3 = (float) Math.cos(phase+freq*2*Math.PI*(i+1)/string.length())*amplitude;
			float vx4 = (float) Math.sin(phase+freq*2*Math.PI*(i+1)/string.length())*amplitude*charHeight;
			float vy4 = (float) Math.cos(phase+freq*2*Math.PI*(i+1)/string.length())*amplitude*charHeight;
			
			float x1 = (c%16f)/16f;
			float x2 = (c%16f)/16f + 1f/16f;
			float y1 = (c/16)/16f;
			float y2 = (c/16)/16f + 1f/16f;
			
			
			//GL11.glColor3f((float)Math.random()*1.3f, (float)Math.random()*1.3f, (float)Math.random()*1.3f);
			//GL11.glBindTexture(GL11.GL_TEXTURE_2D, -1);
			GL11.glBegin( GL11.GL_QUADS );
				GL11.glTexCoord2d(x1,y1); GL11.glVertex3d(vy4,vx4,0);
				GL11.glTexCoord2d(x1,y2); GL11.glVertex3d(vy3,vx3,0);
				GL11.glTexCoord2d(x2,y2); GL11.glVertex3d(vy1,vx1,0);
				GL11.glTexCoord2d(x2,y1); GL11.glVertex3d(vy2,vx2,0);
			GL11.glEnd();
			
		}

		
		GL11.glPopMatrix();
	}
	
	public static enum Alignment {
		LEFT, CENTERED, RIGHT
	}

}

