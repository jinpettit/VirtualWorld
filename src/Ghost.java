
import processing.core.PImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Ghost extends Position{

    public Ghost(
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
            EventScheduler scheduler) {
        Optional<Entity> ghostTarget =
                world.findNearest(getPosition(), new ArrayList<>(Arrays.asList(Fairy.class)));


        if (ghostTarget.isPresent()) {
            if (moveTo(world, ghostTarget.get(), scheduler)) {

                world.removeEntity(ghostTarget.get());
                scheduler.unscheduleAllEvents(ghostTarget.get());
            }
        }
                scheduler.scheduleEvent(this,
                        Factory.createActivityAction(this, world, imageStore),
                        getActionPeriod());
        }

    protected boolean _moveToHelper(WorldModel world, Entity target, EventScheduler scheduler) {
        return true;
    }

    protected boolean _nextPositionHelper(WorldModel world, Point destPos) {
        return true;
    }

}


