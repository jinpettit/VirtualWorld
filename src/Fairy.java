import processing.core.PImage;

import java.util.*;

public class Fairy extends Position{

        public Fairy(
                String id,
                Point position,
                List<PImage> images,
                int actionPeriod,
                int animationPeriod)
        {
            super(id, position, images, actionPeriod, animationPeriod);
        }

        public void executeActivity(
                WorldModel world,
                ImageStore imageStore,
                EventScheduler scheduler)
        {
            Optional<Entity> fairyTarget =
                    world.findNearest(getPosition(), new ArrayList<>(Arrays.asList(Stump.class)));

            if (fairyTarget.isPresent()) {
                Point tgtPos = fairyTarget.get().getPosition();

                if (moveTo(world, fairyTarget.get(), scheduler)) {
                    AnimationEntity sapling = Factory.createSapling("sapling_" + getId(), tgtPos,
                            imageStore.getImageList(Parse.SAPLING_KEY));

                    world.addEntity(sapling);
                    sapling.scheduleActions(scheduler, world, imageStore);
                }
            }

            scheduler.scheduleEvent(this,
                    Factory.createActivityAction(this, world, imageStore),
                    getActionPeriod());
        }


    protected boolean _moveToHelper(WorldModel world, Entity target, EventScheduler scheduler){
        world.removeEntity(target);
        scheduler.unscheduleAllEvents(target);
        return true;
    }

    protected boolean _nextPositionHelper(WorldModel world, Point destPos) {
            return true;
    }
}
