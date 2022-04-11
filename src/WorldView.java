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
        int newCol = Functions.clamp(viewport.col + colDelta, 0,
                world.numCols - viewport.numCols);
        int newRow = Functions.clamp(viewport.row + rowDelta, 0,
                world.numRows - viewport.numRows);

        viewport.shift(newCol, newRow);
    }
}
