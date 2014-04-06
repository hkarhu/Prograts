package tripcodescanner;


import java.util.ArrayList;

public class AlgoTrackEdgels {
	
	public static void trackEdgels(ImageDataPack imageDataPack, ArrayList<Edgel> edgels, int initEdgelX, int initEdgelY) {
		
		final byte[] edgeDetectedNMSImage = imageDataPack.getEdgeDetectedNMSImage();
		final byte[] edgeDetectedImage = imageDataPack.getEdgeDetectedImage();
		final int imageWidth = imageDataPack.getWidth();
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
            if ((edgeDetectedImage[posPixel]&255) != 0) {
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
