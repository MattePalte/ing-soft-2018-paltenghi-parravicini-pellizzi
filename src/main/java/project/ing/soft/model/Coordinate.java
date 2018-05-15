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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Coordinate that = (Coordinate) o;

        if (row != that.row) return false;
        return col == that.col;
    }

    @Override
    public int hashCode() {
        int result = row;
        result = 31 * result + col;
        return result;
    }

    @Override
    public String toString(){
        return "(" + row + ", " + col + ")";
    }

}
