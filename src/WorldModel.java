import java.util.*;

/**
 * Represents the 2D World in which this simulation is running.
 * Keeps track of the size of the world, the background image for each
 * location in the world, and the entities that populate the world.
 */
public final class WorldModel
{
    public int numRows;
    public int numCols;
    public Background background[][];
    public Entity occupancy[][];
    public Set<Entity> entities;

    public WorldModel(int numRows, int numCols, Background defaultBackground) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.background = new Background[numRows][numCols];
        this.occupancy = new Entity[numRows][numCols];
        this.entities = new HashSet<>();

        for (int row = 0; row < numRows; row++) {
            Arrays.fill(this.background[row], defaultBackground);
        }
    }
    public boolean withinBounds(Point pos) {
        return pos.y >= 0 && pos.y < this.numRows && pos.x >= 0
                && pos.x < this.numCols;
    }
    public void addEntity(Entity entity) {
        if (withinBounds(entity.position)) {
            Functions.setOccupancyCell(this, entity.position, entity);
            entities.add(entity);
        }
    }

    public void tryAddEntity(Entity entity) {
        if (Functions.isOccupied(this, entity.position)) {
            // arguably the wrong type of exception, but we are not
            // defining our own exceptions yet
            throw new IllegalArgumentException("position occupied");
        }

        this.addEntity(entity);
    }
    public Optional<Entity> nearestEntity(
            List<Entity> entities, Point pos)
    {
        if (entities.isEmpty()) {
            return Optional.empty();
        }
        else {
            Entity nearest = entities.get(0);
            int nearestDistance = Functions.distanceSquared(nearest.position, pos);

            for (Entity other : entities) {
                int otherDistance = Functions.distanceSquared(other.position, pos);

                if (otherDistance < nearestDistance) {
                    nearest = other;
                    nearestDistance = otherDistance;
                }
            }

            return Optional.of(nearest);
        }
    }
    public Optional<Entity> findNearest(
             Point pos, List<EntityKind> kinds)
    {
        List<Entity> ofType = new LinkedList<>();
        for (EntityKind kind: kinds)
        {
            for (Entity entity : this.entities) {
                if (entity.kind == kind) {
                    ofType.add(entity);
                }
            }
        }

        return this.nearestEntity(ofType, pos);
    }

        /*
       Assumes that there is no entity currently occupying the
       intended destination cell.
    */

    public void moveEntity(Entity entity, Point pos) {
        Point oldPos = entity.position;
        if (this.withinBounds(pos) && !pos.equals(oldPos)) {
            Functions.setOccupancyCell(this, oldPos, null);
            removeEntityAt(pos);
            Functions.setOccupancyCell(this, pos, entity);
            entity.position = pos;
        }
    }

    public void removeEntity(Entity entity) {
        removeEntityAt(entity.position);
    }

    public void removeEntityAt(Point pos) {
        if (this.withinBounds(pos) && Functions.getOccupancyCell(this, pos) != null) {
            Entity entity = Functions.getOccupancyCell(this, pos);

            /* This moves the entity just outside of the grid for
             * debugging purposes. */
            entity.position = new Point(-1, -1);
            this.entities.remove(entity);
            Functions.setOccupancyCell(this, pos, null);
        }
    }
}
