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

import java.util.Comparator;

import org.lwjgl.opengl.DisplayMode;

/**
 * Comparator for {@link DisplayMode}'s. 
 */
public class DisplayModeComparator implements Comparator<DisplayMode> {
		
	@Override
	public int compare(DisplayMode o1, DisplayMode o2) {
		int temp = 0;
		
		temp = compareInt(o1.getWidth(), o2.getWidth());
		if (temp != 0) return temp;
		
		temp = compareInt(o1.getHeight(), o2.getHeight());
		if (temp != 0) return temp;
		
		temp = compareInt(o1.getBitsPerPixel(), o2.getBitsPerPixel());
		if (temp != 0) return temp;
		
		temp = compareInt(o1.getFrequency(), o2.getFrequency());
		if (temp != 0) return temp;
		
		return temp;
	}
	
	private int compareInt(int i1, int i2) {
		if (i1 < i2) return -1;
		else if (i1 > i2) return 1;
		else return 0;
	}
	
	public static String getStringRepresentation(DisplayMode displayMode) {
		return S.sprintf(
				"%d x %d x %dbpp @ %dHz (%s)",
				displayMode.getWidth(),
				displayMode.getHeight(),
				displayMode.getBitsPerPixel(),
				displayMode.getFrequency(),
				displayMode.isFullscreenCapable() == true ? "Fullscreen capable" : ""
		);
	}

}
