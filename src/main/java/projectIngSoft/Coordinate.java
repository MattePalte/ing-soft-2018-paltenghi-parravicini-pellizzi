package projectIngSoft;

public final class Coordinate {
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

}
