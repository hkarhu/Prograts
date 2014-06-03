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
along with this program.  If not, see <http://www.gnu.org/licenses/>
*/
package coderats.ar.gl;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class GLGraphicRoutines {

	//Initializing and setting up Projection view
	public static void initPerspective(float angle) {
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		//GL11.glPixelZoom( 1.0f, 1.0f );
		//GL11.glViewport(0, 0, GLValues.screenWidth, GLValues.screenHeight);
		GLU.gluPerspective(angle, GLValues.glRatio, 0.001f, GLValues.glDepth);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
	}
	
	//Initializing and setting up Ortographic view
	public static void initOrtho(){
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		//GL11.glPixelZoom( 1.0f, 1.0f );
		//GL11.glViewport(0, 0, GLValues.screenWidth, GLValues.screenHeight);
		GL11.glOrtho(0, GLValues.glWidth, GLValues.glHeight, 0, -GLValues.glDepth, GLValues.glDepth);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
	}
	
	public static void initCamera(){
		GLU.gluLookAt(GLValues.cameraPositionX, GLValues.cameraPositionY, GLValues.cameraPositionZ,
				  GLValues.cameraTargetX, GLValues.cameraTargetY, GLValues.cameraTargetZ,
				  GLValues.cameraRotationX, GLValues.cameraRotationY, GLValues.cameraRotationZ );
	}

	public static void drawAxes(){
		GL11.glBegin(GL11.GL_LINES);
			GL11.glColor3f(1, 0, 0);
			GL11.glVertex3f(0, 0, 0);
			GL11.glVertex3f(1, 0, 0);
			
			GL11.glColor3f(0, 1, 0);
			GL11.glVertex3f(0, 0, 0);
			GL11.glVertex3f(0, 1, 0);
			
			GL11.glColor3f(0, 0, 1);
			GL11.glVertex3f(0, 0, 0);
			GL11.glVertex3f(0, 0, 1);
		GL11.glEnd();
	}
	
	public static void translateToAlign(float p1x, float p1y, float p1z, float p2x, float p2y, float p2z){
		
		//Translate joint to the middle of the two points
		GL11.glTranslatef((p1x+p2x)*0.5f, (p1y+p2y)*0.5f, (p1z+p2z)*0.5f);
		
		//Fix rotations so the drawn object will match the two points
		float zxAngle = (float) (Math.atan((p1x-p2x)/(p1z-p2z))/Math.PI);
		float yzxAngle = (float) (Math.atan(Math.sqrt(Math.pow((p1x-p2x),2)+Math.pow((p1z-p2z),2))/(p1y-p2y))/Math.PI);
		
		if(p1z > p2z){
			GL11.glRotatef(zxAngle*180, 0, 1, 0);
		} else {
			GL11.glRotatef(zxAngle*180+180, 0, 1, 0);
		}
		
		if(p1y > p2y){
			GL11.glRotatef(180*yzxAngle, 1, 0, 0);
		} else {
			GL11.glRotatef(180*yzxAngle+180, 1, 0, 0);
		}
		
	}
	
	public static void rotateToAlign(float p1x, float p1y, float p1z, float p2x, float p2y, float p2z){
		
		//Translate joint to the middle of the two points
		//GL11.glTranslatef((p1x+p2x)*0.5f, (p1y+p2y)*0.5f, (p1z+p2z)*0.5f);
		
		//Fix rotations so the drawn object will match the two points
		float zxAngle = (float) (Math.atan((p1x-p2x)/(p1z-p2z))/Math.PI);
		float yzxAngle = (float) (Math.atan(Math.sqrt(Math.pow((p1x-p2x),2)+Math.pow((p1z-p2z),2))/(p1y-p2y))/Math.PI);
		
		if(p1z > p2z){
			GL11.glRotatef(zxAngle*180, 0, 1, 0);
		} else {
			GL11.glRotatef(zxAngle*180+180, 0, 1, 0);
		}
		
		if(p1y > p2y){
			GL11.glRotatef(180*yzxAngle, 1, 0, 0);
		} else {
			GL11.glRotatef(180*yzxAngle+180, 1, 0, 0);
		}
		
	}
	
	//Routine for drawing background
	public static void draw2DRect(float x0, float y0, float x1, float y1, float z){
		GL11.glBegin( GL11.GL_QUADS );
			GL11.glNormal3f(0,0,1); GL11.glTexCoord2d(0,0); GL11.glVertex3d(x0, y0, z);	
			GL11.glNormal3f(0,0,1); GL11.glTexCoord2d(0,0.9999999); GL11.glVertex3d(x0, y1, z);
			GL11.glNormal3f(0,0,1); GL11.glTexCoord2d(0.9999999,0.9999999); GL11.glVertex3d(x1, y1, z);
			GL11.glNormal3f(0,0,1); GL11.glTexCoord2d(0.9999999,0); GL11.glVertex3d(x1, y0, z);
		GL11.glEnd();
	}
		
	//Routine for drawing background
	public static void drawBackgroundPlane(){
		initOrtho();
		draw2DRect(0, 0, GLValues.glWidth, GLValues.glHeight, -GLValues.glDepth);
	}

	public static void drawRepeatedBackgroundPlane(float rx, float ry, float tx, float ty) {
		
		initOrtho();

		GL11.glBegin( GL11.GL_QUADS );
			GL11.glTexCoord2d(tx,ty+ry); GL11.glVertex3d(0,GLValues.glHeight, -GLValues.glDepth);
			GL11.glTexCoord2d(tx+rx,ty+ry); GL11.glVertex3d(GLValues.glWidth,GLValues.glHeight,-GLValues.glDepth);
			GL11.glTexCoord2d(tx+rx,ty); GL11.glVertex3d(GLValues.glWidth,0,-GLValues.glDepth);
			GL11.glTexCoord2d(tx,ty); GL11.glVertex3d(0,0,-GLValues.glDepth);
		GL11.glEnd();
		
	}

	public static void drawCircle(float r, float d) {
		
		GL11.glBegin( GL11.GL_TRIANGLE_FAN );
		
		for(float a=(float) ((2.0f*Math.PI)/d); a < 2*Math.PI; a += (2.0f*Math.PI)/(float)d){
			
			GL11.glNormal3f(0, 0, -1.0f);
			GL11.glTexCoord2d((Math.sin(a)+1.0f)/2.0f, (Math.cos(a)+1.0f)/2.0f);
			GL11.glVertex3d(Math.sin(a)*r, Math.cos(a)*r, 0);
			
		}
		
		GL11.glEnd();
		
	}
	
	public static void drawLineCircle(float radius, float segments, float lineWidth) {
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, -1);
		GL11.glLineWidth(lineWidth);
		
		GL11.glBegin( GL11.GL_LINE_LOOP );
		
		for(float a=0; a < 2*Math.PI; a += (2.0f*Math.PI)/segments){
			GL11.glVertex3d(Math.sin(a)*radius, Math.cos(a)*radius, 0);
		}
		
		GL11.glEnd();
		
	}

	public static void drawLineRect (float w, float x0, float y0, float x1, float y1, float z){
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, -1);
		GL11.glLineWidth(w);
		GL11.glBegin( GL11.GL_LINE_LOOP );
			GL11.glVertex3d(x0, y1, z);
			GL11.glVertex3d(x1, y1, z);
			GL11.glVertex3d(x1, y0, z);
			GL11.glVertex3d(x0, y0, z);
		GL11.glEnd();
		
	}

	public static void drawCube(float r) {
		r /= 2f;
		GL11.glBegin( GL11.GL_QUADS );
		// Front Face
	    GL11.glNormal3f( 0.0f, 0.0f, 1.0f);                  // Normal Pointing Towards Viewer
	    GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex3f(-r, -r,  r);  // Point 1 (Front)
	    GL11.glTexCoord2f(1.0f, 0.0f); GL11.glVertex3f( r, -r,  r);  // Point 2 (Front)
	    GL11.glTexCoord2f(1.0f, 1.0f); GL11.glVertex3f( r,  r,  r);  // Point 3 (Front)
	    GL11.glTexCoord2f(0.0f, 1.0f); GL11.glVertex3f(-r,  r,  r);  // Point 4 (Front)
	    // Back Face
	    GL11.glNormal3f( 0.0f, 0.0f,-1.0f);                  // Normal Pointing Away From Viewer
	    GL11.glTexCoord2f(1.0f, 0.0f); GL11.glVertex3f(-r, -r, -r);  // Point 1 (Back)
	    GL11.glTexCoord2f(1.0f, 1.0f); GL11.glVertex3f(-r,  r, -r);  // Point 2 (Back)
	    GL11.glTexCoord2f(0.0f, 1.0f); GL11.glVertex3f( r,  r, -r);  // Point 3 (Back)
	    GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex3f( r, -r, -r);  // Point 4 (Back)
	    // Top Face
	    GL11.glNormal3f( 0.0f, 1.0f, 0.0f);                  // Normal Pointing Up
	    GL11.glTexCoord2f(0.0f, 1.0f); GL11.glVertex3f(-r,  r, -r);  // Point 1 (Top)
	    GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex3f(-r,  r,  r);  // Point 2 (Top)
	    GL11.glTexCoord2f(1.0f, 0.0f); GL11.glVertex3f( r,  r,  r);  // Point 3 (Top)
	    GL11.glTexCoord2f(1.0f, 1.0f); GL11.glVertex3f( r,  r, -r);  // Point 4 (Top)
	    // Bottom Face
	    GL11.glNormal3f( 0.0f,-1.0f, 0.0f);                  // Normal Pointing Down
	    GL11.glTexCoord2f(1.0f, 1.0f); GL11.glVertex3f(-r, -r, -r);  // Point 1 (Bottom)
	    GL11.glTexCoord2f(0.0f, 1.0f); GL11.glVertex3f( r, -r, -r);  // Point 2 (Bottom)
	    GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex3f( r, -r,  r);  // Point 3 (Bottom)
	    GL11.glTexCoord2f(1.0f, 0.0f); GL11.glVertex3f(-r, -r,  r);  // Point 4 (Bottom)
	    // Right face
	    GL11.glNormal3f( 1.0f, 0.0f, 0.0f);                  // Normal Pointing Right
	    GL11.glTexCoord2f(1.0f, 0.0f); GL11.glVertex3f( r, -r, -r);  // Point 1 (Right)
	    GL11.glTexCoord2f(1.0f, 1.0f); GL11.glVertex3f( r,  r, -r);  // Point 2 (Right)
	    GL11.glTexCoord2f(0.0f, 1.0f); GL11.glVertex3f( r,  r,  r);  // Point 3 (Right)
	    GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex3f( r, -r,  r);  // Point 4 (Right)
	    // Left Face
	    GL11.glNormal3f(-1.0f, 0.0f, 0.0f);                  // Normal Pointing Left
	    GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex3f(-r, -r, -r);  // Point 1 (Left)
	    GL11.glTexCoord2f(1.0f, 0.0f); GL11.glVertex3f(-r, -r,  r);  // Point 2 (Left)
	    GL11.glTexCoord2f(1.0f, 1.0f); GL11.glVertex3f(-r,  r,  r);  // Point 3 (Left)
	    GL11.glTexCoord2f(0.0f, 1.0f); GL11.glVertex3f(-r,  r, -r);  // Point 4 (Left)
                               // Done Drawing Quads
		GL11.glEnd();
		
	}
	
}
