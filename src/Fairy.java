import processing.core.PImage;

import java.util.*;

public class Fairy implements Entity {
    private final String id;
    private Point position;
    private final List<PImage> images;
    private int imageIndex;
    private final int actionPeriod;
    private final int animationPeriod;

        public Fairy(
                String id,
                Point position,
                List<PImage> images,
                int actionPeriod,
                int animationPeriod)
        {
            this.id = id;
            this.position = position;
            this.images = images;
            this.imageIndex = 0;
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

        public void executeFairyActivity(
                WorldModel world,
                ImageStore imageStore,
                EventScheduler scheduler)
        {
            Optional<Entity> fairyTarget =
                    world.findNearest( this.position, new ArrayList<>(Arrays.asList(EntityKind.STUMP)));

            if (fairyTarget.isPresent()) {
                Point tgtPos = fairyTarget.get().getPosition();

                if (moveToFairy(world, fairyTarget.get(), scheduler)) {
                    Entity sapling = Functions.createSapling("sapling_" + this.id, tgtPos,
                            imageStore.getImageList(Functions.SAPLING_KEY));

                    world.addEntity(sapling);
                    ((Fairy)sapling).scheduleActions(scheduler, world, imageStore);
                }
            }

            scheduler.scheduleEvent(this,
                    Functions.createActivityAction(this, world, imageStore),
                    this.actionPeriod);
        }

        private Point nextPositionFairy(
                WorldModel world, Point destPos)
        {
            int horiz = Integer.signum(destPos.x - this.position.x);
            Point newPos = new Point(this.position.x + horiz, this.position.y);

            if (horiz == 0 || world.isOccupied(newPos)) {
                int vert = Integer.signum(destPos.y - this.position.y);
                newPos = new Point(this.position.x, this.position.y + vert);

                if (vert == 0 || world.isOccupied(newPos)) {
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
                            Functions.createActivityAction(this,world, imageStore),
                            this.actionPeriod);
                    scheduler.scheduleEvent(this,
                            Functions.createAnimationAction(this,0),
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


        private boolean moveToFairy(
                WorldModel world,
                Entity target,
                EventScheduler scheduler)
        {
            if (this.position.adjacent(target.getPosition())) {
                world.removeEntity(target);
                scheduler.unscheduleAllEvents(target);
                return true;
            }
            else {
                Point nextPos = this.nextPositionFairy(world, target.getPosition());

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
