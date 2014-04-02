package tripcodescanner;



public class AlgoExtrapolateSamplePoint {

	public static void extrapolateSamplePoint(double xUnitCircle,double yUnitCircle, CircleCodes circleCodes) {
		
		/*
        double ScaleFactor1a = 1.2;
        double ScaleFactor1 = 1.4;
        double ScaleFactor1b = 1.6;

        double ScaleFactor2a = 1.8;
        double ScaleFactor2 = 2.0;
        double ScaleFactor2b = 2.2;
        */

        double ScaleFactor1a = 1.3;
        double ScaleFactor1 = 1.4;
        double ScaleFactor1b = 1.5;

        double ScaleFactor2a = 1.9;
        double ScaleFactor2 = 2.0;
        double ScaleFactor2b = 2.1;



        circleCodes.xRingCode1.value = xUnitCircle * ScaleFactor1;
        circleCodes.yRingCode1.value = yUnitCircle * ScaleFactor1;
        circleCodes.xRingCode1a.value = xUnitCircle * ScaleFactor1a;
        circleCodes.yRingCode1a.value = yUnitCircle * ScaleFactor1a;
        circleCodes.xRingCode1b.value = xUnitCircle * ScaleFactor1b;
        circleCodes.yRingCode1b.value = yUnitCircle * ScaleFactor1b;

        circleCodes.xRingCode2.value = xUnitCircle * ScaleFactor2;
        circleCodes.yRingCode2.value = yUnitCircle * ScaleFactor2;
        circleCodes.xRingCode2a.value = xUnitCircle * ScaleFactor2a;
        circleCodes.yRingCode2a.value = yUnitCircle * ScaleFactor2a;
        circleCodes.xRingCode2b.value = xUnitCircle * ScaleFactor2b;
        circleCodes.yRingCode2b.value = yUnitCircle * ScaleFactor2b;
		
	} // method

} // class
