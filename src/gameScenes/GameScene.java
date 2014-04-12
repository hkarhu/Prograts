package gameScenes;

public abstract class GameScene {

	private boolean running = false;
	
	public abstract void initialize();
	public abstract void glDraw(long time);
	
	public boolean isRunning() {
		return running;
	}
	
	public void setRunning(boolean running) {
		this.running = running;
	}
	
}
