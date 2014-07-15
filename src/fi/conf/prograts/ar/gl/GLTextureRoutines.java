/* [LGPL] Copyright 2010, 2011 Gima

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
package fi.conf.prograts.ar.gl;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class GLTextureRoutines {
	
	private static IntBuffer textureIDbuffer;
	
	static {
		allocateNewTextureIDBuffer(4);
	}
	
	private static void allocateNewTextureIDBuffer(int size) {
		if (textureIDbuffer != null) DirectBuffers.freeNativeBufferMemory(textureIDbuffer);
		textureIDbuffer = DirectBuffers.allocateByteBuffer(size << 2).order(ByteOrder.nativeOrder()).asIntBuffer();
	}
	
	/**
	 * Request unused OpenGL texture ID's.
	 * 
	 * @param count - How many texture IDs are being requested.
	 * @return Requested texture ID's as integer array.
	 */
	public static int[] allocateGLTextureIDs(int count) {
		if (count > textureIDbuffer.capacity()) {
			allocateNewTextureIDBuffer(count);
		}

		textureIDbuffer.clear();
		textureIDbuffer.limit(count);
		
		GL11.glGenTextures(textureIDbuffer);
				
		int[] textureIDs = new int[count];
		
		for (int i=0; i<count; i++) {
			if (!textureIDbuffer.hasRemaining()) break;
			textureIDs[i] = textureIDbuffer.get();
			
			// preset this texture's minification and magnification filter
			int prevTextureID = bindTexture(textureIDs[i]);
//			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
//			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
			bindTexture(prevTextureID);
		}
		
		return textureIDs;
	}
	
	public static int bindTexture(int glTextureID) {
		int prevTextureID = getBoundTextureID();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, glTextureID);
		return prevTextureID;
	}

	public static int getBoundTextureID() {
		return GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
	}

	/**
	 * Request one unused OpenGL texture ID.
	 * 
	 * @return Requested texture ID.
	 */
	public static int allocateTextureID() {
		return allocateGLTextureIDs(1)[0];
	}
	
	/**
	 * Deallocate OpenGL texture ID's so they can be reused.
	 * 
	 * @param textureIDs - Integer array of texture ID's to be "returned" to OpenGL.
	 */
	public static void deallocateGLTextureIDs(int[] textureIDs) {
		
		if (textureIDs.length > textureIDbuffer.capacity()) {
			textureIDbuffer = BufferUtils.createIntBuffer(textureIDs.length);
		}
		
		textureIDbuffer.clear();
		
		GL11.glDeleteTextures(textureIDbuffer);
	}
	
	public static void deallocateGLTextureID(Integer glTextureID) {
		deallocateGLTextureIDs(new int[] { glTextureID });
	}
	
	public static int createTexture(byte[] imageData, int width, int height) {
		int textureID = GLTextureRoutines.allocateTextureID();
		initializeTexture(textureID, imageData, width, height);
		return textureID;
	}
	
	public static int createTexture(BufferedImage bufferedImage){
		return createTexture(ImageConversions.convertBIToBGRABytes(bufferedImage), bufferedImage.getWidth(), bufferedImage.getHeight());
	}

	public static void initializeTexture(int textureID, byte[] imageData, int width, int height) {
		
		ByteBuffer byteBuffer = BufferUtils.createByteBuffer(width * height * 4);

		byteBuffer.put(imageData);
		byteBuffer.flip(); // rewind buffer, set limit, remove mark
		
		int prevTextureID = bindTexture(textureID);
		
    	GL11.glTexImage2D(
				GL11.GL_TEXTURE_2D,
				0,
				4,
				width, height,
				0,
				GL12.GL_BGRA,
				GL11.GL_UNSIGNED_BYTE,
				byteBuffer
		);
    	
    	bindTexture(prevTextureID);
	}
}
