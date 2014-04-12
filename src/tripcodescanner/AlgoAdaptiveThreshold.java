package tripcodescanner;



public class AlgoAdaptiveThreshold {
	
	public static byte[] adaptiveThresholding(byte[] imageData, ImageDataPack imageDataPack) {
		
        // Create image where the result will be saved

        // Set the s parameter to 1/8 of the image width
        int s = imageDataPack.getWidth() / 8;
        // 85% blackness
        int t = 15;

        /*
         T(n) = 1 if p(n) < [ (f(n) / s) ( (100-t) / 100 ) ]
         T(n) = 0 otherwise
         where
         p(n) <--> im[n= rowNumber*Cols+colNumber]
         f(n) <--> 127*s for the 1st element
              prevG[iColumn-1] - (prevG[iColumn-1]/s) + im.frame[0][iColumn] for the rest of elements of 1st row
         */

        int[] prevG = new int[imageDataPack.getWidth()];
        int[] currentG = new int[imageDataPack.getWidth()];
        int[] swapG;

        // Process the first row of the image
        prevG[0] = 127 * s;
        if (imageData[0] < ( (prevG[0] / s) * ( (100.0 - t) / 100.0))) {
        	imageData[0] = AlgorithmConstants.BLACK; // Put color black
        }
        else {
        	imageData[0] = AlgorithmConstants.WHITE; // Put color white
        }

        for (int iColumn = 1; iColumn < imageDataPack.getWidth(); iColumn++) {
            prevG[iColumn] = prevG[iColumn - 1] - (prevG[iColumn - 1] / s) +
            	(imageData[iColumn]&255);

            if ((imageData[iColumn]&255) <
                ( (prevG[iColumn] / s) * ( (100.0 - t) / 100.0))) {
            	imageData[iColumn] = AlgorithmConstants.BLACK; // Put color black
            }
            else {
            	imageData[iColumn] = AlgorithmConstants.WHITE; // Put color white
            }
        }

        // Process the rest of the rows of the image
        int iRow = 1;
        while (iRow < imageDataPack.getHeight()) {
            // Sweep from Right to Left
            for (int iRightLeft = imageDataPack.getWidth() - 1; iRightLeft >= 0; iRightLeft--) {
                if (iRightLeft == imageDataPack.getWidth() - 1) {
                    currentG[iRightLeft] = prevG[imageDataPack.getWidth() - 1] -
                        (prevG[imageDataPack.getWidth() - 1] / s) +
                        (imageData[iRow*imageDataPack.getWidth() + iRightLeft]&255);
                }
                else {
                    currentG[iRightLeft] = currentG[iRightLeft + 1] -
                        (currentG[iRightLeft + 1] / s) +
                        (imageData[iRow*imageDataPack.getWidth() + iRightLeft]&255);

                    if ((imageData[iRow*imageDataPack.getWidth() + iRightLeft]&255) <
                        ( ( ( (currentG[iRightLeft] + prevG[iRightLeft]) / 2) /
                           s) *
                         ( (100.0 - t) / 100.0))) {
                    	imageData[iRow*imageDataPack.getWidth() + iRightLeft] = AlgorithmConstants.BLACK; // Put color black
                    }
                    else {
                    	imageData[iRow*imageDataPack.getWidth() + iRightLeft] = AlgorithmConstants.WHITE; // Put color white
                    }
                }
            }

            swapG = prevG;
            prevG = currentG;
            currentG = swapG;

            iRow++;
            if (iRow < imageDataPack.getHeight()) {
                // Sweep from Left to Right
                for (int iLeftRight = 0; iLeftRight < imageDataPack.getWidth(); iLeftRight++) {
                    if (iLeftRight == 0) {
                        currentG[iLeftRight] = prevG[imageDataPack.getWidth() - 1] -
                            (prevG[imageDataPack.getWidth() - 1] / s) +
                            (imageData[iRow*imageDataPack.getWidth() + iLeftRight]&255);
                    }
                    else {
                        currentG[iLeftRight] = currentG[iLeftRight - 1] -
                            (currentG[iLeftRight - 1] / s) +
                            (imageData[iRow*imageDataPack.getWidth() + iLeftRight]&255);
                    }

                    if ((imageData[iRow*imageDataPack.getWidth() + iLeftRight]&255) <
                        ( ( ( (currentG[iLeftRight] + prevG[iLeftRight]) / 2) /
                           s) * ( (100.0 - t) / 100.0))) {
                    	imageData[iRow*imageDataPack.getWidth() + iLeftRight] = AlgorithmConstants.BLACK; // Put color black
                    }
                    else {
                    	imageData[iRow*imageDataPack.getWidth() + iLeftRight] = AlgorithmConstants.WHITE; // Put color white
                    }
                }

                swapG = prevG;
                prevG = currentG;
                currentG = swapG;

                iRow++;
            }
        }
        
//        byte[] b = new byte[inImageData.length];
//        for (int i=0;i<b.length;i++) b[i] = (byte) thresholdedImage[i];
//        return b;
        return imageData;
    } // method

} // class
