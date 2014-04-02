package tripcodescanner;
import org.omg.CORBA.DoubleHolder;
import org.omg.CORBA.IntHolder;

import ae.routines.S;

public class CircleCodes {
	
	DoubleHolder xCircleRingCode1 = new DoubleHolder();
    DoubleHolder xCircleRingCode1a = new DoubleHolder();
    DoubleHolder xCircleRingCode1b = new DoubleHolder();
    DoubleHolder yCircleRingCode1 = new DoubleHolder();
    DoubleHolder yCircleRingCode1a = new DoubleHolder();
    DoubleHolder yCircleRingCode1b = new DoubleHolder();
    DoubleHolder xCircleRingCode2 = new DoubleHolder();
    DoubleHolder xCircleRingCode2a = new DoubleHolder();
    DoubleHolder xCircleRingCode2b = new DoubleHolder();
    DoubleHolder yCircleRingCode2 = new DoubleHolder();
    DoubleHolder yCircleRingCode2a = new DoubleHolder();
    DoubleHolder yCircleRingCode2b = new DoubleHolder();

    IntHolder x_int1 = new IntHolder();
    IntHolder x_int1a = new IntHolder();
    IntHolder x_int1b = new IntHolder();
    IntHolder y_int1 = new IntHolder();
    IntHolder y_int1a = new IntHolder();
    IntHolder y_int1b = new IntHolder();
    IntHolder x_int2 = new IntHolder();
    IntHolder x_int2a = new IntHolder();
    IntHolder x_int2b = new IntHolder();
    IntHolder y_int2 = new IntHolder();
    IntHolder y_int2a = new IntHolder();
    IntHolder y_int2b = new IntHolder();
    
    DoubleHolder xRingCode1 = new DoubleHolder();
    DoubleHolder xRingCode1a = new DoubleHolder();
    DoubleHolder xRingCode1b = new DoubleHolder();
    DoubleHolder yRingCode1 = new DoubleHolder();
    DoubleHolder yRingCode1a = new DoubleHolder();
    DoubleHolder yRingCode1b = new DoubleHolder();
    DoubleHolder xRingCode2 = new DoubleHolder();
    DoubleHolder xRingCode2a = new DoubleHolder();
    DoubleHolder xRingCode2b = new DoubleHolder();
    DoubleHolder yRingCode2 = new DoubleHolder();
    DoubleHolder yRingCode2a = new DoubleHolder();
    DoubleHolder yRingCode2b = new DoubleHolder();
    
    @Override
    public String toString() {
    	return S.sprintf(
    			"xCircleRingCode1:%f, xCircleRingCode1a:%f, xCircleRingCode1b:%f, yCircleRingCode1:%f, yCircleRingCode1a:%f, yCircleRingCode1b:%f," +
    			"xCircleRingCode2:%f, xCircleRingCode2a:%f, xCircleRingCode2b:%f, yCircleRingCode2:%f, yCircleRingCode2a:%f, yCircleRingCode2b:%f," +
    			"x_int1:%d, x_int1a:%d, x_int1b:%d, y_int1:%d, y_int1a:%d, y_int1b:%d, x_int2:%d, x_int2a:%d, x_int2b:%d, y_int2:%d, y_int2a:%d, y_int2b:%d," +
    			"xRingCode1:%f, xRingCode1a:%f, xRingCode1b:%f, yRingCode1:%f, yRingCode1a:%f, yRingCode1b:%f, xRingCode2:%f, xRingCode2a:%f, xRingCode2b:%f," +
    			"yRingCode2:%f, yRingCode2a:%f, yRingCode2b:%f",
    			xCircleRingCode1.value,
    			xCircleRingCode1a.value,
    			xCircleRingCode1b.value,
    			yCircleRingCode1.value,
    			yCircleRingCode1a.value,
    			yCircleRingCode1b.value,
    			xCircleRingCode2.value,
    			xCircleRingCode2a.value,
    			xCircleRingCode2b.value,
    			yCircleRingCode2.value,
    			yCircleRingCode2a.value,
    			yCircleRingCode2b.value,

    			x_int1.value,
    			x_int1a.value,
    			x_int1b.value,
    			y_int1.value,
    			y_int1a.value,
    			y_int1b.value,
    			x_int2.value,
    			x_int2a.value,
    			x_int2b.value,
    			y_int2.value,
    			y_int2a.value,
    			y_int2b.value,

    			xRingCode1.value,
    			xRingCode1a.value,
    			xRingCode1b.value,
    			yRingCode1.value,
    			yRingCode1a.value,
    			yRingCode1b.value,
    			xRingCode2.value,
    			xRingCode2a.value,
    			xRingCode2b.value,
    			yRingCode2.value,
    			yRingCode2a.value,
    			yRingCode2b.value
    			);
    }
} // class
