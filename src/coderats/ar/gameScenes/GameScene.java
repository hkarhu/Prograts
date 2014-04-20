package coderats.ar.gameScenes;

import coderats.ar.ARCardListener;

public abstract class GameScene implements ARCardListener {

	private boolean running = false;
	
	public abstract void init();
	public abstract void glDraw(long time);
	
	public boolean isRunning() {
		return running;
	}
	
	public void setRunning(boolean running) {
		this.running = running;
	}
	
	public abstract void processInput(int inputKey);
	
}
