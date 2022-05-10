import processing.core.PImage;

import java.util.List;

public abstract class Position extends ActionEntity {
    public Position(String id, Point position, List<PImage> images, int animationPeriod, int actionPeriod) {
        super(id, position, images, animationPeriod, actionPeriod);
    }

    abstract Point nextPosition(WorldModel world, Point destPos);
    abstract boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler);
}
