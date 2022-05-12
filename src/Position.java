import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public abstract class Position extends ActionEntity {
    public Position(String id, Point position, List<PImage> images, int animationPeriod, int actionPeriod) {
        super(id, position, images, animationPeriod, actionPeriod);
    }

    public Point nextPosition(
            WorldModel world, Point destPos) {
        int horiz = Integer.signum(destPos.x - getPosition().x);
        Point newPos = new Point(getPosition().x + horiz, getPosition().y);

        if (horiz == 0 || world.isOccupied(newPos) && _nextPositionHelper(world, destPos)) {
            int vert = Integer.signum(destPos.y - getPosition().y);
            newPos = new Point(getPosition().x, getPosition().y + vert);

            if (vert == 0 || world.isOccupied(newPos) && _nextPositionHelper(world, destPos)) {
                newPos = getPosition();
            }
        }

        return newPos;
    }

    public boolean moveTo(
            WorldModel world,
            Entity target,
            EventScheduler scheduler) {
        if (getPosition().adjacent(target.getPosition())) {
            return _moveToHelper(world, target, scheduler);
        }
        else {
            Point nextPos = this.nextPosition(world, target.getPosition());

            if (!getPosition().equals(nextPos)) {
                Optional<Entity> occupant = world.getOccupant(nextPos);
                if (occupant.isPresent()) {
                    scheduler.unscheduleAllEvents(occupant.get());
                }

                world.moveEntity(this, nextPos);
            }
            return false;
        }
    }

    protected abstract boolean _moveToHelper(WorldModel world, Entity target, EventScheduler scheduler);

    protected abstract boolean _nextPositionHelper(WorldModel world, Point destPos);
}
