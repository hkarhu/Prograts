package tripcodescanner;


import java.util.ArrayList;

public class AlgoEllipseFitting {

	public static EllipseParams ellipseFitting(ArrayList<Edgel> edgeToFit) {
		int numPoints = edgeToFit.size();
        double D[][] = new double[numPoints + 1][7];
        double S[][] = new double[7][7];
        double Const[][] = new double[7][7];
        double temp[][] = new double[7][7];
        double L[][] = new double[7][7];
        double C[][] = new double[7][7];

        double invL[][] = new double[7][7];
        double d[] = new double[7];
        double V[][] = new double[7][7];
        double sol[][] = new double[7][7];
        double tx, ty;
        int nrot = 0;
        //int npts=50;

        //double XY[][] = new double[3][numPointsts+1];
        double pvec[] = new double[7];

        double lambda_1, lambda_2; // eigenvalues for parameterisation system
        double lump_1, lump_2; // two parts of quadratic equation

        // We need at least six point to deduce an ellipse
        if (numPoints < 6) {
            return null;
        }

        // 1.- Build the Design Matrix of size (NUM_POINTS*6)
        for (int i = 0; i < numPoints; i++) {
            tx = edgeToFit.get(i).getCoordX();
            ty = edgeToFit.get(i).getCoordY();

            D[i + 1][1] = tx * tx;
            D[i + 1][2] = tx * ty;
            D[i + 1][3] = ty * ty;
            D[i + 1][4] = tx;
            D[i + 1][5] = ty;
            D[i + 1][6] = 1.0;
        }

        // 2.- Build the scatter matrix, S = transpose(X)*X, being X the Design matrix
        MatrixUtils.A_TperB(D, D, S, numPoints, 6, numPoints, 6);

        // 3.- Build the Constraint Matrix
        Const[1][3] = -2;
        Const[2][2] = 1;
        Const[3][1] = -2;

        /* a)
           Apply Cholenski Method for Symmetric Matrix Descomposition
           to the symmetric matrix S
           S = L * transpose(L),
           Example:
           | 16  4  8 |   | 4      |   | 4  1  2 |
           |  4  5 -4 | = | 1  2   | * |    2 -3 |
           |  8 -4 22 |   | 2 -3 3 |   |       3 |
         */
        MatrixUtils.choldc(S, 6, L);
        // b) Calculate the inverse of L
        MatrixUtils.inverse(L, invL, 6);

        // Calculates the inv(S) * Const
        MatrixUtils.AperB_T(Const, invL, temp, 6, 6, 6, 6);
        MatrixUtils.AperB(invL, temp, C, 6, 6, 6, 6);

        // Obtain the Eigenvalues and EigenVectors of Matrix C
        MatrixUtils.jacobi(C, 6, d, V, nrot);
        // storing EigenVectors in V and EigenValues in d

        MatrixUtils.A_TperB(invL, V, sol, 6, 6, 6, 6);

        // Now normalize them
        for (int j = 1; j <= 6; j++) /* Scan columns */ {
            double mod = 0.0;
            for (int i = 1; i <= 6; i++) {
                mod += sol[i][j] * sol[i][j];
            }
            for (int i = 1; i <= 6; i++) {
                sol[i][j] /= Math.sqrt(mod);
            }
        }

        double zero = 10e-20;
        int solind = 0;
        // 5.- Determine the only negative eigenvalue
        for (int i = 1; i <= 6; i++) {
            if (d[i] < 0 && Math.abs(d[i]) > zero) {
                solind = i;
            }
        }

        // Now fetch the right solution and store it into pvec
        for (int j = 1; j <= 6; j++) {
            pvec[j] = sol[j][solind];
        }

        // Store the coefficients of the conic
        EllipseParams params = new EllipseParams();
        params.setA1(pvec[1]);
        params.setB1(pvec[2]);
        params.setC1(pvec[3]);
        params.setD1(pvec[4]);
        params.setE1(pvec[5]);
        params.setF1(pvec[6]);

        // Find the parameters of the ellipse
        // Swap x and y in the parameters calculated
        params.setY( ( -pvec[4] * pvec[2] + 2 * pvec[1] * pvec[5]) /
                    (pvec[2] * pvec[2] - 4 * pvec[1] * pvec[3]));
        params.setX( -pvec[2] * (pvec[4] + pvec[2] * params.getY()) /
                    (2 * pvec[1] * pvec[2]));

        pvec[6] = pvec[6] - pvec[1] * params.getX() * params.getX() -
            pvec[3] * params.getY() * params.getY() -
            pvec[2] * params.getX() * params.getY();

        lump_1 = pvec[1] + pvec[3];
        lump_2 = Math.sqrt(pvec[1] * pvec[1] + pvec[2] * pvec[2] +
                           pvec[3] * pvec[3] - 2 * pvec[1] * pvec[3]);
        lambda_1 = 0.5 * (lump_1 + lump_2);
        lambda_2 = 0.5 * (lump_1 - lump_2);

        params.setA(Math.sqrt( -pvec[6] / lambda_1));
        params.setB(Math.sqrt( -pvec[6] / lambda_2));

        params.setAlpha(Math.atan(2 * (lambda_1 - pvec[1]) / pvec[2]));

        return params;
	} // method

} // class
