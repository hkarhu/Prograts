package coderats.ar;

import java.io.Serializable;

public class SavedParams implements Serializable {
	
	public volatile int par1 = 50;
	public volatile int par2 = 50;
	public volatile int par3 = 133;
	public volatile int par4 = 18;
	public volatile int par5 = 255;
	public volatile int par6 = 255;
	
	public volatile int dbg = 0;
	
	public int imageWidth = 640;
	public int imageHeight = 480;
	
	public int[][] CPs = {{0,0},{imageWidth,0},{imageWidth,imageHeight},{0, imageHeight}};
	

}
