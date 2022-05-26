import processing.core.PImage;

import java.util.*;

public class Dude_Not_Full extends Dude {
    private int resourceCount;

    public Dude_Not_Full(
            String id,
            Point position,
            List<PImage> images,
            int animationPeriod,
            int actionPeriod,
            int resourceLimit,
            int resourceCount) {
        super(id, position, images, animationPeriod, actionPeriod, resourceLimit);
        this.resourceCount = resourceCount;
    }

    public void executeActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler) {
        Optional<Entity> target =
                world.findNearest(getPosition(), new ArrayList<>(Arrays.asList(Tree.class, Sapling.class)));

        if (!target.isPresent() || !moveTo(world,
                target.get(),
                scheduler)
                || !transform(world, scheduler, imageStore)) {
            scheduler.scheduleEvent(this,
                    Factory.createActivityAction(this, world, imageStore),
                    getActionPeriod());
        }
    }

    public boolean transform(
            WorldModel world,
            EventScheduler scheduler,
            ImageStore imageStore) {
            if (this.resourceCount >= getResourceLimit()) {
                AnimationEntity miner = Factory.createDudeFull(getId(),
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
            return false;
        }

    protected boolean _moveToHelper(WorldModel world, Entity target, EventScheduler scheduler) {
        this.resourceCount += 1;
        if (target instanceof TreeEntity) {
            ((TreeEntity) target).setHealth(((TreeEntity) target).getHealth() - 1);
            return true;
        } else
            return false;
    }

    protected boolean _nextPositionHelper(WorldModel world, Point destPos) {
        if (world.getOccupancyCell(destPos) == null)
            return true;
        return world.getOccupancyCell(destPos).getClass() != Stump.class;
    }


}
