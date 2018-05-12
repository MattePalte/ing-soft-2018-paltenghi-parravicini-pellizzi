package project.ing.soft.model;

import java.io.Serializable;

public final class Coordinate implements Serializable{
    private final int row;
    private final int col;

    public Coordinate(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public Coordinate(Coordinate aCoordinate){
        this.row = aCoordinate.getRow();
        this.col = aCoordinate.getCol();
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    @Override
    public String toString(){
        return "(" + row + ", " + col + ")";
    }

}
