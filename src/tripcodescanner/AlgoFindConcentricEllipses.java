package tripcodescanner;
import java.util.ArrayList;


public class AlgoFindConcentricEllipses {
	
	public static ArrayList<EllipseParams> findConcentricEllipses(ImageDataPack imageDataPack, ArrayList<ArrayList<Edgel>> edges, ArrayList<EllipseParams> paramEdgesEllipses) {
		
		int numEdges = edges.size();
        int indexSecondSmallestTarget = 0, indexSmallestTarget = 0, indexThirdSmallestTarget = 0;

        int concentricEllipses[][] = new int[AlgorithmConstants.MAX_CONC_ELLIPSES][numEdges];
        ArrayList<EllipseParams> paramMaxConcEllipses = new ArrayList<EllipseParams>();
//        ArrayList param2ndBigConcEllipses = new ArrayList();

        double dx, dy;

        // book-keeping for a set of concentric ellipses identified
        int numConcEllipseTarget;

        // edgeBelongsTarget - a vector [0 ..n-1] concentric site each edgel belongs to
        int[] edgeBelongsTarget = new int[numEdges];

        // Set edges don't belong to any target (-1)
        for (int i = 0; i < numEdges; i++) {
            edgeBelongsTarget[i] = -1;
        }

        int targetsIdentified = 0;
        for (int j = 0; j < numEdges; j++) {
        	
        	
        	//BEGIN IMAGE
//        	if (!Double.isNaN(paramEdgesEllipses.get(j).getX())) {
//	        	BufferedImage b = new BufferedImage(imageDataPack.getWidth(), imageDataPack.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
//	        	Graphics2D g = b.createGraphics();
//	        	g.drawImage(imageDataPack.getEdgeTrackedImageBI(), 0, 0, null);
//	        	Ellipse2D.Float ellipse = new Ellipse2D.Float();
//	        	
//	        	g.setColor(Color.WHITE);
//	        	ellipse.setFrameFromCenter(
//	        			0,0,
//	        			paramEdgesEllipses.get(j).getB(),
//	        			paramEdgesEllipses.get(j).getA()
//	        			);
//	        	g.translate(
//	        			paramEdgesEllipses.get(j).getY(),
//	        			paramEdgesEllipses.get(j).getX()
//	        			);
//	        	g.rotate(-paramEdgesEllipses.get(j).getAlpha());
//	        	g.draw(ellipse);
////	        	g.setColor(Color.GREEN);
////	        	g.drawString(Double.toString(paramEdgesEllipses.get(j).getAlpha()), 20, 20);
//	        	
//	        	g.dispose();
//	        	try { ImageIO.write(b, "PNG", new FileOutputStream("debugrun/findConcentricEllipses"+j+".png")); } catch (FileNotFoundException e) { } catch (Exception e) {}
//        	}
        	//END IMAGE
        	
        	
            // Test if ellipse edge j has concentric ellipses to it
            if (edgeBelongsTarget[j] == -1) {
                numConcEllipseTarget = 0;
                for (int i = j + 1; i < numEdges; i++) {
                	
                    dx = Math.abs(
                    		paramEdgesEllipses.get(j).getX() -
                            paramEdgesEllipses.get(i).getX()
                            );
                    
                    dy = Math.abs(
                    		paramEdgesEllipses.get(j).getY() -
                    		paramEdgesEllipses.get(i).getY()
                    		);

                    // If the differences between the centers of the ellipses are
                    //   < PROXIMITY_THRESHOLD those ellipses are concentric
                    if (dx < AlgorithmConstants.PROXIMITY_THRESHOLD && dy < AlgorithmConstants.PROXIMITY_THRESHOLD) {
                        if (edgeBelongsTarget[j] == -1) {
                            edgeBelongsTarget[j] = targetsIdentified;
                            concentricEllipses[numConcEllipseTarget][targetsIdentified] = j;
                            // Calculate average co-ordinates of the centre
                            indexSmallestTarget = indexSecondSmallestTarget = indexThirdSmallestTarget = j;
                        }
                        numConcEllipseTarget++;
                        edgeBelongsTarget[i] = targetsIdentified;
                        concentricEllipses[numConcEllipseTarget][targetsIdentified] = i;
                        


                        if (paramEdgesEllipses.get(i).getA() < paramEdgesEllipses.get(indexThirdSmallestTarget).getA()) {
                        	
                            
                            
                            if (paramEdgesEllipses.get(i).getA() < paramEdgesEllipses.get(indexSecondSmallestTarget).getA()) {
                                  	
                            	
                                      
                            	if ( paramEdgesEllipses.get(i).getA() < paramEdgesEllipses.get(indexSmallestTarget).getA()) {
                                    
                            		 indexThirdSmallestTarget = indexSecondSmallestTarget;
                            		 indexSecondSmallestTarget = indexSmallestTarget;
                                     indexSmallestTarget = i;
                                     
                            	} else {
                            		indexThirdSmallestTarget = indexSecondSmallestTarget;
                            		indexSecondSmallestTarget = i;
                            	}
        
                            } else {
                            	indexThirdSmallestTarget = i;
                            }
                        }                     
                       
                        
                        
                        
                     
                    } // if < PROXIMITY_THRESHOLD
                } // for i = current numEdges to numEdges

             
                if (edgeBelongsTarget[j] != -1) {
                    numConcEllipseTarget++;
//	Why was this needed?
//                    paramEdgesEllipses.get(indexSmallestTarget).setX2(
//                    		paramEdgesEllipses.get(indexThirdSmallestTarget).getX()
//                    		);
//                    
//                    paramEdgesEllipses.get(indexSmallestTarget).setY2(
//                    		paramEdgesEllipses.get(indexThirdSmallestTarget).getY()
//                        	);
//                    
//                    paramEdgesEllipses.get(indexSecondSmallestTarget).setX2(
//                    		paramEdgesEllipses.get(indexThirdSmallestTarget).getX()
//                    		);
//                    
//                    paramEdgesEllipses.get(indexSecondSmallestTarget).setY2(
//                    		paramEdgesEllipses.get(indexThirdSmallestTarget).getY()
//                        	);
//                     
                    paramMaxConcEllipses.add(paramEdgesEllipses.get(
                    		indexThirdSmallestTarget)
                    		);
//                    param2ndBigConcEllipses.add(paramEdgesEllipses.get(
//                        indexSecondBiggestEllipseTarget));
                    System.out.println("Smallest: "+indexSmallestTarget);
                    System.out.println("2ndSmallest: "+indexSecondSmallestTarget);
                    System.out.println("3rdSmallest: "+indexThirdSmallestTarget);
                    
                    targetsIdentified++;
                }
                
                
            } // if !edgeBelongsTarget
        } // for numEdges

        
        System.out.println(paramMaxConcEllipses.size());
        
        //BEGIN IMAGE
//        if (paramMaxConcEllipses.size() > 0) {
//	    	BufferedImage b = new BufferedImage(imageDataPack.getWidth(), imageDataPack.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
//	    	Graphics2D g = b.createGraphics();
//	    	g.drawImage(imageDataPack.getEdgeTrackedImageBI(), 0, 0, null);
//	    	Ellipse2D.Float ellipse = new Ellipse2D.Float();
//	    	
//	    	EllipseParams x = paramMaxConcEllipses.get(paramMaxConcEllipses.size()-1);
//	    	
//	    	g.setColor(Color.WHITE);
//        	ellipse.setFrameFromCenter(
//        			0,0,
//        			x.getB(),
//        			x.getA()
//        			);
//        	g.translate(
//        			x.getY(),
//        			x.getX()
//        			);
//        	g.rotate(-x.getAlpha());
//        	g.draw(ellipse);
//	    	
//	    	g.dispose();
//	    	try { ImageIO.write(b, "PNG", new FileOutputStream("debugrun/findConcentricEllipses(final).png")); } catch (FileNotFoundException e) { } catch (Exception e) {}
//        }
    	//END IMAGE

        return paramMaxConcEllipses;
	} // method

} // class
