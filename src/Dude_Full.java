import java.util.*;

import processing.core.PImage;

public class Dude_Full implements ActionEntity,AnimationEntity, Move{
        private final String id;
        private Point position;
        private final List<PImage> images;
        private int imageIndex;
        private final int resourceLimit;
        private int resourceCount;
        private final int actionPeriod;
        private final int animationPeriod;

        public Dude_Full(
                String id,
                Point position,
                List<PImage> images,
                int resourceLimit,
                int resourceCount,
                int actionPeriod,
                int animationPeriod)
        {
            this.id = id;
            this.position = position;
            this.images = images;
            this.imageIndex = 0;
            this.resourceLimit = resourceLimit;
            this.resourceCount = resourceCount;
            this.actionPeriod = actionPeriod;
            this.animationPeriod = animationPeriod;
        }

        public String getId(){
            return id;
        }

        public Point getPosition() {
            return position;
        }

        public void setPosition(Point position) {
            this.position = position;
        }

        public void executeActivity(
                WorldModel world,
                ImageStore imageStore,
                EventScheduler scheduler)
        {
            Optional<Entity> fullTarget =
                    world.findNearest(this.position, new ArrayList<>(Arrays.asList(House.class)));

            if (fullTarget.isPresent() && moveToFull(world,
                    fullTarget.get(), scheduler))
            {
                transformFull(world, scheduler, imageStore);
            }
            else {
                scheduler.scheduleEvent(this,
                        Factory.createActivityAction(this,world, imageStore),
                        this.actionPeriod);
            }
        }


        public void transformFull(
                WorldModel world,
                EventScheduler scheduler,
                ImageStore imageStore)
        {
            AnimationEntity miner = Factory.createDudeNotFull(this.id,
                    this.position, this.actionPeriod,
                    this.animationPeriod,
                    this.resourceLimit,
                    this.images);

            world.removeEntity(this);
            scheduler.unscheduleAllEvents(this);

            world.addEntity(miner);
            miner.scheduleActions(scheduler, world, imageStore);
        }

        public Point nextPosition(
                WorldModel world, Point destPos)
        {
            int horiz = Integer.signum(destPos.x - this.position.x);
            Point newPos = new Point(this.position.x + horiz, this.position.y);

            if (horiz == 0 || world.isOccupied(newPos) && world.getOccupancyCell(newPos).getClass() != Stump.class) {
                int vert = Integer.signum(destPos.y - this.position.y);
                newPos = new Point(this.position.x, this.position.y + vert);

                if (vert == 0 || world.isOccupied(newPos) && world.getOccupancyCell(newPos).getClass() != Stump.class) {
                    newPos = this.position;
                }
            }

            return newPos;
        }

        public void scheduleActions(
                EventScheduler scheduler,
                WorldModel world,
                ImageStore imageStore)
        {
                    scheduler.scheduleEvent(this,
                            Factory.createActivityAction(this, world, imageStore),
                            this.actionPeriod);
                    scheduler.scheduleEvent(this,
                            Factory.createAnimationAction(this,0),
                            getAnimationPeriod());
            }

        public PImage getCurrentImage() {
            return (images.get(imageIndex));
        }

        public int getAnimationPeriod() {
                    return this.animationPeriod;
            }

        public void nextImage() {
            this.imageIndex = (this.imageIndex + 1) % this.images.size();
        }

        public boolean moveToFull(
                WorldModel world,
                Entity target,
                EventScheduler scheduler)
        {
            if (this.position.adjacent(target.getPosition())) {
                return true;
            }
            else {
                Point nextPos = this.nextPosition(world, target.getPosition());

                if (!this.position.equals(nextPos)) {
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

