package coderats.ar;


import java.util.ArrayList;

import org.opencv.core.Mat;

public class AlgoTrackEdgels {
	
	private final static int THRESHOLD = 1;
	private final static int BLACK = 0;
	
	public static void trackEdgels(Mat frame, ArrayList<Edgel> edgels, int initEdgelX, int initEdgelY) {
		
		int prevX = initEdgelX;
        int prevY = initEdgelY;
        int testX, testY;
       
        boolean continueLoop = true;

        // Clockwise from the pixel on the right hand side of the current one.
        do {
        	System.out.println("Start: " + prevX + " " + prevY + " " + frame.get(prevY, prevX)[0]);
        	
        	if(frame.get(prevY, prevX)[0] < THRESHOLD) break;
        	
            // 8 connected ordering clockwise starting on pixel on the rhs
            testX = prevX;
            testY = prevY + 1;
            if (frame.get(testY, testX)[0] > THRESHOLD) {
                edgels.add(new Edgel(testX, testY));
                // this pixel has already been included in an edge so wipe it out
                frame.put(testY, testX, new double[] {BLACK});
                prevX = testX;
                prevY = testY;
                continue;
            }

            testX = prevX + 1;
            testY = prevY + 1;
            if (pixelInsideFrame(testX, testY, frame) && frame.get(testY, testX)[0] > THRESHOLD) {
                edgels.add(new Edgel(testX, testY));
                // this pixel has already been included in an edge so wipe it out
                frame.put(testY, testX, new double[] {BLACK});
                prevX = testX;
                prevY = testY;
                continue;
            }

            testX = prevX + 1;
            testY = prevY;
            if (pixelInsideFrame(testX, testY, frame) &&  frame.get(testY, testX)[0] > THRESHOLD) {
                edgels.add(new Edgel(testX, testY));
                // this pixel has already been included in an edge so wipe it out
                frame.put(testY, testX, new double[] {BLACK});
                prevX = testX;
                prevY = testY;
                continue;
            }

            testX = prevX + 1;
            testY = prevY - 1;
            if (pixelInsideFrame(testX, testY, frame) && frame.get(testY, testX)[0] < THRESHOLD) {
                edgels.add(new Edgel(testX, testY));
                // this pixel has already been included in an edge so wipe it out
                frame.put(testY, testX, new double[] {BLACK});
                prevX = testX;
                prevY = testY;
                continue;
            }

            testX = prevX;
            testY = prevY - 1;
            if (pixelInsideFrame(testX, testY, frame) && frame.get(testY, testX)[0] < THRESHOLD) {
                edgels.add(new Edgel(testX, testY));
                // this pixel has already been included in an edge so wipe it out
                frame.put(testY, testX, new double[] {BLACK});
                prevX = testX;
                prevY = testY;
                continue;
            }

            testX = prevX - 1;
            testY = prevY - 1;
            if (pixelInsideFrame(testX, testY, frame) && frame.get(testY, testX)[0] < THRESHOLD) {
                edgels.add(new Edgel(testX, testY));
                // this pixel has already been included in an edge so wipe it out
                frame.put(testY, testX, new double[] {BLACK});
                prevX = testX;
                prevY = testY;
                continue;
            }

            testX = prevX - 1;
            testY = prevY;
            if (pixelInsideFrame(testX, testY, frame) && frame.get(testY, testX)[0] < THRESHOLD) {
                edgels.add(new Edgel(testX, testY));
                // this pixel has already been included in an edge so wipe it out
                frame.put(testY, testX, new double[] {BLACK});
                prevX = testX;
                prevY = testY;
                continue;
            }

            testX = prevX - 1;
            testY = prevY + 1;
            if (pixelInsideFrame(testX, testY, frame) && frame.get(testY, testX)[0] < THRESHOLD) {
                edgels.add(new Edgel(testX, testY));
                // this pixel has already been included in an edge so wipe it out
                frame.put(testY, testX, new double[] {BLACK});
                prevX = testX;
                prevY = testY;
                continue;
            }
            
            continueLoop = false;

        }
        while (continueLoop);
    }
	
	private static boolean pixelInsideFrame(int x, int y, Mat frame){
		return !(x >= frame.width() || y >= frame.height() || x < 0 || y  < 0);
	}
	
}
