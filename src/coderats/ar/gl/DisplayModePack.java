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
package coderats.ar.gl;

import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;

/**
 * Container for {@link DisplayMode} and {@link PixelFormat}.
 */
public class DisplayModePack {
	
	private final DisplayMode displayMode;
	private final PixelFormat pixelFormat;
	private final boolean fullscreen;
	
	
	public DisplayModePack(
			DisplayMode displayMode,
			PixelFormat pixelFormat,
			boolean fullscreen
	) {
		this.displayMode = displayMode;
		this.pixelFormat = pixelFormat;
		this.fullscreen = fullscreen;
	}
	
	
	public DisplayMode getDisplayMode() {
		return displayMode;
	}
	
	
	public PixelFormat getPixelFormat() {
		return pixelFormat;
	}
	
	
	public boolean isFullscreen() {
		return fullscreen;
	}
}
