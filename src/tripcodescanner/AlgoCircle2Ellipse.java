package tripcodescanner;

public class AlgoCircle2Ellipse {

	public static void circle2Ellipse(CircleCodes circleCodes, double a,
			double b, double x1, double y1, double sinAlpha, double cosAlpha) {
		// Transform back to the ellipse
        // Do the stretching, rotate the points alpha degrees, translate the point where it should be
        // Convert the points coordinates from double to integer
		circleCodes.x_int1.value = (int)Math.round( (circleCodes.xRingCode1.value * a * cosAlpha -
				circleCodes.yRingCode1.value * b * sinAlpha) + x1);
		circleCodes.y_int1.value = (int)Math.round( ( (circleCodes.xRingCode1.value * a * sinAlpha) +
                               (circleCodes.yRingCode1.value * b * cosAlpha) + y1));
		circleCodes.x_int1a.value = (int)Math.round( (circleCodes.xRingCode1a.value * a * cosAlpha -
				circleCodes.yRingCode1a.value * b * sinAlpha) + x1);
		circleCodes.y_int1a.value = (int)Math.round( ( (circleCodes.xRingCode1a.value * a * sinAlpha) +
                                (circleCodes.yRingCode1a.value * b * cosAlpha) + y1));
		circleCodes.x_int1b.value = (int)Math.round( (circleCodes.xRingCode1b.value * a * cosAlpha -
				circleCodes.yRingCode1b.value * b * sinAlpha) + x1);
		circleCodes.y_int1b.value = (int)Math.round( ( (circleCodes.xRingCode1b.value * a * sinAlpha) +
                                (circleCodes.yRingCode1b.value * b * cosAlpha) + y1));

		circleCodes.x_int2.value = (int)Math.round( (circleCodes.xRingCode2.value * a * cosAlpha -
				circleCodes.yRingCode2.value * b * sinAlpha) +
                             x1);
		circleCodes.y_int2.value = (int)Math.round( ( (circleCodes.xRingCode2.value * a * sinAlpha) +
                               (circleCodes.yRingCode2.value * b * cosAlpha) + y1));
		circleCodes.x_int2a.value = (int)Math.round( (circleCodes.xRingCode2a.value * a * cosAlpha -
				circleCodes.yRingCode2a.value * b * sinAlpha) + x1);
		circleCodes.y_int2a.value = (int)Math.round( ( (circleCodes.xRingCode2a.value * a * sinAlpha) +
                                (circleCodes.yRingCode2a.value * b * cosAlpha) + y1));
		circleCodes.x_int2b.value = (int)Math.round( (circleCodes.xRingCode2b.value * a * cosAlpha -
				circleCodes.yRingCode2b.value * b * sinAlpha) + x1);
		circleCodes.y_int2b.value = (int)Math.round( ( (circleCodes.xRingCode2b.value * a * sinAlpha) +
                                (circleCodes.yRingCode2b.value * b * cosAlpha) + y1));
	}

}
