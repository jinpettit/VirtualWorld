import java.util.*;

import processing.core.PImage;

public class Dude_Full extends Position{
        private final int resourceLimit;

        public Dude_Full(
                String id,
                Point position,
                List<PImage> images,
                int resourceLimit,
                int animationPeriod,
                int actionPeriod)
        {
            super(id, position, images, animationPeriod, actionPeriod);
            this.resourceLimit = resourceLimit;
        }

        public void executeActivity(
                WorldModel world,
                ImageStore imageStore,
                EventScheduler scheduler)
        {
            Optional<Entity> fullTarget =
                    world.findNearest(getPosition(), new ArrayList<>(Arrays.asList(House.class)));

            if (fullTarget.isPresent() && moveTo(world,
                    fullTarget.get(), scheduler))
            {
                transformFull(world, scheduler, imageStore);
            }
            else {
                scheduler.scheduleEvent(this,
                        Factory.createActivityAction(this,world, imageStore),
                        getActionPeriod());
            }
        }


        private void transformFull(
                WorldModel world,
                EventScheduler scheduler,
                ImageStore imageStore)
        {
            AnimationEntity miner = Factory.createDudeNotFull(getId(),
                    getPosition(), getActionPeriod(),
                    getAnimationPeriod(),
                    this.resourceLimit,
                    getImages());

            world.removeEntity(this);
            scheduler.unscheduleAllEvents(this);

            world.addEntity(miner);
            miner.scheduleActions(scheduler, world, imageStore);
        }

        public Point nextPosition(
                WorldModel world, Point destPos)
        {
            int horiz = Integer.signum(destPos.x - getPosition().x);
            Point newPos = new Point(getPosition().x + horiz, getPosition().y);

            if (horiz == 0 || world.isOccupied(newPos) && world.getOccupancyCell(newPos).getClass() != Stump.class) {
                int vert = Integer.signum(destPos.y - getPosition().y);
                newPos = new Point(getPosition().x, getPosition().y + vert);

                if (vert == 0 || world.isOccupied(newPos) && world.getOccupancyCell(newPos).getClass() != Stump.class) {
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

