package tripcodescanner;



public class AlgoPointBlackFirstRing {

	public static boolean pointBlackFirstRing(CircleCodes circleCodes, byte[] thresholdedImage, int imageWidth) {
		if ( ((thresholdedImage[circleCodes.x_int1.value * imageWidth + circleCodes.y_int1.value]&255) == AlgorithmConstants.BLACK) ||
	            ((thresholdedImage[circleCodes.x_int1a.value * imageWidth + circleCodes.y_int1a.value]&255) == AlgorithmConstants.BLACK) ||
	            ((thresholdedImage[circleCodes.x_int1b.value * imageWidth + circleCodes.y_int1b.value]&255) == AlgorithmConstants.BLACK)) { // Black first ring code
	            return true;
	        }
	        else {
	            return false;
	        }
	}

}
