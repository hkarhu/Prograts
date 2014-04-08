
public class RatsAR {

	public static final int WINDOW_WIDTH = 1280/2;
	public static final int WINDOW_HEIGHT = 768/2;
	
	static OpenGLTableAugment table;
	static WebcamImageProcessor input;
	
	public static void main(String[] args) {

		input = new WebcamImageProcessor();
		table = new OpenGLTableAugment();
		
		input.addListener(table);
		
		table.startGL();
		
	}

	public static void requestShutdown() {
		input.shutdown();
	}

}
