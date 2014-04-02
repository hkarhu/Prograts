package tripcodescanner;



public class AlgoPointsWithinImageFrame {

	public static boolean pointsWithinImageFrame(CircleCodes circleCodes, int imageWidth, int imageHeight) {
		if ( (circleCodes.x_int1.value >= 0) && (circleCodes.x_int1.value < (int) imageHeight) && (circleCodes.x_int2.value >= 0) &&
	            (circleCodes.x_int2.value < (int) imageHeight) &&
	            (circleCodes.y_int1.value >= 0) && (circleCodes.y_int1.value < (int) imageWidth) && (circleCodes.y_int2.value >= 0) &&
	            (circleCodes.y_int2.value < (int) imageWidth) &&
	            (circleCodes.x_int1a.value >= 0) && (circleCodes.x_int1a.value < (int) imageHeight) && (circleCodes.x_int2a.value >= 0) &&
	            (circleCodes.x_int2a.value < (int) imageHeight) &&
	            (circleCodes.y_int1a.value >= 0) && (circleCodes.y_int1a.value < (int) imageWidth) && (circleCodes.y_int2a.value >= 0) &&
	            (circleCodes.y_int2a.value < (int) imageWidth) &&
	            (circleCodes.x_int1b.value >= 0) && (circleCodes.x_int1b.value < (int) imageHeight) && (circleCodes.x_int2b.value >= 0) &&
	            (circleCodes.x_int2b.value < (int) imageHeight) &&
	            (circleCodes.y_int1b.value >= 0) && (circleCodes.y_int1b.value < (int) imageWidth) && (circleCodes.y_int2b.value >= 0) &&
	            (circleCodes.y_int2b.value < (int) imageWidth)) {
	            return true;
	        }
	        else {
	            return false;
	        }
	}

}
