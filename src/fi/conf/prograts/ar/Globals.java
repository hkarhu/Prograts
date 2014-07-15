package fi.conf.prograts.ar;

public class Globals {

	public static final int WINDOW_WIDTH = 1280;
	public static final int WINDOW_HEIGHT = 800;
	public static final boolean FAKE_AR = false;
	
	//Parameters for all drawing. (Others elements read from here. Careful when touching)
	public static final float CARD_SCALE = 0.68f;
	
	public static final float CARD_WIDTH = 0.4f * CARD_SCALE;
	public static final float CARD_HEIGTH= 0.58f * CARD_SCALE;
	public static final float CARD_ROW_ARCH = 0.8f;

	public static final float CARD_ROW_SPACING = CARD_WIDTH*2 - 0.1f;
	
}
