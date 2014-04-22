package tripcodescanner;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import coderats.ar.AlgoEllipseFitting;
import ae.image.netpbm.PPMWriter;
import ae.image.netpbm.PPMWriterException;
import ae.routines.S;

public class TRIPCodeScanner {
	
//	private byte[] imageData;

//	private boolean debug;
	
//	private JFrame frame;
//	private BufferedImage bi;

	
	public static void scan(byte[] imageData, int imageWidth, int imageHeight, boolean debug) {
//		char c = 255;
//		byte b = (byte) 255;
//		
//		char c2 = (char) (b&255);
//		
//		System.out.println(c2);
//		System.out.println((imageData[28]&255));
//		System.out.println(c2);
//		if (1==1) return;
//		this.imageData = imageData;
		
//		this.debug = debug;
		
		
		
//		bi = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_BYTE_GRAY);
//		DataBufferByte dbb = (DataBufferByte) bi.getRaster().getDataBuffer();
//		
//		for ( int i = 0; i < imageData.length; i++ ) {
//			dbb.getData()[i] = imageData[i];
//		}
		
//		frame = new JFrame("TRIPScanner test debug window") {
//			@Override
//			public void paint(Graphics g) {
//				TRIPscanner.this.paint(g);
//			}
//		};
//		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//		frame.setSize(800,600);
//		frame.setVisible(true);
		
		final ImageDataPack imageDataPack = new ImageDataPack();
		imageDataPack.setWidth(imageWidth);
		imageDataPack.setHeight(imageHeight);
		
		writePGM(
				imageData,
				"00_original.pgm",imageWidth,imageHeight);
		
		
		imageDataPack.setThresholdImage(
				AlgoAdaptiveThreshold.adaptiveThresholding(imageData, imageDataPack)
				);
		writePGM(
				imageDataPack.getThresholdImage(),
				"01_adaptiveThreshold.pgm",imageWidth, imageHeight);
		
		
		AlgoEdgeDetection.edgeDetection(imageDataPack);
		writePGM(
				imageDataPack.getEdgeDetectedImage(),
				"02_edgeDetecton.pgm",imageWidth,imageHeight);
		
		ArrayList<ArrayList<Edgel>> edges = AlgoEdgeFollow.edgeFollowing(imageDataPack);
		writePGM(
				imageDataPack.getEdgeTrackedImage(),
				"03_edgeFollowed.pgm",imageWidth, imageHeight);
		
		int numEdges = edges.size();
        ArrayList<EllipseParams> paramEdgesEllipses = new ArrayList<EllipseParams>(numEdges);
        for (int i = 0; i < numEdges; i++) {
//        	int n = 0;
//        	for (Edgel e : edges.get(i)) {
//        		n++;
//        		S.printf("EDGES DEBUG("+i+","+n+"): " + edges);
//        	}
            EllipseParams params = AlgoEllipseFitting.ellipseFitting( edges.get(i));
            S.printf("ellipseParams debug("+i+"): " + params);
            paramEdgesEllipses.add(params);
        }
        
        final ArrayList<EllipseParams> paramMaxConcEllipses = AlgoFindConcentricEllipses.findConcentricEllipses(imageDataPack, edges, paramEdgesEllipses);
        final ArrayList<TargetParams> targetsRecognized = new ArrayList<TargetParams>();
        int numTargets = paramMaxConcEllipses.size();
        for (int iTarget = 0; iTarget < numTargets; iTarget++) {
            EllipseParams params = paramMaxConcEllipses.get(iTarget);
            System.out.println("EllipseParams: "+params);
            String code = DecipherTRIPcode.decipherTRIPcode(params, imageWidth, imageHeight, imageData);
            // HACK swap x and y in params, because it is wrong
            double tempX = params.getX();
            params.setX(params.getY());
            params.setY(tempX);
            targetsRecognized.add(new TargetParams(params, code));
        }
        
        
        for (int i=0; i<targetsRecognized.size(); i++) {
            TargetParams params = targetsRecognized.get(i);
            System.out.println("Code for target " + i + ": " + params.getCode() + " - " + params.getDecimalCode());
            System.out.println("Target location: " + params.getEllipseParams().getX() + ":" +
                               params.getEllipseParams().getY());
        }
		
	} // method
	
	
//	public void paint(Graphics g) {
//		g.drawImage(bi, 0, 0, null);
//	}
	
	
	public static void writePGM( byte[] imageData, String filename, int imageWidth, int imageHeight) {
		
		try {
			PPMWriter.writePGM(
					new ByteArrayInputStream(imageData),
					imageWidth, imageHeight,
					new FileOutputStream(new File(filename))
					);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PPMWriterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	} // method
	
}
