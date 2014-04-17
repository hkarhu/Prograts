package coderats.ar;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

import ae.AE;
import ae.gl.GLGraphicRoutines;
import ae.gl.GLValues;
import ae.gl.core.DisplayModePack;
import ae.gl.core.GLCore;
import ae.gl.core.GLKeyboardListener;
import ae.gl.texture.GLTextureManager;
import ae.routines.S;
import coderats.ar.gameScenes.AllocateScene;
import coderats.ar.gameScenes.AssembleScene;
import coderats.ar.gameScenes.GameScene;
import coderats.ar.gameScenes.IntroScene;


public class OpenGLTableAugment extends GLCore implements GLKeyboardListener, ARCardListener  {

	private long startTime;
	private long currentTime;
	private IntroScene intro;
	private AllocateScene allocateStage;
	private AssembleScene assembleStage;
	
	private ConcurrentLinkedDeque<ARCard> p1Cards, p2Cards;
	//private ConcurrentHashMap<Integer, ARCard> knownCards;
	
	private LinkedList<GameScene> gameScenes;
	
	public OpenGLTableAugment(ConcurrentHashMap<Integer, ARCard> knownCards) {
		
		p1Cards = new ConcurrentLinkedDeque<>();
		p2Cards = new ConcurrentLinkedDeque<>();
		
		gameScenes = new LinkedList<GameScene>();
		
		keyboardListeners.add(this);
		
		GLValues.setScreenSize(RatsAR.WINDOW_WIDTH, RatsAR.WINDOW_HEIGHT);
		GLValues.calculateRatios();
		
		//this.knownCards = knownCards;
		
		intro = new IntroScene(knownCards);
		allocateStage = new AllocateScene();
		assembleStage = new AssembleScene();

		resetGameEngine();
		
		startTime = System.currentTimeMillis();
		
	}

	@Override
	public DisplayModePack glPickDisplayMode() throws Exception {

		int desktopBpp = Display.getDesktopDisplayMode().getBitsPerPixel();
		
		if ( desktopBpp < 24 ) {
			throw new Exception( "Desktop bpp is too low." );
		}
		
		return new DisplayModePack(
				new DisplayMode(  GLValues.screenWidth,  GLValues.screenHeight ),
				new PixelFormat().withDepthBits( 24 ).withSamples( GLValues.antialiasSamples ),
				GLValues.fullScreen
		);
	}

	@Override
	public boolean glInit() {

		new GLTextureManager(getExecutorService()).initialize();

		GL11.glClearColor( 0.0f, 0.0f, 0.0f, 1.0f);

		GL11.glEnable( GL11.GL_ALPHA_TEST );
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GL11.glEnable( GL11.GL_DEPTH_TEST );
		GL11.glDepthFunc( GL11.GL_LEQUAL );

		GL11.glEnable( GL11.GL_BLEND );
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_COLOR_MATERIAL);

		GL11.glEnable(GL11.GL_NORMALIZE);
		GL11.glEnable( GL11.GL_CULL_FACE );
		GL11.glCullFace( GL11.GL_BACK );

		//GL11.glEnable( GL11.GL_BLEND );
		//GL11.glEnable( GL11.GL_LIGHTING );
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable( GL11.GL_DITHER );
		//GL11.glEnable( GL11.GL_SHADE_MODEL );
		GL11.glEnable( GL11.GL_SMOOTH );
		//GL11.glEnable( GL11.GL_POINT_SMOOTH );
		//GL11.glEnable( GL11.GL_LINE_SMOOTH );
		GL11.glShadeModel(GL11.GL_FLAT);
		//GL11.glEnable( GL11.GL_STENCIL_TEST );
		//GL11.glDisable( GL11.GL_FOG );
		//GL11.glHint(GL11.GL_POINT_SMOOTH_HINT, GL11.GL_NICEST);
		//GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
		//GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
		//GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_NICEST);
		//GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		//GL11.glBlendFunc(GL11.GL_SRC_ALPHA_SATURATE, GL11.GL_ONE);
		//GL11.glEnable( GL11.GL_STENCIL_TEST );
		//GL11.glDisable( GL11.GL_FOG );
		//GL11.glClearDepth(5.0f);
		//GL11.glEnable( GL11.GL_COLOR_MATERIAL );
		//GL11.glEnable( GL11.GL_POLYGON_SMOOTH );
		GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_NICEST);
		GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);

		GL11.glEnable(GL11.GL_LIGHT0);

		GL11.glClear(
				GL11.GL_COLOR_BUFFER_BIT |
				GL11.GL_DEPTH_BUFFER_BIT |
				GL11.GL_ACCUM_BUFFER_BIT |
				GL11.GL_STENCIL_BUFFER_BIT
				);


		// images
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get("data"), "*.{jpg,png}")) {
			for (Path path : stream) {
				String identifier = path.getFileName().toString();
				identifier = identifier.substring(0,identifier.length()-4);
				if (AE.isDebug()) S.debugFunc("Loading texture '%s' to identifier '%s'", path.toString(), identifier);
				GLTextureManager.getInstance().blockingLoad(path, identifier);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		Display.setLocation(2550, 0);
		
		return true;
	}

	@Override
	public void glLoop() {

		currentTime = System.currentTimeMillis() - startTime;
		
		GL11.glClear(
				GL11.GL_COLOR_BUFFER_BIT |
				GL11.GL_DEPTH_BUFFER_BIT |
				GL11.GL_ACCUM_BUFFER_BIT |
				GL11.GL_STENCIL_BUFFER_BIT
				);

		GL11.glLoadIdentity();
		GL11.glTranslatef(0, 0, 0);
		GLGraphicRoutines.initOrtho();

		//startScreen.glDraw(currentTime);
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, -1);
		GL11.glColor4f(0.0f, 0.0f, 0.0f, 1);
		GLGraphicRoutines.drawRepeatedBackgroundPlane(-GLValues.glWidth*0.49f, -GLValues.glHeight*0.49f, GLValues.glWidth*0.49f, GLValues.glHeight*0.49f);
		
		GL11.glPushMatrix();
		
			if(gameScenes.getFirst().isRunning()){
				gameScenes.getFirst().glDraw(currentTime);
			} else {
				gameScenes.removeFirst();
				if(gameScenes.isEmpty()){
					resetGameEngine();
				}
				gameScenes.getFirst().init();
				startTime = System.currentTimeMillis();
			}
		
		GL11.glPopMatrix();

		Display.sync(60);
		swapBuffers();

	}

	private void resetGameEngine(){
		gameScenes.add(intro);
		gameScenes.add(allocateStage);
		gameScenes.add(assembleStage);
	}
	
	@Override
	public void glFocusChanged(boolean isFocused) {
		// TODO Auto-generated method stub

	}

	@Override
	public void glTerminate() {
		GLTextureManager.getInstance().requestShutdown();
	}

	@Override
	public void glKeyDown(int eventKey) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void glKeyUp(int eventKey) {
		System.out.println(eventKey);
		gameScenes.getFirst().processInput(eventKey);
		
	}

	@Override
	public void cardDataUpdated(int id) {
		//knownCards.get(id).updateValues(x,y,a);
	}
	
	public void setKnownCards(ConcurrentHashMap<Integer, ARCard> t){
		System.out.println("setknow" + t.size());
	}

}
