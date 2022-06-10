import processing.core.PImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


public class Knight extends Position {

    private static final int FAIRY_ANIMATION_PERIOD = 4;
    private static final int FAIRY_ACTION_PERIOD = 5;

    public Knight(
            String id,
            Point position,
            List<PImage> images,
            int animationPeriod,
            int actionPeriod) {
        super(id, position, images, animationPeriod, actionPeriod);
    }

    public void executeActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler) {
        Optional<Entity> knightTarget =
                world.findNearest(getPosition(), new ArrayList<>(Arrays.asList(Ghost.class)));

        if (knightTarget.isPresent()) {
            Point tgtPos = knightTarget.get().getPosition();
            if (moveTo(world, knightTarget.get(), scheduler)) {

                world.removeEntity(knightTarget.get());
                scheduler.unscheduleAllEvents(knightTarget.get());

                if (!world.isOccupied(tgtPos)) {
                    AnimationEntity fairy = Factory.createFairy("fairy" + getId(), tgtPos, FAIRY_ACTION_PERIOD, FAIRY_ANIMATION_PERIOD,
                            imageStore.getImageList(Parse.FAIRY_KEY));
                    world.addEntity(fairy);
                    fairy.scheduleActions(scheduler, world, imageStore);
                }
            }
        }
        scheduler.scheduleEvent(this,
                Factory.createActivityAction(this, world, imageStore),
                getActionPeriod());
    }

    protected boolean _moveToHelper(WorldModel world, Entity target, EventScheduler scheduler){
        return true;
    }

    protected boolean _nextPositionHelper(WorldModel world, Point destPos) {
        return world.getOccupancyCell(destPos).getClass() != Stump.class;
    }
}

