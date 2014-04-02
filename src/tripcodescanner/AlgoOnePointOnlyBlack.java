package tripcodescanner;



public class AlgoOnePointOnlyBlack {

	public static boolean onePointOnlyBlack(CircleCodes circleCodes, byte[] thresholdedImage, int imageWidth) {
		boolean blackFirstRing = ((thresholdedImage[circleCodes.x_int1.value * imageWidth + circleCodes.y_int1.value]&255) == AlgorithmConstants.BLACK) ||
			((thresholdedImage[circleCodes.x_int1a.value * imageWidth + circleCodes.y_int1a.value]&255) == AlgorithmConstants.BLACK) ||
        	((thresholdedImage[circleCodes.x_int1b.value * imageWidth + circleCodes.y_int1b.value]&255) == AlgorithmConstants.BLACK);
		boolean blackSecondRing = ((thresholdedImage[circleCodes.x_int2.value * imageWidth + circleCodes.y_int2.value]&255) == AlgorithmConstants.BLACK) ||
			((thresholdedImage[circleCodes.x_int2a.value * imageWidth + circleCodes.y_int2a.value]&255) == AlgorithmConstants.BLACK) ||
			((thresholdedImage[circleCodes.x_int2b.value * imageWidth + circleCodes.y_int2b.value]&255) == AlgorithmConstants.BLACK);
		if (blackFirstRing && blackSecondRing) {
			return false;
		} else if (blackFirstRing || blackSecondRing) {
			return true;
		} else {
			return false;
		}
	} // method

} // class
