package coderats.ar;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */


public class Edgel {
    private int coordX;
    private int coordY;
    
    public Edgel() {
        this.coordX = 0;
        this.coordY = 0;
    }

    public Edgel(Edgel oldEdgel) {
        this.coordX = oldEdgel.coordX;
        this.coordY = oldEdgel.coordY;
    }

    public Edgel(int x, int y) {
        this.coordX = x;
        this.coordY = y;
    }

    public int getCoordX() {
        return this.coordX;
    }

    public int getCoordY() {
        return this.coordY;
    }

    public boolean equals(Edgel otherEdgel) {
        if (this.coordX == otherEdgel.getCoordX() &&
            this.coordY == otherEdgel.getCoordY()) {
            return true;
        } else {
            return false;
        }
    }

    public Edgel overwriteEdgel(Edgel otherEdgel) {
        if (this.equals(otherEdgel)) {
            return this;
        } else {
            this.coordX = otherEdgel.getCoordX();
            this.coordY = otherEdgel.getCoordY();
            return this;
        }
    }

    @Override
	public String toString() {
        return "[" + this.getCoordX() + " - " + this.getCoordY() + "]";
    }
}



