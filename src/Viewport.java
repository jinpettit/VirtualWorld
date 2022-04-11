public final class Viewport
{
    public int row;
    public int col;
    public int numRows;
    public int numCols;

    public Viewport(int numRows, int numCols) {
        this.numRows = numRows;
        this.numCols = numCols;
    }

    public Point viewportToWorld(int col, int row) {
        return new Point(col + this.col, row + this.row);
    }

    public void shift(int col, int row) {
        this.col = col;
        this.row = row;
    }
}
