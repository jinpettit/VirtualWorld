import java.util.*;

import processing.core.PImage;

public class Dude_Full extends Dude {

    public Dude_Full(
            String id,
            Point position,
            List<PImage> images,
            int animationPeriod,
            int actionPeriod,
            int resourceLimit) {
        super(id, position, images, animationPeriod, actionPeriod, resourceLimit);
    }

    public void executeActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler) {
        Optional<Entity> fullTarget =
                world.findNearest(getPosition(), new ArrayList<>(Arrays.asList(House.class)));

        if (fullTarget.isPresent() && moveTo(world,
                fullTarget.get(), scheduler)) {
            transform(world, scheduler, imageStore);
        } else {
            scheduler.scheduleEvent(this,
                    Factory.createActivityAction(this, world, imageStore),
                    getActionPeriod());
        }
    }

    public boolean transform(
            WorldModel world,
            EventScheduler scheduler,
            ImageStore imageStore) {

        AnimationEntity miner = Factory.createDudeNotFull(getId(),
                getPosition(), getActionPeriod(),
                getAnimationPeriod(),
                getResourceLimit(),
                getImages());

        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);

        world.addEntity(miner);
        miner.scheduleActions(scheduler, world, imageStore);

        return true;
    }

    protected boolean _moveToHelper(WorldModel world, Entity target, EventScheduler scheduler) {
        return true;
    }

    protected boolean _nextPositionHelper(WorldModel world, Point destPos) {
        int horiz = Integer.signum(destPos.x - getPosition().x);
        Point newPos = new Point(getPosition().x + horiz, getPosition().y);

        return world.getOccupancyCell(newPos).getClass() != Stump.class;
    }

}


