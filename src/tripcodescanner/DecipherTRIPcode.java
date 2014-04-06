package tripcodescanner;
import ae.routines.S;

public class DecipherTRIPcode {
	
	private final static int BITS = 16;
	
	public static void debug_ptatpoint(double x, double y) {
		byte clr = debugImg[(int) ((x)*debugImageWidth+y)];
		if ( clr < 0 ) clr = 127;
		else clr = -128;
		
		debugImg[(int) ((x)*debugImageWidth+y)] = clr;
		debugImg[(int) ((x+1)*debugImageWidth+y)] = clr;
		debugImg[(int) ((x-1)*debugImageWidth+y)] = clr;
		debugImg[(int) ((x)*debugImageWidth+y+1)] = clr;
		debugImg[(int) ((x)*debugImageWidth+y-1)] = clr;
	}
	
	public static byte[] debugImg;
	public static int debugImageWidth;
	private static int run = 0;
	
	public static String decipherTRIPcode(EllipseParams params, int imageWidth, int imageHeight, byte[] thresholdedImage) {
		
		debugImg = thresholdedImage.clone();
		debugImageWidth = imageWidth;
		
		double alpha = 0.0, beta = 0.0, gamma = 0.0;
        double x_coord1, y_coord1;

        double ellipseAlpha = params.getAlpha();
        double cosAlpha = Math.cos(ellipseAlpha);
        double sinAlpha = Math.sin(ellipseAlpha);

        double x_rotated, y_rotated;
        double xUnitCircleRotated = 0.0;
        double yUnitCircleRotated = 0.0;
        double xUnitCircle, yUnitCircle;
        boolean sectorFound = false;
        double x1 = params.getX()-1;
        double y1 = params.getY()-1;
        double a = params.getA();
        double b = params.getB();
        
        S.printf(
        		"x:%f, y:%f, a:%f, b:%f",
        		x1,y1,a,b);


        // Take as point of reference the point of the ellipse (params.x1 - params.a, y)
        x_coord1 = x1 - a;
        y_coord1 = y1;

        // Now rotate this point alpha degrees
        x_rotated = ( (x_coord1 - x1) * Math.cos(ellipseAlpha)) -
                      ((y_coord1 - y1) * Math.sin(ellipseAlpha)) + x1;
        y_rotated = ( (y_coord1 - y1) * Math.cos(ellipseAlpha)) +
                      ((x_coord1 - x1) * Math.sin(ellipseAlpha)) + y1;
        x_coord1 = x_rotated;
        y_coord1 = y_rotated;

        // Tranform it to the unit circle
        // Translate the point to the origin, rotate by -alpha and unstretch the point
        xUnitCircle = ( (x_coord1 - x1) * cosAlpha +
                       ( (y_coord1 - y1) * sinAlpha)) / a;
        yUnitCircle = ( ( -1.0 * (x_coord1 - x1) * sinAlpha) +
                       ( (y_coord1 - y1) * cosAlpha)) / b;

        // Find the same point in the two concentric ring code circles
        // Scale point (xUnitCircle, yUnitCircle) to obtain points in RingCode1 and RingCode2
        // Take three samples for each ring code
        CircleCodes circleCodes = new CircleCodes();
        AlgoExtrapolateSamplePoint.extrapolateSamplePoint(xUnitCircle, yUnitCircle, circleCodes);

        // Transform back to the ellipse
        AlgoCircle2Ellipse.circle2Ellipse(circleCodes,a, b, x1, y1, sinAlpha, cosAlpha);

        sectorFound = false;

        System.out.println("begining pointsWithinImageFrame: "+AlgoPointsWithinImageFrame.pointsWithinImageFrame(circleCodes, imageWidth, imageHeight) + " " +
        		circleCodes);
        
        if (AlgoPointsWithinImageFrame.pointsWithinImageFrame(circleCodes, imageWidth, imageHeight)) {
        	if (AlgoPointsBothBlack.pointsBothBlack(circleCodes, thresholdedImage, imageWidth)) {
                sectorFound = true;
            } else {
                // FIND BEGINNING OF RING CODES
                int sectorCounter = 0;
                beta = 0.0;

//                while ( (sectorCounter < 39) && (!sectorFound)) {
                while ( (sectorCounter < BITS) && (!sectorFound)) { // 48 bits
                    // New line equation with +15 degrees rotation
                    // Rotate (x_coord1, y_coord1) point 15 degrees (beta)
                    //cout << endl << "Sector Counter: " << sectorCounter;
//                    beta += Math.PI / 20.0;
                	beta += Math.PI / BITS / 2; // 48 bits
                    xUnitCircleRotated = xUnitCircle * Math.cos(beta) -
                                         yUnitCircle * Math.sin(beta);
                    yUnitCircleRotated = xUnitCircle * Math.sin(beta) +
                                         yUnitCircle * Math.cos(beta);

                    // 3. Find the same point in the two concentric ring code circles
                    // Scale point (xUnitCircle, yUnitCircle) to obtain points in RingCode1 and RingCode2
                    AlgoExtrapolateSamplePoint.extrapolateSamplePoint(xUnitCircleRotated, yUnitCircleRotated,circleCodes);
                    
//                    debug_ptatpoint(circleCodes.xRingCode1.value,
//                            circleCodes.yRingCode1.value);
//                    
//                    
//                    debug_ptatpoint(circleCodes.xRingCode1a.value,
//                    		circleCodes.yRingCode1a.value);
//                    
//                    debug_ptatpoint(circleCodes.xRingCode1b.value,
//                    		circleCodes.yRingCode1b.value);
//                    
//                    TRIPscanner.writePGM(debugImg, "debugImgRun"+(run ++)+".pgm", imageWidth, imageHeight);
//                    System.err.println("wut not here?");

                    // Transform back to ellipses
                    AlgoCircle2Ellipse.circle2Ellipse(circleCodes,a, b, x1, y1, sinAlpha, cosAlpha);

                    if (AlgoPointsWithinImageFrame.pointsWithinImageFrame(circleCodes, imageWidth, imageHeight)) {
                    	debug_ptatpoint(circleCodes.x_int1.value, circleCodes.y_int1.value);
                    	debug_ptatpoint(circleCodes.x_int1a.value, circleCodes.y_int1a.value);
                    	debug_ptatpoint(circleCodes.x_int1b.value, circleCodes.y_int1b.value);
                    	debug_ptatpoint(circleCodes.x_int2.value, circleCodes.y_int2.value);
                    	debug_ptatpoint(circleCodes.x_int2a.value, circleCodes.y_int2a.value);
                    	debug_ptatpoint(circleCodes.x_int2b.value, circleCodes.y_int2b.value);
                    TRIPCodeScanner.writePGM(debugImg, "debugrun\\01_debugImgRun"+S.sprintf("%03d", sectorCounter)+".pgm", imageWidth, imageHeight);
//                    	thresholdedImage[ * imageWidth + ]&255) == AlgorithmConstants.BLACK) ||
//      	              ((thresholdedImage[circleCodes.x_int1a.value * imageWidth + circleCodes.y_int1a.value]&255) == AlgorithmConstants.BLACK) ||
//      	              ((thresholdedImage[circleCodes.x_int1b.value * imageWidth + circleCodes.y_int1b.value]&255) == AlgorithmConstants.BLACK)) &&
//      	            ( ((thresholdedImage[circleCodes.x_int2.value * imageWidth + circleCodes.y_int2.value]&255) == AlgorithmConstants.BLACK) ||
//      	             ((thresholdedImage[circleCodes.x_int2a.value * imageWidth + circleCodes.y_int2a.value]&255) == AlgorithmConstants.BLACK) ||
//      	             ((thresholdedImage[circleCodes.x_int2b.value * imageWidth + circleCodes.y_int2b.value]&255) == AlgorithmConstants.BLACK))) { // Two blacks
//      	            return true;
                        if (AlgoPointsBothBlack.pointsBothBlack(circleCodes, thresholdedImage, imageWidth)) {
                            sectorFound = true;
                        }
                    }
                    else {
                    	System.out.println("guugaa");
                    }
                    sectorCounter++;
                }

            }
        }
        
        debugImg = thresholdedImage.clone();
        run = 0;

        if (!sectorFound) {
            System.out.println("***ERROR: TRIPcode beginning of sector not found!***");
            return null;
        }

        // Rotate clockwise till we identify the border of the RIGHT-MOST Point of the sector
        double offset;
//        offset = gamma = -Math.PI / 18.0; // 10.0 degrees
        offset = gamma = -Math.PI / BITS / 2; // 48 bits
        int iterations = 0;
        double angle;
        do {
            angle = beta + gamma;
            xUnitCircleRotated = xUnitCircle * Math.cos(angle) -
                                 yUnitCircle * Math.sin(angle);
            yUnitCircleRotated = xUnitCircle * Math.sin(angle) +
                                 yUnitCircle * Math.cos(angle);

            // 3. Find the same point in the two concentric ring code circles
            // Scale point (xUnitCircle, yUnitCircle) to obtain points in RingCode1 and RingCode2
            AlgoExtrapolateSamplePoint.extrapolateSamplePoint(xUnitCircleRotated, yUnitCircleRotated,circleCodes);

            // Transform back to ellipses
            AlgoCircle2Ellipse.circle2Ellipse(circleCodes, a, b, x1, y1, sinAlpha, cosAlpha);

            boolean foundNow = false;
            if (AlgoPointsWithinImageFrame.pointsWithinImageFrame(circleCodes, imageWidth, imageHeight)) {
            	debug_ptatpoint(circleCodes.x_int1.value, circleCodes.y_int1.value);
            	debug_ptatpoint(circleCodes.x_int1a.value, circleCodes.y_int1a.value);
            	debug_ptatpoint(circleCodes.x_int1b.value, circleCodes.y_int1b.value);
            	debug_ptatpoint(circleCodes.x_int2.value, circleCodes.y_int2.value);
            	debug_ptatpoint(circleCodes.x_int2a.value, circleCodes.y_int2a.value);
            	debug_ptatpoint(circleCodes.x_int2b.value, circleCodes.y_int2b.value);
            TRIPCodeScanner.writePGM(debugImg, "debugrun\\02_debugImgRightmostSector"+S.sprintf("%03d", run++)+".pgm", imageWidth, imageHeight);
                if (AlgoPointsBothBlack.pointsBothBlack(circleCodes, thresholdedImage, imageWidth)) {
                    foundNow = true;
                }
            }
            else {
            	S.printf("�����");
            }

            if (!foundNow) {
                // if no match was found rotate clockwise again but by a smaller angle
                offset /= 2;
                gamma += ( -1.0 * (offset));
            } else {
                gamma += offset;
            }
            // If it was found keep rotating by the previous angle
            iterations++;
        }
        while ( (Math.abs(offset) > Math.PI / 360.0) && (iterations < 15)); // 0.5 degree

        debugImg = thresholdedImage.clone();
        debug_ptatpoint(circleCodes.x_int1.value, circleCodes.y_int1.value);
    	debug_ptatpoint(circleCodes.x_int1a.value, circleCodes.y_int1a.value);
    	debug_ptatpoint(circleCodes.x_int1b.value, circleCodes.y_int1b.value);
    	debug_ptatpoint(circleCodes.x_int2.value, circleCodes.y_int2.value);
    	debug_ptatpoint(circleCodes.x_int2a.value, circleCodes.y_int2a.value);
    	debug_ptatpoint(circleCodes.x_int2b.value, circleCodes.y_int2b.value);
    	TRIPCodeScanner.writePGM(debugImg, "debugrun\\02_debugImgRightmostSectorFinal"+S.sprintf("%03d", run++)+".pgm", imageWidth, imageHeight);
    	debugImg = thresholdedImage.clone();
    	run = 0;

        int numCodeErrors = 0;
        int parityRingCode1 = 0;
        int num1s = 0;
        int num2s = 0;
//        alpha = angle + Math.PI / 16.0 + Math.PI / 8.0; // Middle of first parity checking sector
//        alpha = angle + Math.PI / 24.0 ; // 48 bits
        alpha = angle + Math.PI / 16.0 + Math.PI / 16.0;
        x_rotated = xUnitCircle * Math.cos(alpha) - yUnitCircle * Math.sin(alpha);
        y_rotated = xUnitCircle * Math.sin(alpha) + yUnitCircle * Math.cos(alpha);

        // 3. Find the same point in the two concentric ring code circles
        // Scale point (xUnitCircle, yUnitCircle) to obtain points in RingCode1 and RingCode2
        AlgoExtrapolateSamplePoint.extrapolateSamplePoint(x_rotated, y_rotated,circleCodes);

        // Transform back to the ellipse
        AlgoCircle2Ellipse.circle2Ellipse(circleCodes,a, b, x1, y1, sinAlpha, cosAlpha);

        if (AlgoPointsWithinImageFrame.pointsWithinImageFrame(circleCodes, imageWidth, imageHeight)) {
        	debug_ptatpoint(circleCodes.x_int1.value, circleCodes.y_int1.value);
        	debug_ptatpoint(circleCodes.x_int1a.value, circleCodes.y_int1a.value);
        	debug_ptatpoint(circleCodes.x_int1b.value, circleCodes.y_int1b.value);
        	debug_ptatpoint(circleCodes.x_int2.value, circleCodes.y_int2.value);
        	debug_ptatpoint(circleCodes.x_int2a.value, circleCodes.y_int2a.value);
        	debug_ptatpoint(circleCodes.x_int2b.value, circleCodes.y_int2b.value);
        TRIPCodeScanner.writePGM(debugImg, "debugrun\\03_debugMiddleFirstParityCheck"+S.sprintf("%03d", run++)+".pgm", imageWidth, imageHeight);
            if (AlgoPointsBothBlack.pointsBothBlack(circleCodes, thresholdedImage, imageWidth)) {
                System.out.println("***ERROR: Parity in Ring 1 wrong!***");
                numCodeErrors++;
            } else {
                if (AlgoOnePointOnlyBlack.onePointOnlyBlack(circleCodes, thresholdedImage, imageWidth)) {
                    if (AlgoPointBlackFirstRing.pointBlackFirstRing(circleCodes, thresholdedImage, imageWidth)) {
                        parityRingCode1 = 1;
                    } else {
                        numCodeErrors++;
                    }
                } else { // Two whites
                    parityRingCode1 = 0;
                }
            }
        }
        else S.printf("������������");
        
        debugImg = thresholdedImage.clone();
        run = 0;

        int parityRingCode2 = 0;
//        alpha += Math.PI / 8.0;
        alpha += Math.PI / 12; // 48 bits
        x_rotated = xUnitCircle * Math.cos(alpha) - yUnitCircle * Math.sin(alpha);
        y_rotated = xUnitCircle * Math.sin(alpha) + yUnitCircle * Math.cos(alpha);

        // 3. Find the same point in the two concentric ring code circles
        // Scale point (xUnitCircle, yUnitCircle) to obtain points in RingCode1 and RingCode2
        AlgoExtrapolateSamplePoint.extrapolateSamplePoint(x_rotated, y_rotated,circleCodes);

        // Transform back to the ellipse
        AlgoCircle2Ellipse.circle2Ellipse(circleCodes,a, b, x1, y1, sinAlpha, cosAlpha);

        if (AlgoPointsWithinImageFrame.pointsWithinImageFrame(circleCodes, imageWidth, imageHeight)) {
        	debug_ptatpoint(circleCodes.x_int1.value, circleCodes.y_int1.value);
        	debug_ptatpoint(circleCodes.x_int1a.value, circleCodes.y_int1a.value);
        	debug_ptatpoint(circleCodes.x_int1b.value, circleCodes.y_int1b.value);
        	debug_ptatpoint(circleCodes.x_int2.value, circleCodes.y_int2.value);
        	debug_ptatpoint(circleCodes.x_int2a.value, circleCodes.y_int2a.value);
        	debug_ptatpoint(circleCodes.x_int2b.value, circleCodes.y_int2b.value);
        TRIPCodeScanner.writePGM(debugImg, "debugrun\\04_debugsamePointConcentricCircles"+S.sprintf("%03d", run++)+".pgm", imageWidth, imageHeight);
            if (AlgoPointsBothBlack.pointsBothBlack(circleCodes,thresholdedImage, imageWidth)) {
                System.out.println("***ERROR: Parity in Ring 2 wrong!***");
                numCodeErrors++;
            } else {
                if (AlgoOnePointOnlyBlack.onePointOnlyBlack(circleCodes,thresholdedImage,imageWidth)) {
                    if (!AlgoPointBlackFirstRing.pointBlackFirstRing(circleCodes, thresholdedImage, imageWidth)) {
                        parityRingCode2 = 1;
                    }
                    else {
                        numCodeErrors++;
                    }
                }
                else { // Two whites
                    parityRingCode2 = 0;
                }
            }
        }
        else {
        	S.printf("AAAAAAAA");
        }
        
        debugImg = thresholdedImage.clone();
        run = 1;

        // Now rotate 22.5 degrees and check the code
        String code = "";
//        for (int i = 0; i < 13; i++) {
        for (int i = 0; i < 21; i++) { // 48 bit      	
            // 2. Rotate the point in the circle 22.5 degrees.
//            alpha += Math.PI / 8.0;
        	alpha += Math.PI / 12; // 48 bits
            x_rotated = xUnitCircle * Math.cos(alpha) - yUnitCircle * Math.sin(alpha);
            y_rotated = xUnitCircle * Math.sin(alpha) + yUnitCircle * Math.cos(alpha);

            // 3. Find the same point in the two concentric ring code circles
            // Scale point (xUnitCircle, yUnitCircle) to obtain points in RingCode1 and RingCode2
            AlgoExtrapolateSamplePoint.extrapolateSamplePoint(x_rotated, y_rotated,circleCodes);

            // Transform back to the ellipse
            AlgoCircle2Ellipse.circle2Ellipse(circleCodes,a, b, x1, y1, sinAlpha, cosAlpha);
            if (AlgoPointsWithinImageFrame.pointsWithinImageFrame(circleCodes, imageWidth, imageHeight)) {
            	debug_ptatpoint(circleCodes.x_int1.value, circleCodes.y_int1.value);
            	debug_ptatpoint(circleCodes.x_int1a.value, circleCodes.y_int1a.value);
            	debug_ptatpoint(circleCodes.x_int1b.value, circleCodes.y_int1b.value);
            	debug_ptatpoint(circleCodes.x_int2.value, circleCodes.y_int2.value);
            	debug_ptatpoint(circleCodes.x_int2a.value, circleCodes.y_int2a.value);
            	debug_ptatpoint(circleCodes.x_int2b.value, circleCodes.y_int2b.value);
            TRIPCodeScanner.writePGM(debugImg, "debugrun\\05_read"+S.sprintf("%03d", run++)+".pgm", imageWidth, imageHeight);
                if (AlgoPointsBothBlack.pointsBothBlack(circleCodes, thresholdedImage, imageWidth)) {
                    System.out.println("***ERROR: Impossible to find two black in sector " + (2+i) + "***");
                    code += "-1";
                    numCodeErrors++;
                } else {
                    if (AlgoOnePointOnlyBlack.onePointOnlyBlack(circleCodes, thresholdedImage, imageWidth)) {
                        if (AlgoPointBlackFirstRing.pointBlackFirstRing(circleCodes,thresholdedImage,imageWidth)) {
                            code += "1";
                            num1s++;
                        }
                        else {
                            code += "2";
                            num2s++;
                        }
                    } else { // Two whites
                        code += "0";
                    }
                }
            }
            else S.printf("fuckyou");
        }

        // If 2 or more illegal codes have been detected is very likely that what it has being checked is not a target
        if (numCodeErrors < 2) {
//            if (code.length() < 13) {
        	if (code.length() < 21) { // 48 bits
                System.out.println("***ERROR: Code imcomplete: " + code);
                return null;
            }
            else {
                if ( ( (parityRingCode1 + num1s) % 2 != 0) ||
                    ( (parityRingCode2 + num2s) % 2 != 0)) {
                    System.out.println("***ERROR: parity check did not pass: " + code);
                    return null;
                }
            }
            return code;
        } else {
            System.out.println("***ERROR: more than two errors were found while dechiphering the code: " + code);
            return null;
        }
	} // method

} // class
