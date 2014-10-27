package fi.conf.prograts.ar.videoproc;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import org.opencv.core.Mat;

import fi.conf.prograts.ar.gl.S;


public class OpenCVUtils {

	public static int CAP_PROP_POS_MSEC		  =0;
	public static int CAP_PROP_POS_FRAMES     =1;
	public static int CAP_PROP_POS_AVI_RATIO  =2;
	public static int CAP_PROP_FRAME_WIDTH    =3;
	public static int CAP_PROP_FRAME_HEIGHT   =4;
	public static int CAP_PROP_FPS            =5;
	public static int CAP_PROP_FOURCC         =6;
	public static int CAP_PROP_FRAME_COUNT    =7;
	public static int CAP_PROP_FORMAT         =8;
	public static int CAP_PROP_MODE           =9;
	public static int CAP_PROP_BRIGHTNESS    =10;
	public static int CAP_PROP_CONTRAST      =11;
	public static int CAP_PROP_SATURATION    =12;
	public static int CAP_PROP_HUE           =13;
	public static int CAP_PROP_GAIN          =14;
	public static int CAP_PROP_EXPOSURE      =15;
	public static int CAP_PROP_CONVERT_RGB   =16;
	public static int CAP_PROP_WHITE_BALANCE_BLUE_U =17;
	public static int CAP_PROP_RECTIFICATION =18;
	public static int CAP_PROP_MONOCROME     =19;
	public static int CAP_PROP_SHARPNESS     =20;
	public static int CAP_PROP_AUTO_EXPOSURE =21; // DC1394: exposure control done by camera; user can adjust refernce level using this feature
	public static int CAP_PROP_GAMMA         =22;
	public static int CAP_PROP_TEMPERATURE   =23;
	public static int CAP_PROP_TRIGGER       =24;
	public static int CAP_PROP_TRIGGER_DELAY =25;
	public static int CAP_PROP_WHITE_BALANCE_RED_V =26;
	public static int CAP_PROP_ZOOM          =27;
	public static int CAP_PROP_FOCUS         =28;
	public static int CAP_PROP_GUID          =29;
	public static int CAP_PROP_ISO_SPEED     =30;
	public static int CAP_PROP_BACKLIGHT     =32;
	public static int CAP_PROP_PAN           =33;
	public static int CAP_PROP_TILT          =34;
	public static int CAP_PROP_ROLL          =35;
	public static int CAP_PROP_IRIS          =36;
	public static int CAP_PROP_SETTINGS      =37;

	/**  
	 * Converts/writes a Mat into a BufferedImage.  
	 *  
	 * @param matrix Mat of type CV_8UC3 or CV_8UC1
	 * @return BufferedImage of type TYPE_3BYTE_BGR or TYPE_BYTE_GRAY  
	 */  
	public static BufferedImage matToBufferedImage(Mat matrix) {
		int cols = matrix.cols();  
		int rows = matrix.rows();

		if(cols <= 0 || rows <= 0){
			System.out.println("Tried to convert weird frame to debug...");
			return null;
		}
		BufferedImage image = null;

		byte[] data;  
		int type;  

		switch (matrix.channels()) {  
		case 1:
			int elemSize = (int)matrix.elemSize();  
			data = new byte[cols * rows * elemSize];
			matrix.get(0, 0, data);
			type = BufferedImage.TYPE_BYTE_GRAY;
			image = new BufferedImage(cols, rows, type);
			image.getRaster().setDataElements(0, 0, cols, rows, data);
			break;

		case 3:
			int width = matrix.width(), height = matrix.height(), channels = matrix.channels() ;  
			data = new byte[width * height * channels];  
			matrix.get(0, 0, data);  
			// create new image and get reference to backing data  
			image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);  
			final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();  
			System.arraycopy(data, 0, targetPixels, 0, data.length);
			break;

		default:  
			System.out.println("Unknown mat type.");
			return null;  
		} 

		return image;

	}

}
