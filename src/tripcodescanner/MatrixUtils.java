package tripcodescanner;


public class MatrixUtils {

    public static void multMatrix(double m[][], double g[][], double mg[][]) {
    // This function performs the meat of the calculations for the
    // curve plotting.  Note that it is not a matrix multiplier in the
    // pure sense.  The first matrix is the curve matrix (each curve type
    // has its own matrix), and the second matrix is the geometry matrix
    // (defined by the control points).  The result is returned in the
    // third matrix.

	// First clear the return array
	for(int i=0; i<4; i++)
	    for(int j=0; j<2; j++)
		mg[i][j]=0;

	// Perform the matrix math
	for(int i=0; i<4; i++)
	    for(int j=0; j<2; j++)
		for(int k=0; k<4; k++)
		    mg[i][j]=mg[i][j] + (m[i][k] * g[k][j]);
    }


    public static void ROTATE(double a[][], int i, int j, int k, int l, double tau, double s) {
	double g,h;
	g=a[i][j];h=a[k][l];a[i][j]=g-s*(h+g*tau);
	a[k][l]=h+s*(g-h*tau);
      }

    public static void jacobi(double a[][], int n, double d[] , double v[][], int nrot)
      {
	int j,iq,ip,i;
	double tresh,theta,tau,t,sm,s,h,g,c;

	double b[] = new double[n+1];
	double z[] = new double[n+1];

	for (ip=1;ip<=n;ip++) {
	  for (iq=1;iq<=n;iq++) v[ip][iq]=0.0;
	  v[ip][ip]=1.0;
	}
	for (ip=1;ip<=n;ip++) {
	  b[ip]=d[ip]=a[ip][ip];
	  z[ip]=0.0;
	}
	nrot=0;
	for (i=1;i<=50;i++) {
	  sm=0.0;
  	  for (ip=1;ip<=n-1;ip++) {
	    for (iq=ip+1;iq<=n;iq++)
	      sm += Math.abs(a[ip][iq]);
	  }
	  if (sm == 0.0) {
	    /*    free_vector(z,1,n);
		  free_vector(b,1,n);  */
	    return;
	  }
	  if (i < 4)
	    tresh=0.2*sm/(n*n);
	  else
	    tresh=0.0;
	  for (ip=1;ip<=n-1;ip++) {
	    for (iq=ip+1;iq<=n;iq++) {
	      g=100.0*Math.abs(a[ip][iq]);
	      if (i > 4 && Math.abs(d[ip])+g == Math.abs(d[ip])
		  && Math.abs(d[iq])+g == Math.abs(d[iq]))
		a[ip][iq]=0.0;
	      else if (Math.abs(a[ip][iq]) > tresh) {
		h=d[iq]-d[ip];
		if (Math.abs(h)+g == Math.abs(h))
		  t=(a[ip][iq])/h;
		else {
		  theta=0.5*h/(a[ip][iq]);
		  t=1.0/(Math.abs(theta)+Math.sqrt(1.0+theta*theta));
		  if (theta < 0.0) t = -t;
		}
		c=1.0/Math.sqrt(1+t*t);
		s=t*c;
		tau=s/(1.0+c);
		h=t*a[ip][iq];
		z[ip] -= h;
		z[iq] += h;
		d[ip] -= h;
		d[iq] += h;
		a[ip][iq]=0.0;
		for (j=1;j<=ip-1;j++) {
		  ROTATE(a,j,ip,j,iq,tau,s);
		  }
		for (j=ip+1;j<=iq-1;j++) {
		  ROTATE(a,ip,j,j,iq,tau,s);
		  }
		for (j=iq+1;j<=n;j++) {
		  ROTATE(a,ip,j,iq,j,tau,s);
		  }
		for (j=1;j<=n;j++) {
		  ROTATE(v,j,ip,j,iq,tau,s);
		  }
		++nrot;
	      }
	    }
	  }
	  for (ip=1;ip<=n;ip++) {
	    b[ip] += z[ip];
	    d[ip]=b[ip];
	    z[ip]=0.0;
	  }
	}
	//printf("Too many iterations in routine JACOBI");
      }


    //  Perform the Cholesky decomposition
    // Return the lower triangular L  such that L*L'=A
    public static void choldc(double a[][], int n, double l[][])
      {
	int i,j,k;
	double sum;
	double p[] = new double[n+1];

	for (i=1; i<=n; i++)  {
	  for (j=i; j<=n; j++)  {
	    for (sum=a[i][j],k=i-1;k>=1;k--) sum -= a[i][k]*a[j][k];
	    if (i == j) {
	      if (sum<=0.0)
		// printf("\nA is not poitive definite!");
		{}
	      else
		p[i]=Math.sqrt(sum); }
	    else
	      {
		a[j][i]=sum/p[i];
	      }
	  }
	}
	for (i=1; i<=n; i++)
	  for (j=i; j<=n; j++)
	    if (i==j)
	      l[i][i] = p[i];
	    else
	      {
		l[j][i]=a[j][i];
		l[i][j]=0.0;
	      }
      }


    /********************************************************************/
    /**    Calcola la inversa della matrice  B mettendo il risultato   **/
    /**    in InvB . Il metodo usato per l'inversione e' quello di     **/
    /**    Gauss-Jordan.   N e' l'ordine della matrice .               **/
    /**    ritorna 0 se l'inversione  corretta altrimenti ritorna     **/
    /**    SINGULAR .                                                  **/
    /********************************************************************/
    public static int inverse(double TB[][], double InvB[][], int N) {
      int k,i,j,p,q;
      double mult;
      double D,temp;
      double maxpivot;
      int npivot;
      double B[][] = new double [N+1][N+2];
      double A[][] = new double [N+1][2*N+2];
//      double C[][] = new double [N+1][N+1];
      double eps = 10e-20;


      for(k=1;k<=N;k++)
	for(j=1;j<=N;j++)
	  B[k][j]=TB[k][j];

      for (k=1;k<=N;k++)
	{
	  for (j=1;j<=N+1;j++)
	    A[k][j]=B[k][j];
	  for (j=N+2;j<=2*N+1;j++)
	    A[k][j]=0;
	  A[k][k-1+N+2]=1;
	}
      for (k=1;k<=N;k++)
	{
	  maxpivot=Math.abs(A[k][k]);
	  npivot=k;
	  for (i=k;i<=N;i++)
	    if (maxpivot<Math.abs(A[i][k]))
	      {
		maxpivot=Math.abs(A[i][k]);
		npivot=i;
	      }
	  if (maxpivot>=eps)
	    {      if (npivot!=k)
		     for (j=k;j<=2*N+1;j++)
		       {
			 temp=A[npivot][j];
			 A[npivot][j]=A[k][j];
			 A[k][j]=temp;
		       } ;
		   D=A[k][k];
		   for (j=2*N+1;j>=k;j--)
		     A[k][j]=A[k][j]/D;
		   for (i=1;i<=N;i++)
		     {
		       if (i!=k)
			 {
			   mult=A[i][k];
			   for (j=2*N+1;j>=k;j--)
			     A[i][j]=A[i][j]-mult*A[k][j] ;
			 }
		     }
		 }
	  else
	    {  // printf("\n The matrix may be singular !!") ;
	       return(-1);
	     };
	}
      /**   Copia il risultato nella matrice InvB  ***/
      for (k=1,p=1;k<=N;k++,p++)
	for (j=N+2,q=1;j<=2*N+1;j++,q++)
	  InvB[p][q]=A[k][j];
      return(0);
    }            /*  End of INVERSE   */



    public static void AperB(double _A[][], double _B[][], double _res[][],
		              int _righA, int _colA, int _righB, int _colB) {
      int p,q,l;
      for (p=1;p<=_righA;p++)
	for (q=1;q<=_colB;q++)
	  { _res[p][q]=0.0;
	    for (l=1;l<=_colA;l++)
	      _res[p][q]=_res[p][q]+_A[p][l]*_B[l][q];
	  }
    }

    public static void A_TperB(double _A[][], double  _B[][], double _res[][],
			       int _righA, int _colA, int _righB, int _colB) {
      int p,q,l;
      for (p=1;p<=_colA;p++)
	for (q=1;q<=_colB;q++)
	  { _res[p][q]=0.0;
	    for (l=1;l<=_righA;l++)
	      _res[p][q]=_res[p][q]+_A[l][p]*_B[l][q];
	  }
    }

    public static void AperB_T(double _A[][], double _B[][], double _res[][],
			 int _righA, int _colA, int _righB, int _colB) {
      int p,q,l;
      for (p=1;p<=_colA;p++)
	for (q=1;q<=_colB;q++)
	  { _res[p][q]=0.0;
	    for (l=1;l<=_righA;l++)
	      _res[p][q]=_res[p][q]+_A[p][l]*_B[q][l];
	  }
    }
}
