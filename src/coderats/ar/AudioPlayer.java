package coderats.ar;

import java.io.File;
import java.util.HashMap;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

public class AudioPlayer {
	private final String[] SUPPORTED_FORMATS = {".wav"};
	private final int ONCE = 0;
	private final int LOOP = 1;
	private final int CONTINUOUS = Clip.LOOP_CONTINUOUSLY;
	private HashMap<String,File> loaded_files;
	
	/**
	 * Constructs a new plain audio player. Calling loadFiles("path") is still needed.
	 */
	public AudioPlayer() {
		loaded_files = new HashMap<String,File>();
	}
	
	/**
	 * Constructs a new audio player and loads files from the given path.
	 * @param folder_path
	 */
	public AudioPlayer(String folder_path) {
		this();
		loadFiles(folder_path);
		System.out.println("Loaded "+loaded_files.size()+" files");
		System.out.println(loaded_files.toString());
	}
	
	/*
	// Main for testing
    public static void main(String[] args)  {
    	AudioPlayer ap = new AudioPlayer("res/audio_files/");
        //ap.loadFiles("res/audio_files/");
    	ap.playAll();
        
    }
    */
	
    /**
     * Returns a HashMap<String, File> of loaded files.
     * @return
     */
    public HashMap getLoadedFiles() {
    	return loaded_files;
    }
    
    /**
     * Plays the given sound once. sound_name is sounds filename without suffix.
     * @param sound_name
     * @return
     */
    public Clip playSound(String sound_name) {
    	return playSound(sound_name, ONCE, 0);
    }
    
    /**
     * Starts looping the given sound. Set loop count with times parameter (AudioPlayer.CONTINUOUS == infinite)
     * @param sound_name
     * @param times
     * @return
     */
    public Clip loopSound(String sound_name, int times) {
    	return playSound(sound_name, LOOP, times);
    }
    
    /**
     * Plays the given sound according to given parameters :P
     * @param sound_name
     * @param type
     * @param times
     * @return
     */
    private Clip playSound(String sound_name, int type, int times) {
    	if(!loaded_files.containsKey(sound_name)) {
    		System.out.println("File '"+sound_name+"' is not loaded..");
    		return null;
    	}
    	
    	Clip clip = null;
    	
    	// Suppress all exceptions ;D
    	try {
			AudioInputStream ais = AudioSystem.getAudioInputStream( loaded_files.get(sound_name));
	        AudioFormat format = ais.getFormat();
	        
	        DataLine.Info info = new DataLine.Info(Clip.class, format);
	        clip = (Clip)AudioSystem.getLine(info);
	        
    		clip.open(ais);
	        
	        switch (type) {
	        	case LOOP:
	        		clip.loop(times);
	        		break;
	        	default:
	        		clip.start();
	        		break;
	        }
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	return clip;
    }
    
    /**
     * Loads supported files from the given path.
     * @param folder_path
     */
    public void loadFiles(String folder_path) {
    	System.out.println("Loading files from '" + folder_path + "'");
    	File folder = new File(folder_path);
    	File[] files = folder.listFiles();
    	for (File file : files) {
            if (file.isDirectory()) {
                System.out.println("Directory: " + file.getName());
                loadFiles(file.getPath()); // recursion ->
            } else {
                for(String suffix: SUPPORTED_FORMATS) {
                	if(file.getName().endsWith(suffix)) {
                		System.out.println("File '"+file.getName()+"' is supported.. loading to memory");
                    	loaded_files.put(file.getName().substring(0, file.getName().indexOf(suffix)), file);
                    	break;
                    }
                }
            }
        }
    }
    
    /**
     * Demo for testing sounds.
     */
    public void playAll() {
    	for(String key: loaded_files.keySet()) {
    		System.out.println("key: " + key + " value: " + loaded_files.get(key));
    		Clip sound = playSound(key);
    		do {
    			try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    		} while(sound.isRunning());
    	}
    }
}