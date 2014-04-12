package tripcodescanner;


import java.util.ArrayList;

import org.opencv.core.Mat;

public class AlgoTrackEdgels {
	
	public static void trackEdgels(byte[] edgeDetectedNMSImage, ArrayList<Edgel> edgels, int initEdgelX, int initEdgelY) {
		
		final int imageWidth = 64;
//		final int imageHeight = imageDataPack.getHeight();
		
		int prevX = initEdgelX;
        int prevY = initEdgelY;
        int testX, testY;

        boolean continueLoop = true;

        int posPixel;
        // Clockwise from the pixel on the right hand side of the current one.
        do {
            // 8 connected ordering clockwise starting on pixel on the rhs
            testX = prevX;
            testY = prevY + 1;
            posPixel = testX * imageWidth + testY;
            if ((edgeDetectedNMSImage[posPixel]&255) != 0) {
                edgels.add(new Edgel(testX, testY));
                // this pixel has already been included in an edge so wipe it out
                edgeDetectedNMSImage[posPixel] = AlgorithmConstants.BLACK;
                prevX = testX;
                prevY = testY;
                continue;
            }

            testX = prevX + 1;
            testY = prevY + 1;
            posPixel = testX * imageWidth + testY;
            if ((edgeDetectedNMSImage[posPixel]&255) != 0) {
                edgels.add(new Edgel(testX, testY));
                // this pixel has already been included in an edge so wipe it out
                edgeDetectedNMSImage[posPixel] = AlgorithmConstants.BLACK;
                prevX = testX;
                prevY = testY;
                continue;
            }

            testX = prevX + 1;
            testY = prevY;
            posPixel = testX * imageWidth + testY;
            if ((edgeDetectedNMSImage[posPixel]&255) != 0) {
                edgels.add(new Edgel(testX, testY));
                // this pixel has already been included in an edge so wipe it out
                edgeDetectedNMSImage[posPixel] = AlgorithmConstants.BLACK;
                prevX = testX;
                prevY = testY;
                continue;
            }

            testX = prevX + 1;
            testY = prevY - 1;
            posPixel = testX * imageWidth + testY;
            if ((edgeDetectedNMSImage[posPixel]&255) != 0) {
                edgels.add(new Edgel(testX, testY));
                // this pixel has already been included in an edge so wipe it out
                edgeDetectedNMSImage[posPixel] = AlgorithmConstants.BLACK;
                prevX = testX;
                prevY = testY;
                continue;
            }

            testX = prevX;
            testY = prevY - 1;
            posPixel = testX * imageWidth + testY;
            if ((edgeDetectedNMSImage[posPixel]&255) != 0) {
                edgels.add(new Edgel(testX, testY));
                // this pixel has already been included in an edge so wipe it out
                edgeDetectedNMSImage[posPixel] = AlgorithmConstants.BLACK;
                prevX = testX;
                prevY = testY;
                continue;
            }

            testX = prevX - 1;
            testY = prevY - 1;
            posPixel = testX * imageWidth + testY;
            if ((edgeDetectedNMSImage[posPixel]&255) != 0) {
                edgels.add(new Edgel(testX, testY));
                // this pixel has already been included in an edge so wipe it out
                edgeDetectedNMSImage[posPixel] = AlgorithmConstants.BLACK;
                prevX = testX;
                prevY = testY;
                continue;
            }

            testX = prevX - 1;
            testY = prevY;
            posPixel = testX * imageWidth + testY;
            if ((edgeDetectedNMSImage[posPixel]&255) != 0) {
                edgels.add(new Edgel(testX, testY));
                // this pixel has already been included in an edge so wipe it out
                edgeDetectedNMSImage[posPixel] = AlgorithmConstants.BLACK;
                prevX = testX;
                prevY = testY;
                continue;
            }

            testX = prevX - 1;
            testY = prevY + 1;
            posPixel = testX * imageWidth + testY;
            if ((edgeDetectedNMSImage[posPixel]&255) != 0) {
                edgels.add(new Edgel(testX, testY));
                // this pixel has already been included in an edge so wipe it out
                edgeDetectedNMSImage[posPixel] = AlgorithmConstants.BLACK;
                prevX = testX;
                prevY = testY;
                continue;
            }

            continueLoop = false;

        }
        while (continueLoop);
    } // method

} // class
