import processing.core.PImage;

import java.util.*;

public class Tree {
    private final String id;
    private Point position;
    private final List<PImage> images;
    private int imageIndex;
    private final int actionPeriod;
    private final int animationPeriod;
    private int health;
    private final int healthLimit;

    private static final Random rand = new Random();

    public Tree(
            String id,
            Point position,
            List<PImage> images,
            int actionPeriod,
            int animationPeriod,
            int health,
            int healthLimit) {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
        this.health = health;
        this.healthLimit = healthLimit;
    }

    public String getId() {
        return id;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public int getHealth() {
        return health;
    }


    public void executeTreeActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler) {

        if (!transformPlant(world, scheduler, imageStore)) {

            scheduler.scheduleEvent((Entity)this,
                    Functions.createActivityAction((Entity)this, world, imageStore),
                    this.actionPeriod);
        }

    }

    public void scheduleActions(
            EventScheduler scheduler,
            WorldModel world,
            ImageStore imageStore) {
        scheduler.scheduleEvent((Entity)this,
                Functions.createActivityAction((Entity) this, world, imageStore),
                this.actionPeriod);
        scheduler.scheduleEvent((Entity)this,
                Functions.createAnimationAction((Entity)this, 0),
                getAnimationPeriod());

    }

    public PImage getCurrentImage() {
        return (images.get(imageIndex));
    }

    public int getAnimationPeriod() {
        return this.animationPeriod;
    }

    public void nextImage() {
        this.imageIndex = (this.imageIndex + 1) % this.images.size();
    }

    private boolean transformPlant(WorldModel world,
                                   EventScheduler scheduler,
                                   ImageStore imageStore) {
        return transformTree(world, scheduler, imageStore);
    }

    private boolean transformTree(
            WorldModel world,
            EventScheduler scheduler,
            ImageStore imageStore) {
        if (this.health <= 0) {
            Entity stump = Functions.createStump(this.id,
                    this.position,
                    imageStore.getImageList(Functions.STUMP_KEY));

            world.removeEntity((Entity) this);
            scheduler.unscheduleAllEvents((Entity) this);

            world.addEntity(stump);
            ((Tree)stump).scheduleActions(scheduler, world, imageStore);

            return true;
        }

        return false;
    }
}

