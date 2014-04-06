package tripcodescanner;
import java.util.ArrayList;


public class AlgoEdgeFollow {
	
	public static ArrayList<ArrayList<Edgel>> edgeFollowing(ImageDataPack imageDataPack) {
		
		final byte[] edgeTrackedImage = new byte[imageDataPack.getThresholdImage().length];
		final byte[] im = imageDataPack.getEdgeDetectedImage();
		final byte[] edgeDetectedNMSImage = imageDataPack.getEdgeDetectedNMSImage();
		final int imageWidth = imageDataPack.getWidth();
		final int imageHeight = imageDataPack.getHeight();

		ArrayList<ArrayList<Edgel>> edgesList = new ArrayList<ArrayList<Edgel>>();
        int limitRows = imageHeight - 2;
        int limitCols = imageWidth - 2;

        for (int i = 0; i < imageHeight; i++) {
            int iRow = i * imageWidth;
            for (int j = 0; j < imageWidth; j++) {
                edgeTrackedImage[iRow + j] = AlgorithmConstants.BLACK;
            }
        }

        for (int i = 2; i < limitRows; i++) {
            int iRow = i * imageWidth;
            for (int j = 2; j < limitCols; j++) {
                if ((im[iRow + j]&255) > AlgorithmConstants.HIGH_THRESHOLD) {
                    // begin edge follow 8-connected! non-branching and ordered
                    ArrayList<Edgel> currentEdge = new ArrayList<Edgel>();
                    // Insert the first edgel point
                    currentEdge.add(new Edgel(i, j));
                    // wipe out the initial pixel
                    edgeDetectedNMSImage[iRow + j] = AlgorithmConstants.BLACK;
                    AlgoTrackEdgels.trackEdgels(imageDataPack, currentEdge, i, j);
                    // Reverse Edgel List and follow in the other direction
                    ArrayList<Edgel> reversedEdge = new ArrayList<Edgel>(currentEdge.size());
                    for (int k = currentEdge.size() - 1; k >= 0; k--) {
                        reversedEdge.add(currentEdge.get(k));
                    }
                    currentEdge = reversedEdge;
                    // Last edgel in edgels is now the first that was inserted with values (i,j)
                    AlgoTrackEdgels.trackEdgels(imageDataPack, currentEdge, i, j);
                    // Once we arrive here we have in edgels all the edgels that form a new dge.
                    // Check if the edge has more than 10 points
                    int numEdgels = currentEdge.size();

                    // VALID EDGE FILTERING
                    // a) If the edges are formed by 10 or fewer pixels get rid of it
                    if (numEdgels > 10) {
                        // If the number of points in the edgel is minor than 30
                        // b) If the EdgeNumPixels / DistanceBetweenEdgeExtremes < 10 is an invalid edge
                        //    because we are looking for edges with elliptical shape that should form closed chains of edgels
                        int distanceExtremeEdgels = Math.abs( ( (Edgel)
                            currentEdge.get(0)).getCoordX() -
                            ( (Edgel) currentEdge.get(numEdgels - 1)).getCoordX()) +
                            Math.abs( ( (Edgel) currentEdge.get(0)).getCoordY() -
                                     ( (Edgel) currentEdge.get(numEdgels - 1)).
                                     getCoordY());

                        if (numEdgels > 30) {
                            if (distanceExtremeEdgels == 0 ||
                                (numEdgels / distanceExtremeEdgels) > 10) {
                                //if ( ((float)(numEdgels / DistanceExtremeEdgels)) > 5.0 ) {
                                edgesList.add(currentEdge);
                                for (int iEdgel = 0; iEdgel < numEdgels; iEdgel++)
                                    edgeTrackedImage[ ( (Edgel)
                                        currentEdge.get(iEdgel)).getCoordX() *
                                        imageWidth +
                                        ( (Edgel) currentEdge.get(iEdgel)).
                                        getCoordY()] = AlgorithmConstants.WHITE;
                            }
                        }
                        else {
                            // In here we are dealing we edges of sizes in the range (11-30)
                            if (distanceExtremeEdgels == 0 ||
                                (numEdgels / distanceExtremeEdgels) > 5) {
                                edgesList.add(currentEdge);
                                for (int iEdgel = 0; iEdgel < numEdgels; iEdgel++)
                                    edgeTrackedImage[ ( (Edgel)
                                        currentEdge.get(iEdgel)).getCoordX() *
                                        imageWidth +
                                        ( (Edgel) currentEdge.get(iEdgel)).
                                        getCoordY()] = AlgorithmConstants.WHITE;
                            }
                        }
                    } // end of if num > 10
                }
            }
        }
		imageDataPack.setEdgeTrackedImage(edgeTrackedImage);

        return edgesList;
		
    } // method

} // class
