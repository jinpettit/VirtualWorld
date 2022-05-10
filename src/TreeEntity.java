import processing.core.PImage;

import java.util.List;

public abstract class TreeEntity extends ActionEntity {
    private int health;

    public TreeEntity(String id, Point position, List<PImage> images, int animationPeriod, int actionPeriod, int health) {
        super(id, position, images, animationPeriod, actionPeriod);
        this.health = health;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public boolean transformPlant(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (getHealth() <= 0) {
            Entity stump = Factory.createStump(getId(),
                    getPosition(),
                    imageStore.getImageList(Parse.STUMP_KEY));

            world.removeEntity(this);
            scheduler.unscheduleAllEvents(this);

            world.addEntity(stump);

            return true;
        }
        return _transformHelper(world, scheduler, imageStore);
    }

    protected abstract boolean _transformHelper(WorldModel world, EventScheduler scheduler, ImageStore imageStore);
}
