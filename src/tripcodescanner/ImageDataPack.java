package tripcodescanner;


import java.awt.Image;
import java.awt.image.BufferedImage;

public class ImageDataPack {
	
	private byte[] thresholdImage;
	private byte[] edgeDetectedImage;
	private byte[] edgeDetectedNMSImage;
	private byte[] edgeTrackedImage;
	private int imageWidth;
	private int imageHeight;
	private BufferedImage edgeTrackedImageBI;
	
	
	public void setThresholdImage(byte[] thresholdImage) {
		this.thresholdImage = thresholdImage;
	}


	public void setEdgeDetectedImage(byte[] edgeDetectedImage) {
		this.edgeDetectedImage = edgeDetectedImage;
	}


	public void setEdgeDetectedNMSImage(byte[] edgeDetectedNMSImage) {
		this.edgeDetectedNMSImage = edgeDetectedNMSImage;
	}


	public void setEdgeTrackedImage(byte[] edgeTrackedImage) {
		this.edgeTrackedImage = edgeTrackedImage;
		
		edgeTrackedImageBI = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_3BYTE_BGR);
		
		for (int y=0; y<imageHeight; y++)
			for (int x=0; x<imageWidth; x++)
				edgeTrackedImageBI.setRGB(x, y, edgeTrackedImage[y*imageWidth+x]!=0?0x00FF0000:0);
				
	}
	
	
	public void setWidth(int imageWidth) {
		this.imageWidth = imageWidth;
	}
	
	
	public void setHeight(int imageHeight) {
		this.imageHeight = imageHeight;
	}


	public byte[] getThresholdImage() {
		return thresholdImage;
	}
	
	
	public byte[] getEdgeDetectedImage() {
		return edgeDetectedImage;
	}
	
	
	public byte[] getEdgeDetectedNMSImage() {
		return edgeDetectedNMSImage;
	}
	
	
	public byte[] getEdgeTrackedImage() {
		return edgeTrackedImage;
	}
	
	
	public int getWidth() {
		return imageWidth;
	}
	
	
	public int getHeight() {
		return imageHeight;
	}


	public Image getEdgeTrackedImageBI() {
		return edgeTrackedImageBI;
	}

} // class
