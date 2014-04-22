package coderats.ar;
import ae.routines.S;

public class EllipseParams {
    private double x;
    private double y;
    private double a;
    private double b;
    private double alpha;
    private double x2;
    private double y2;

    private double a1;
    private double b1;
    private double c1;
    private double d1;
    private double e1;
    private double f1;
    
    @Override
    public String toString() {
    	return S.sprintf(
    			"x:%f, y:%f, a:%f, b:%f, alpha:%f, x2:%f, y2:%f, a1:%f, b1:%f, c1:%f, d1:%f, e1:%f, f1:%f",
    			x,y,a,b,alpha,x2,y2,a1,b1,c1,d1,e1,f1
    			);
    }

    public void setX(double x) {
        this.x = x;
//    	System.err.println("setX(): " + x);
//    	new Exception().printStackTrace();
    }

    public double getX() {
        return this.x;
    }

    public void setX2(double x2) {
        this.x2 = x2;
    }

    public double getX2() {
        return this.x2;
    }

    public void setY(double y) {
        this.y = y;
}

    public double getY() {
        return this.y;
    }

    public void setY2(double y2) {
        this.y2 = y2;
    }

    public double getY2() {
        return this.y2;
    }

    public void setA(double a) {
        this.a = a;
    }

    public double getA() {
        return this.a;
    }

    public void setB(double b) {
        this.b = b;
    }

    public double getB() {
        return this.b;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public double getAlpha() {
        return this.alpha;
    }

    public void setA1(double a1) {
        this.a1 = a1;
    }

    public double getA1() {
        return this.a1;
    }

    public void setB1(double b1) {
        this.b1 = b1;
    }

    public double getB1() {
        return this.b1;
    }

    public void setC1(double c1) {
        this.c1 = c1;
    }

    public double getC1() {
        return this.c1;
    }

    public void setD1(double d1) {
        this.d1 = d1;
    }

    public double getD1() {
        return this.d1;
    }

    public void setE1(double e1) {
        this.e1 = e1;
    }

    public double getE1() {
        return this.e1;
    }

    public void setF1(double f1) {
        this.f1 = f1;
    }

    public double getF1() {
        return this.f1;
    }
}

