package tripcodescanner;



public class AlgoPointsWithinImageFrame {

	public static boolean pointsWithinImageFrame(CircleCodes circleCodes, int imageWidth, int imageHeight) {
		if ( (circleCodes.x_int1.value >= 0) && (circleCodes.x_int1.value < imageHeight) && (circleCodes.x_int2.value >= 0) &&
	            (circleCodes.x_int2.value < imageHeight) &&
	            (circleCodes.y_int1.value >= 0) && (circleCodes.y_int1.value < imageWidth) && (circleCodes.y_int2.value >= 0) &&
	            (circleCodes.y_int2.value < imageWidth) &&
	            (circleCodes.x_int1a.value >= 0) && (circleCodes.x_int1a.value < imageHeight) && (circleCodes.x_int2a.value >= 0) &&
	            (circleCodes.x_int2a.value < imageHeight) &&
	            (circleCodes.y_int1a.value >= 0) && (circleCodes.y_int1a.value < imageWidth) && (circleCodes.y_int2a.value >= 0) &&
	            (circleCodes.y_int2a.value < imageWidth) &&
	            (circleCodes.x_int1b.value >= 0) && (circleCodes.x_int1b.value < imageHeight) && (circleCodes.x_int2b.value >= 0) &&
	            (circleCodes.x_int2b.value < imageHeight) &&
	            (circleCodes.y_int1b.value >= 0) && (circleCodes.y_int1b.value < imageWidth) && (circleCodes.y_int2b.value >= 0) &&
	            (circleCodes.y_int2b.value < imageWidth)) {
	            return true;
	        }
	        else {
	            return false;
	        }
	}

}
