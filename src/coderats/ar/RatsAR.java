package coderats.ar;

import java.util.concurrent.ConcurrentHashMap;

public class RatsAR {

	public static final int WINDOW_WIDTH = 1280;
	public static final int WINDOW_HEIGHT = 800;
	public static final boolean FAKE_AR = false;
	
	static OpenGLTableAugment table;
	static WebcamImageProcessor input;
	
	public static void main(String[] args) {

		if(FAKE_AR){
			table = new OpenGLTableAugment(new ConcurrentHashMap<Integer, ARCard>());
		} else {
			input = new WebcamImageProcessor();
			table = new OpenGLTableAugment(input.getKnownCards());
			input.addListener(table);
		}
		
		table.startGL();
		
	}

	public static void requestShutdown() {
		if(!FAKE_AR) input.shutdown();
		table.requestClose();
	}

}
