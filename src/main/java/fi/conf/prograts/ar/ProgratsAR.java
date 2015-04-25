package fi.conf.prograts.ar;

import java.util.concurrent.ConcurrentHashMap;

import fi.conf.prograts.ar.objects.ARCard;
import fi.conf.prograts.ar.videoproc.WebcamImageProcessor;

public class ProgratsAR {
	
	static OpenGLTableAugment table;
	static WebcamImageProcessor input;
	
	public static void main(String[] args) {

		if(Globals.FAKE_AR){
			table = new OpenGLTableAugment(new ConcurrentHashMap<Integer, ARCard>());
		} else {
			input = new WebcamImageProcessor();
			table = new OpenGLTableAugment(input.getKnownCards());
			input.addListener(table);
		}
		
		table.startGL();
		
	}

	public static void requestShutdown() {
		if(!Globals.FAKE_AR) input.shutdown();
		table.requestClose();
	}

}
