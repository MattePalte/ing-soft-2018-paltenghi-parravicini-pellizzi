package project.ing.soft.model;

import java.io.Serializable;

/**
 * Representation as a couple of int values of a position in a matrix
 */
public final class Coordinate implements Serializable{
    private final int row;
    private final int col;

    /**
     * Coordinate default constructor
     * @param row index of a position in the matrix
     * @param col index of a position in the matrix
     */
    public Coordinate(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Coordinate producer. This creates a copy of the Coordinate object passed as parameter
     * @param aCoordinate the Coordinate to be copied
     */
    public Coordinate(Coordinate aCoordinate){
        this.row = aCoordinate.getRow();
        this.col = aCoordinate.getCol();
    }

    /**
     *
     * @return row index of the Coordinate
     */
    public int getRow() {
        return row;
    }

    /**
     *
     * @return col index of the Coordinate
     */
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

    /**
     *
     * @return a String representation of the Coordinate
     */
    @Override
    public String toString(){
        return "(" + row + ", " + col + ")";
    }

}
