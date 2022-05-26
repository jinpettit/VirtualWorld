import processing.core.PImage;

import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public abstract class Position extends ActionEntity {
    public Position(String id, Point position, List<PImage> images, int animationPeriod, int actionPeriod) {
        super(id, position, images, animationPeriod, actionPeriod);
    }

    public Point nextPosition(
           WorldModel world, Point destPos) {

        PathingStrategy strategy = new AStarPathingStrategy();
        Predicate<Point> canPassThrough = p -> ((world.withinBounds(p)) && (!(world.isOccupied(p)) || !_nextPositionHelper(world, p)));
        BiPredicate<Point, Point> withinReach = (p1, p2) -> (p1.adjacent(p2));
        List<Point> list = strategy.computePath(getPosition(), destPos, canPassThrough, withinReach, PathingStrategy.CARDINAL_NEIGHBORS);

        if (list.size() == 0) {
            return this.getPosition();
        }

        return list.get(0);
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
