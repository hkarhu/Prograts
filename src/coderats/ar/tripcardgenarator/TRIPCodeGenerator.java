package coderats.ar.tripcardgenarator;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;

public class TRIPCodeGenerator {

    private double wholeRadius;
    private Graphics2D g;
    private double ringWidth;

    public void drawTRIPcode(
            Graphics2D g2d,
            double xOffset, double yOffset, double radius,
            byte[][] ringsAndCodes
    ) {
        this.g = g2d;
        this.wholeRadius = radius;
        this.ringWidth = wholeRadius * .15d;
        
        final byte[] ring1Code = ringsAndCodes[0];
        final byte[] ring2Code = ringsAndCodes[1];

        // tell canvas to to draw antialiased shapes
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // move so 0,0 is center of ring
        final AffineTransform oldTransform = g.getTransform();
        g.translate(xOffset + wholeRadius + (ringWidth / 2), yOffset + wholeRadius + (ringWidth / 2));

        // set shape outline width
        g.setStroke(new BasicStroke((float) ringWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));

        Arc2D.Double ring = new Arc2D.Double();

        // draw outer ring
        ring.setArcByCenter(0, 0, wholeRadius, 0, 360, Arc2D.CHORD);
        g.draw(ring);

        // draw inner ring
        ring.setArcByCenter(0, 0, wholeRadius * .30f, 0, 360, Arc2D.CHORD);
        g.draw(ring);

        // draw ring 1 sectors
        drawBits(
                ring1Code,
                wholeRadius - (ringWidth * 1) - ((ringWidth * 1 * .6d))
                );

        // draw ring 2 sectors
        drawBits(
                ring2Code,
                wholeRadius - (ringWidth * 2) - ((ringWidth * 2 * .6d))
                );

        // restore old 0,0
        g.setTransform(oldTransform);

    }

    private void drawBits(byte[] sectorCode, double atRadius) {
        final double oneBitDegLength = (360d / sectorCode.length) * .9d;
        final double offset = 360 - oneBitDegLength * sectorCode.length;

        Arc2D.Double sectorArc = new Arc2D.Double();
        
        /*
        g.setColor(new Color(
                (int) (Math.random() * 255),
                (int) (Math.random() * 255),
                (int) (Math.random() * 255)
                ));
        */

        for (int i = 0; i < sectorCode.length; i++) {
            if (sectorCode[i] == 0) continue;
            sectorArc.setArcByCenter(
                    0, 0,
                    atRadius,
                    oneBitDegLength * i + (offset / 2),
                    oneBitDegLength,
                    Arc2D.OPEN);
            g.draw(sectorArc);
        }

        sectorArc.setArcByCenter(
                0, 0,
                wholeRadius * .6f,
                offset/2,
                -offset,
                Arc2D.OPEN);
        
        Stroke oldStroke = g.getStroke();
        g.setStroke(new BasicStroke((float) ringWidth*5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
        g.draw(sectorArc);
        g.setStroke(oldStroke);
    }

    public static byte[][] encodeTRIPCode(int num) {
        
        // here be ternary code generation
        
        /*
         * must: n.length == 2
         * must: [n][0].length == [n][1].length
         * must: [n][m] == 0 OR 1
         */
        
    	boolean codeOk = true;
    	int generatedCode[] = new int[6];
    	
    	for(int i=0; i < 6; i++){
    		generatedCode[i] = 0;
    	}
    	
    	int code = 0;
    	
    	while(true){
    		
    		codeOk = true;
    		
    		generatedCode[0]++;
    		
    		if(generatedCode[0] >= 3){
    			generatedCode[0] = 0;
    			generatedCode[1]++;
    		}
    		if(generatedCode[1] >= 3){
    			generatedCode[1] = 0;
    			generatedCode[2]++;
    		}
    		if(generatedCode[2] >= 3){
    			generatedCode[2] = 0;
    			generatedCode[3]++;
    		}
    		if(generatedCode[3] >= 3){
    			generatedCode[3] = 0;
    			generatedCode[4]++;
    		}
    		if(generatedCode[4] >= 3){
    			generatedCode[4] = 0;
    			generatedCode[5]++;
    		}
    		if(generatedCode[5] >= 3){
    			break;
    		}
    		
    		int ld = -1;
			for(int d : generatedCode){
				if(d == ld){
					codeOk = false;
					break;
				}
				ld = d;
			}
			
			if(!codeOk) continue;
    		
			if(code >= num) break;
			code++;
			
    	}
    	
    	byte out[][] = new byte[2][7];
    	
    	for(int i=0; i < 6; i++){
    		if(generatedCode[i] == 0){
    			out[0][i] = 0;
    			out[1][i] = 0;
    		} else if(generatedCode[i] == 1){
    			out[0][i] = 1;
    			out[1][i] = 0;    			
    		} else if(generatedCode[i] == 2){
    			out[0][i] = 0;
    			out[1][i] = 1;
    		}
    	}
    	
		out[0][6] = 0;
		out[1][6] = 0;
    	
        // example, dummy code
        return out;
    }

}
