import processing.core.PImage;

import java.util.List;

public abstract class TreeEntity extends ActionEntity{
    private int health;

    public TreeEntity(String id, Point position, List<PImage> images, int animationPeriod, int actionPeriod, int health) {
        super(id, position, images, animationPeriod, actionPeriod);
        this.health = health;
    }

    public int getHealth() {
        return health;
    }
    abstract boolean transformPlant(WorldModel world, EventScheduler scheduler, ImageStore imageStore);
    public void setHealth(int health) {
        this.health = health;
    }
}
