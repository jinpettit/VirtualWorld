import processing.core.PImage;

import java.util.*;

public class Tree extends TreeEntity{

    public Tree(
            String id,
            Point position,
            List<PImage> images,
            int actionPeriod,
            int animationPeriod,
            int health) {
        super(id, position, images, actionPeriod, animationPeriod, health);
    }

    public void executeActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler) {

        if (!transformPlant(world, scheduler, imageStore)) {

            scheduler.scheduleEvent(this,
                    Factory.createActivityAction(this, world, imageStore),
                    getActionPeriod());
        }

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

        return false;
    }

}

