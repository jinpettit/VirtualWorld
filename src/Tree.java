import processing.core.PImage;

import java.util.*;

public class Tree extends TreeEntity {

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

    protected boolean _transformHelper(WorldModel world,
                                       EventScheduler scheduler,
                                       ImageStore imageStore) {
        return false;
    }
}

