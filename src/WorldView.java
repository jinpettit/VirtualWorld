import processing.core.PApplet;

public final class WorldView
{
    public PApplet screen;
    public WorldModel world;
    public int tileWidth;
    public int tileHeight;
    public Viewport viewport;

    public WorldView(
            int numRows,
            int numCols,
            PApplet screen,
            WorldModel world,
            int tileWidth,
            int tileHeight)
    {
        this.screen = screen;
        this.world = world;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.viewport = new Viewport(numRows, numCols);
    }

    public void shiftView(int colDelta, int rowDelta) {
        int newCol = Functions.clamp(this.viewport.col + colDelta, 0,
                this.world.numCols - this.viewport.numCols);
        int newRow = Functions.clamp(this.viewport.row + rowDelta, 0,
                this.world.numRows - this.viewport.numRows);

        this.viewport.shift(newCol, newRow);
    }
}
