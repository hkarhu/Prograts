/* [LGPL] Copyright 2010, 2011 Gima, Irah

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

import java.awt.Canvas;
import java.nio.ByteBuffer;

import javax.swing.JFrame;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public abstract class GLCore {
	
	private boolean requestClose;
	private boolean lastFocusedState;
	
	private String windowTitle;
	private ByteBuffer[] windowIcon;
	
	public ListenerManager<GLKeyboardListener> keyboardListeners;
	
	private final static DrainableExecutorService drainableExecutorService = new DrainableExecutorService();
	
	public GLCore() {
		requestClose = false;
		lastFocusedState = true;

		windowTitle = "untitled window wodniw deltitnu";
		windowIcon = null;
		keyboardListeners = new ListenerManager<>();
	}
	
	public static DrainableExecutorService getExecutorService() {
		return drainableExecutorService;
	}

	public void startGL(String title) {
		this.windowTitle = title;
		startGL();		
	}

	public void startGL(String title, ByteBuffer[] icon) {
		this.windowIcon = icon;
		startGL(windowTitle);
	}
	
	public void startGL() {
		
		requestClose = false;
		lastFocusedState = true;
		
		Keyboard.enableRepeatEvents(false);
		Display.setTitle(windowTitle);
		Display.setIcon(windowIcon);
		
		contemplateDisplayMode();
		
		if (glInit()) {
			
			//Do stereo mode-init if any
//			switch(stereoMode){
//			
//				case interlaced: interlacedStereoInit(); break;
//				case oculus: oculusStereoInit(); break;
//					
//			}
			
			while (internalLoop()) {
				// fps cap?
				// fap cap?
				// fap fap?
			}
			glTerminate();
		}

		Display.destroy();
	}
	
	public void startGL(Canvas destination){
		
		requestClose = false;
		lastFocusedState = true;
		
		Keyboard.enableRepeatEvents(false);
		
		try {
			Display.setParent(destination);
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
			return;
		}
		
		if (!glInit()) throw new Error("GL Initialization failed.");

//	TÄä ei oo mun koodia t: tommi
/*		new Timer(10, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//if (!glLoop()
			}
		});
*/
		
	}
	
	public void requestClose() {
		requestClose = true;
	}
	
	/**
	 * Sleep at most the specified amount milliseconds. 
	 */
	public void sleep(long millis) {
		try {
			Thread.sleep(millis);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void swapBuffers() {
		try {
			Display.swapBuffers();
		}
		catch (LWJGLException e) {
			e.printStackTrace();
		}
	}
	
	private void contemplateDisplayMode() {
		
		DisplayModePack displayModePack; 
		
		try {
			// request subclass to pick a display mode
			displayModePack = glPickDisplayMode();
			if (displayModePack == null) {
				// subclass specifically requested to let the user choose
				displayModeChooserDialog();
				return;
			}
		}
		catch (Exception e) {
			throw new Error("User class failed to pick a display mode.", e);
		}
		
		try {
			// try to use the returned display mode
			useDisplayModePack(displayModePack);
			return;
		}
		catch (LWJGLException e) {
			S.eprintf("Requested display mode could not be set. Offering the user a choice to select one of the available display modes.", e);
		}
		
		// let the user choose a display mode
		displayModeChooserDialog();
		
	}

	private void displayModeChooserDialog() {
		DisplayModePack dmpUserChosen = DisplayModeChooserDialog.dialogChooseDisplayMode();
		try {
			useDisplayModePack(dmpUserChosen);
		}
		catch (LWJGLException e) {
			throw new Error("No display mode could not be set. Exiting.");
		}
	}

	private void useDisplayModePack(DisplayModePack displayModePack) throws LWJGLException {
		
		if (displayModePack == null) throw new NullPointerException("DisplayModePack must not be null.");
		
		Display.setDisplayMode(displayModePack.getDisplayMode());
		Display.setFullscreen(displayModePack.isFullscreen());
		
		GLValues.screenWidth = displayModePack.getDisplayMode().getWidth();
		GLValues.screenHeight = displayModePack.getDisplayMode().getHeight();
		GLValues.calculateRatios();
		
		if (displayModePack.getPixelFormat() == null) {
			Display.create();
		} else {
			Display.create(displayModePack.getPixelFormat());
		}
		
	}
	
	private boolean internalLoop() {
		
		handleKeyboardEvents();
		//handleFocusChange();
		
//		switch(stereoMode){
//		
//			case interlaced: interlacedStereoDraw(true); glLoop(); interlacedStereoDraw(false); glLoop(); break;
//			case oculus: oculusStereoDraw(true); glLoop(); oculusStereoDraw(false); glLoop(); break; 
//		
//			default: glLoop(); swapBuffers(); break;
//		}
		
		 glLoop(); //swapBuffers();
		
		Display.processMessages();

		drainableExecutorService.executePending();
		
		return (!(Display.isCloseRequested() || requestClose));
		
	}
	
	public void renderToTexture(int textureID, int width, int height){
		GL11.glViewport(0, 0, width, height);
		glLoop();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);	// Bind and copy texture
		GL11.glCopyTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, 0, 0, width, height, 0);
		GL11.glViewport(0, 0, GLValues.screenWidth, GLValues.screenHeight);
		//GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);		// Clear The Screen And Depth Buffer
	}
	
	private void handleFocusChange() {
		
		boolean isFocused = Display.isActive();
		
		if (lastFocusedState != isFocused) {
			glFocusChanged(isFocused);
			lastFocusedState = isFocused;
		}
	}
	
	private void handleKeyboardEvents() {
		while (Keyboard.next()) {

			if (Keyboard.getEventKeyState() == true) {
				// key changed to down
				for (GLKeyboardListener listener : keyboardListeners) {
					listener.glKeyDown(Keyboard.getEventKey());
				}
				
			}
			else {
				// key changed to up
				for (GLKeyboardListener listener : keyboardListeners) {
					listener.glKeyUp(Keyboard.getEventKey());
				}
			}
			
		} // while
	}
	
	public abstract boolean glInit();
	public abstract DisplayModePack glPickDisplayMode() throws Exception;
	public abstract void glLoop();
	public abstract void glFocusChanged(boolean isFocused);
	public abstract void glTerminate();

}
