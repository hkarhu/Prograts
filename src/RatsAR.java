
public class RatsAR {

	public static final int WINDOW_WIDTH = 1280/2;
	public static final int WINDOW_HEIGHT = 768/2;
	
	static OpenGLTableAugment table;
	
	public static void main(String[] args) {
		table = new OpenGLTableAugment();
		table.startGL();
	}

}
