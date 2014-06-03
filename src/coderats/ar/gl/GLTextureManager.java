/* [LGPL] Copyright 2010, 2011 Irah, Gima

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
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

import coderats.ar.gl.ImageScaler.QualityParams;
import coderats.ar.gl.ImageScaler.ScaleParams;

public class GLTextureManager {
	
	private static GLTextureManager INSTANCE;
	
	private final DrainableExecutorService glExecutorService;
	private final ExecutorService workerExecutorService;
	
	private final HashMap<String, Integer> idMap;
	private final TreeSet<String> loading;
	
	private int placeholderTextureID;
	
	public static GLTextureManager getInstance() {
		return INSTANCE;
	}
	
	public GLTextureManager(DrainableExecutorService glExecutorService) {
		this.glExecutorService = glExecutorService;
		workerExecutorService = Executors.newSingleThreadExecutor();
		idMap = new HashMap<>();
		loading = new TreeSet<>();
	}
	
	public void initialize() {
		INSTANCE = this;
		BufferedImage placeHolderImage = generatePlaceholderImage();
		
		placeholderTextureID = GLTextureRoutines.createTexture(placeHolderImage);
	}
	
	public void requestShutdown() {
		workerExecutorService.shutdown();
	}
	
	public int getPlaceholderTextureID() {
		return placeholderTextureID;
	}
	
	private static BufferedImage generatePlaceholderImage() {
		
		int w=64, h=64;
		
		BufferedImage placeholder = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D g = (Graphics2D) placeholder.getGraphics();
		
		g.setColor(Color.black);
		g.fillRect(0, 0, w, h);
//		g.setColor(Color.gray);
//		g.fillOval(0, 0, w, h);
//		g.setColor(Color.white);
//		g.drawLine(0, 0, w, h);
//		g.drawLine(0, w, h, 0);
		
		return placeholder;
		
	}
	
	public void deferredLoad(InputStream inputStream, String identifier) {
		internalLoadTextureFromStream(inputStream, identifier, false, -1, -1, null);
	}
	
	public void deferredLoad(Path filename, String identifier) {
		InputStream inputStream = getTextureInputStream(filename, identifier);
		if (inputStream == null) return;
		internalLoadTextureFromStream(inputStream, identifier, false, -1, -1, null);
	}
	
	public void deferredLoad(BufferedImage bufferedImage, String identifier) {
		internalLoadBufferedImageTexture(bufferedImage, identifier, false);
	}
	
	public int blockingLoad(InputStream inputStream, String identifier) {
		S.debugFunc("Loading: %s\n", identifier);
		return internalLoadTextureFromStream(inputStream, identifier, true, -1, -1, null);
	}
	
	public int blockingLoad(Path filename, String identifier) {
		InputStream inputStream = getTextureInputStream(filename, identifier);
		if (inputStream == null) return placeholderTextureID;
		return internalLoadTextureFromStream(inputStream, identifier, true, -1, -1, null);
	}

	public int blockingLoad(String path, String identifier) {
		return blockingLoad(Paths.get(path), identifier);
	}
	
	public int blockingLoad(BufferedImage bufferedImage, String identifier) {
		return internalLoadBufferedImageTexture(bufferedImage, identifier, true);
	}
	
	public void deferredScaledLoad(InputStream inputStream, String identifier, int newWidth, int newHeight, ScaleParams scaleParams) {
		internalLoadTextureFromStream(inputStream, identifier, false, newWidth, newHeight, scaleParams);
	}
	
	public void deferredScaledLoad(Path filename, String identifier, int newWidth, int newHeight, ScaleParams scaleParams) {
		InputStream inputStream = getTextureInputStream(filename, identifier);
		if (inputStream == null) return;
		internalLoadTextureFromStream(inputStream, identifier, false, newWidth, newHeight, scaleParams);
	}
	
	public int blockingScaledLoad(InputStream inputStream, String identifier, int newWidth, int newHeight, ScaleParams scaleParams) {
		return internalLoadTextureFromStream(inputStream, identifier, true, newWidth, newHeight, scaleParams);
	}
	
	public int blockingScaledLoad(Path filename, String identifier, int newWidth, int newHeight, ScaleParams scaleParams) {
		InputStream inputStream = getTextureInputStream(filename, identifier);
		if (inputStream == null) return placeholderTextureID;
		return internalLoadTextureFromStream(inputStream, identifier, true, newWidth, newHeight, scaleParams);
	}
	
	private InputStream getTextureInputStream(Path filename, String identifier) {
		try {
			return Files.newInputStream(filename, StandardOpenOption.READ);
		}
		catch (IOException e) {
			new Exception(S.sprintf("Cannot load texture '%s'", identifier), e).printStackTrace();
			return null;
		}
	}
	
	private int internalLoadTextureFromStream(InputStream inputStream, String identifier, boolean wait, int newWidth, int newHeight, ScaleParams scaleParams) {
		//S.funcArgs(inputStream, identifier);
		Integer textureID;
		
		synchronized (idMap) {
			textureID = idMap.get(identifier);
			if (textureID != null) {
				//texture already loaded, don't load it again
				//TODO: if it's in the process of being loaded, this can kick another loading into action
				return textureID;
			}
			
		}
		
		synchronized (loading) {
			if(loading.contains(identifier)) {
				return placeholderTextureID;
			} else {
				loading.add(identifier);
			}
		}
		
		if (glExecutorService.getThreadID() == Thread.currentThread().getId()) {
			textureID = GLTextureRoutines.allocateTextureID();
		} else {
			// <sht begns here>
			// texture doesn't exist, load it
			final AtomicInteger abbi2 = new AtomicInteger();
			abbi2.set(-99);
			glExecutorService.submit(new Runnable() {
				@Override
				public void run() {
					int tid = GLTextureRoutines.allocateTextureID();
					abbi2.set(tid);
				}
			});
			
			while (true) {
				textureID = abbi2.get();
				if (textureID != -99) break;
				try { Thread.sleep(10); } catch (InterruptedException e) {}
			}
			// </sht önds here>
		}

		ThreadWorkRecipe recipe = new ThreadWorkRecipe();
		
		recipe.add(loadImageWork, workerExecutorService);
		if (scaleParams != null) {
			recipe.add(imageReducingWork, workerExecutorService, newWidth, newHeight, scaleParams);
		}
		recipe.add(imageConversionWork, workerExecutorService);
		recipe.add(glTextureUploadWork, glExecutorService, textureID);
		recipe.add(idMapUpdateWork, workerExecutorService, identifier, textureID);
		
		recipe.nextWork(recipe, inputStream);
		
		if (wait) {
			try {
				recipe.loopPollResultingCallParams(glExecutorService);
			}
			catch (InterruptedException e) {
				new Exception(S.sprintf("Failed to load texture '%s'.", identifier), e).printStackTrace();
			}
		}
		
		return textureID;
	}

	private int internalLoadBufferedImageTexture(BufferedImage bufferedImage, String identifier, boolean wait) {
//		S.funcArgs(bufferedImage, identifier);
		Integer textureID;
		
		synchronized (idMap) {
			textureID = idMap.get(identifier);
			if (textureID != null) {
				// texture already loaded, don't load it again
				// TODO: if it's in the process of being loaded, this can kick another loading into action
				return textureID;
			}
		}
		
		synchronized (loading) {
			if(loading.contains(identifier)) {
				return placeholderTextureID;
			} else {
				loading.add(identifier);
			}
		}

		if (glExecutorService.getThreadID() == Thread.currentThread().getId()) {
			textureID = GLTextureRoutines.allocateTextureID();
		} else {
			// <sht begns here>
			// texture doesn't exist, load it
			final AtomicInteger abbi = new AtomicInteger();
			abbi.set(-99);
			glExecutorService.submit(new Runnable() {
				@Override
				public void run() {
					int tid = GLTextureRoutines.allocateTextureID();
					abbi.set(tid);
				}
			});
			
			while (true) {
				textureID = abbi.get();
				if (textureID != -99) break;
				try { Thread.sleep(10); } catch (InterruptedException e) {}
			}
			// </sht önds here>
		}
		
		ThreadWorkRecipe recipe = new ThreadWorkRecipe();
		
		recipe.add(imageConversionWork, workerExecutorService);
		recipe.add(glTextureUploadWork, glExecutorService, textureID);
		recipe.add(idMapUpdateWork, workerExecutorService, identifier, textureID);
		
		recipe.nextWork(recipe, bufferedImage);
		
		if (wait) {
			try {
				recipe.loopPollResultingCallParams(glExecutorService);
			}
			catch (InterruptedException e) {
				new Exception(S.sprintf("Failed to load texture '%s'.", identifier), e).printStackTrace();
			}
		}
		
		return textureID;
	}
	
	public void bindTexture(String identifier) {
		//S.funcArgs(identifier);
		Integer textureID;
		synchronized (idMap) {
			textureID = idMap.get(identifier);
		}
		
		if (textureID == null) {
			//S.debug("Cannot bind texture '%s', no matching identifier found. Binding placeholder texture instead.", identifier);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, placeholderTextureID);
		} else {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
		}
	}

	public int getTextureID(String identifier) {
		Integer textureID;
		
		synchronized (idMap) {
			textureID = idMap.get(identifier);
		}
		
		if (textureID == null) {
//			S.eprintfn("Cannot find texture '%s', no matching identifier found. Returning placeholder texture ID.", identifier);
			return placeholderTextureID;
		}
		else {
			return textureID;
		} 
	}
	
	// thread works
	private final ThreadWork loadImageWork = new ThreadWork() {
		@SuppressWarnings("unused")
		public void call(ThreadWorkRecipe recipe, InputStream inputStream) throws IOException {
			//S.funcArgs("loadImageWork: ", recipe, inputStream);
			BufferedImage loadedImage = ImageIO.read(inputStream);
			recipe.nextWork(recipe, loadedImage);
		}
	};
	
	private final ThreadWork imageReducingWork = new ThreadWork() {
		@SuppressWarnings("unused")
		public void call(ThreadWorkRecipe recipe, BufferedImage bufferedImage, int newWidth, int newHeight, ScaleParams scaleParams) throws IOException {
//			S.funcArgs("imageReducingWork: ", recipe, bufferedImage, newWidth, newHeight, scaleParams);
			BufferedImage reducedImage = ImageScaler.scaleImage(bufferedImage, newWidth, newHeight, scaleParams, ImageScaler.QualityParams.FAST);
			recipe.nextWork(recipe, reducedImage);
		}
	};
	
	private final ThreadWork imageConversionWork = new ThreadWork() {
		@SuppressWarnings("unused")
		public void call(ThreadWorkRecipe recipe, BufferedImage bufferedImage) throws IOException {
//			S.funcArgs("imageConversionWork: ", recipe, bufferedImage);
			byte[] convertedImageData = ImageConversions.convertBIToBGRABytes(bufferedImage);
//			S.funcArgs(recipe.getClass().getName(), convertedImageData.getClass().getName(), bufferedImage.getClass().getName());
			recipe.nextWork(recipe, convertedImageData, bufferedImage);
		}
	};
	
	private final ThreadWork glTextureUploadWork = new ThreadWork() {
		@SuppressWarnings("unused")
		public void call(ThreadWorkRecipe recipe, byte[] imageData, BufferedImage bufferedImage, int glTextureID) throws IOException {
//			S.funcArgs("glTextureUploadWork", recipe, imageData, bufferedImage, glTextureID);
			GLTextureRoutines.initializeTexture(glTextureID, imageData, bufferedImage.getWidth(), bufferedImage.getHeight());
			recipe.nextWork(recipe);
		}
	};
	
	private final ThreadWork idMapUpdateWork = new ThreadWork() {
		@SuppressWarnings("unused")
		public void call(ThreadWorkRecipe recipe, String identifier, int glTextureID) throws IOException {
//			S.funcArgs("idMapUpdateWork", recipe, identifier, glTextureID);
			synchronized (idMap) {
				idMap.put(identifier, glTextureID);
			}
			synchronized (loading) {
				loading.remove(identifier);
			}
			recipe.nextWork(glTextureID);
		}
	};

	public static void unbindTexture() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, -1);
	}
	
}
