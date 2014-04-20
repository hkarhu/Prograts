package coderats.ar;
import java.awt.image.BufferedImage;

import org.opencv.core.Mat;

import ae.routines.S;


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
		int elemSize = (int)matrix.elemSize();  
		byte[] data = new byte[cols * rows * elemSize];  
		int type;  
		matrix.get(0, 0, data);
		switch (matrix.channels()) {  
		case 1:  
			type = BufferedImage.TYPE_BYTE_GRAY;
			break;  
		case 3:  
			type = BufferedImage.TYPE_INT_BGR;  
			// bgr to rgb
			byte b;  
			for(int i=0; i<data.length; i=i+3) {  
				b = data[i];  
				data[i] = data[i+2];  
				data[i+2] = b;  
			}  
			break;  
		default:  
			S.debug("Unknown mat type.");
			return null;  
		}  
		BufferedImage image = new BufferedImage(cols, rows, type);  
		image.getRaster().setDataElements(0, 0, cols, rows, data);
		return image;
	}  

}
