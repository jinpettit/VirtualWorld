import processing.core.PImage;

import java.util.*;

public class Dude_Not_Full extends Position {
        private final int resourceLimit;
        private int resourceCount;

        public Dude_Not_Full(
                String id,
                Point position,
                List<PImage> images,
                int resourceLimit,
                int resourceCount,
                int actionPeriod,
                int animationPeriod)
        {
            super(id, position, images, actionPeriod, animationPeriod);
            this.resourceLimit = resourceLimit;
            this.resourceCount = resourceCount;
        }

        public void executeActivity(
                WorldModel world,
                ImageStore imageStore,
                EventScheduler scheduler)
        {
            Optional<Entity> target =
                    world.findNearest(getPosition(), new ArrayList<>(Arrays.asList(Tree.class, Sapling.class)));

            if (!target.isPresent() || !moveTo(world,
                    target.get(),
                    scheduler)
                    || !transformNotFull(world, scheduler, imageStore))
            {
                scheduler.scheduleEvent(this,
                        Factory.createActivityAction(this,world, imageStore),
                        getActionPeriod());
            }
        }

        private boolean transformNotFull(
                WorldModel world,
                EventScheduler scheduler,
                ImageStore imageStore)
        {
            if (this.resourceCount >= this.resourceLimit) {
                AnimationEntity miner = Factory.createDudeFull(getId(),
                        getPosition(), getActionPeriod(),
                        getAnimationPeriod(),
                        this.resourceLimit,
                        getImages());

                world.removeEntity(this);
                scheduler.unscheduleAllEvents(this);

                world.addEntity(miner);
                miner.scheduleActions(scheduler, world, imageStore);

                return true;
            }

            return false;
        }

        public Point nextPosition(
                WorldModel world, Point destPos)
        {
            int horiz = Integer.signum(destPos.x - getPosition().x);
            Point newPos = new Point(getPosition().x + horiz, getPosition().y);

            if (horiz == 0 || world.isOccupied(newPos) && world.getOccupancyCell(newPos).getClass() != Stump.class){
                int vert = Integer.signum(destPos.y - getPosition().y);
                newPos = new Point(getPosition().x, getPosition().y + vert);

                if (vert == 0 || world.isOccupied(newPos) && world.getOccupancyCell(newPos).getClass() != Stump.class){
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
                this.resourceCount += 1;
                ((TreeEntity)target).setHealth(((TreeEntity)target).getHealth() - 1);
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
