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

        public Point nextPosition(
                WorldModel world, Point destPos)
        {
            int horiz = Integer.signum(destPos.x - getPosition().x);
            Point newPos = new Point(getPosition().x + horiz, getPosition().y);

            if (horiz == 0 || world.isOccupied(newPos)) {
                int vert = Integer.signum(destPos.y - getPosition().y);
                newPos = new Point(getPosition().x, getPosition().y + vert);

                if (vert == 0 || world.isOccupied(newPos)) {
                    newPos = getPosition();
                }
            }

            return newPos;
        }

        public boolean moveTo(
                WorldModel world,
                Entity target,
                EventScheduler scheduler)
        {
            if (getPosition().adjacent(target.getPosition())) {
                world.removeEntity(target);
                scheduler.unscheduleAllEvents(target);
                return true;
            }
            else {
                Point nextPos = this.nextPosition(world, target.getPosition());

                if (!getPosition().equals(nextPos)) {
                    Optional<Entity> occupant = world.getOccupant(nextPos);
                    if (occupant.isPresent()) {
                        scheduler.unscheduleAllEvents(occupant.get());
                    }

                    world.moveEntity(this, nextPos);
                }
                return false;
            }
        }

}
