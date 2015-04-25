package fi.conf.prograts.ar.objects;

public abstract class GLBoardObject {
	
	protected int x = 0;
	protected int y = 0;
	
	public abstract void glDraw(long time);
	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
}
