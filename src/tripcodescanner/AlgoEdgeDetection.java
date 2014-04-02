package tripcodescanner;

public class AlgoEdgeDetection {
	
	public static void edgeDetection(ImageDataPack imageDataPack) {
		
		final byte[] thresholdedImage = imageDataPack.getThresholdImage();
		final byte[] edgeDetectedImage = new byte[thresholdedImage.length];
		final byte[] edgeDetectedNMSImage = new byte[thresholdedImage.length];
		final int imageWidth = imageDataPack.getWidth();
		final int imageHeight = imageDataPack.getHeight();
		
        int sigma;
        int diagonal;
        int limitRows = imageHeight - 2;
        int limitCols = imageWidth - 2;
        int prevRow, postRow, curRow;
        for (int i = 2; i < limitRows; i++) {
            // process only black pixels
            prevRow = (i - 1) * imageWidth;
            postRow = (i + 1) * imageWidth;
            curRow = (i * imageWidth);
            for (int j = 2; j < limitCols; j++) {
                //System.out.println("Edge detection: (" + i + ", " + j + ")");
                if ((thresholdedImage[curRow + j]&255) == AlgorithmConstants.BLACK) {
                    diagonal = (thresholdedImage[prevRow + j + 1]&255) +
                    	(thresholdedImage[prevRow + j - 1]&255) +
                    	(thresholdedImage[postRow + j - 1]&255) +
                    	(thresholdedImage[postRow + j + 1]&255);
                    sigma = (thresholdedImage[curRow + j + 1]&255) +
                    	(thresholdedImage[prevRow + j]&255) +
                    	(thresholdedImage[curRow + j - 1]&255) +
                    	(thresholdedImage[postRow + j]&255) +
                        diagonal;

                    if ( ( (diagonal != 0) && (sigma == diagonal)) ||
                        (sigma == 0)) {
                    	edgeDetectedImage[curRow + j] = AlgorithmConstants.BLACK;
                        edgeDetectedNMSImage[curRow + j] = AlgorithmConstants.BLACK;
                    }
                    else {
                        edgeDetectedImage[curRow + j] = AlgorithmConstants.WHITE;
                        edgeDetectedNMSImage[curRow + j] = AlgorithmConstants.WHITE;
                    }
                }
                else {
                    edgeDetectedImage[curRow + j] = AlgorithmConstants.BLACK;
                    edgeDetectedNMSImage[curRow + j] = AlgorithmConstants.BLACK;
                }
            }
        }
        
        imageDataPack.setEdgeDetectedImage(edgeDetectedImage);
        imageDataPack.setEdgeDetectedNMSImage(edgeDetectedNMSImage);
        
    } // method

} // class
