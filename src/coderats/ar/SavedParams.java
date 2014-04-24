package coderats.ar;

import java.io.Serializable;

public class SavedParams implements Serializable {
	
	public int imageWidth = 640;
	public int imageHeight = 480;
	
	public int[][] CPs = {{0,0},{imageWidth,0},{imageWidth,imageHeight},{0, imageHeight}};

}
