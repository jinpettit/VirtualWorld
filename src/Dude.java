import processing.core.PImage;

import java.util.List;

public abstract class Dude extends Position {
    private final int resourceLimit;

    public Dude(String id, Point position, List<PImage> images, int animationPeriod, int actionPeriod, int resourceLimit) {
        super(id, position, images, animationPeriod, actionPeriod);
        this.resourceLimit = resourceLimit;
    }

    public int getResourceLimit() {
        return resourceLimit;
    }

    protected abstract boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore);
}
