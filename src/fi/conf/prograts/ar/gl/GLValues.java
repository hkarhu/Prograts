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
package fi.conf.prograts.ar.gl;



public class GLValues {
	
	private static final int multiplier = 5;
	public static int screenWidth, screenHeight;
	public static boolean fullScreen;
	public static float glWidth, glHeight;
	public static float glDepth = -(multiplier*2);
	public static float glRatio = 0;
	
	public static float cameraPositionX = 0f;
	public static float cameraPositionY = 0f;
	public static float cameraPositionZ = multiplier;
	public static float cameraTargetX = 0f;
	public static float cameraTargetY = 0f;
	public static float cameraTargetZ = 0f;
	public static float cameraRotationX = 0f;
	public static float cameraRotationY = 0f;
	public static float cameraRotationZ = -1f;
	public static int antialiasSamples = 0;
	
	public static void calculateRatios() {
		if (screenWidth < screenHeight) {
			glWidth = multiplier;
			glHeight = ((float) screenHeight / (float) screenWidth) * multiplier;
		} else {
			glHeight = multiplier;
			glWidth = ((float) screenWidth / (float) screenHeight) * multiplier;
		}
		
		glRatio = ((float) screenWidth / (float) screenHeight);
		
		S.debugFunc("resolution(%d * %d), glRatio(%f), glWidth(%f), glHeight(%f), glDepth(%f)",
				screenWidth, screenHeight,
				glRatio,
				glWidth, glHeight, glDepth
				);
	}

	public static void setCameraPosition(float x, float y, float z) {
		cameraPositionX = x;
		cameraPositionY = y;
		cameraPositionZ = z;
	}

	public static void setCameraTarget(float x, float y, float z) {
		cameraTargetX = x;
		cameraTargetY = y;
		cameraTargetZ = z;
	}

	public static void setCameraRotation(float x, float y, float z) {
		cameraRotationX = x;
		cameraRotationY = y;
		cameraRotationZ = z;
	}	

	public static void setScreenSize(int screenWidth, int screenHeight) {
		GLValues.screenHeight = screenHeight;
		GLValues.screenWidth = screenWidth;
		calculateRatios();
	}
	
}
