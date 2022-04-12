import java.util.*;

import processing.core.PImage;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public final class Entity
{
    public EntityKind kind;
    public String id;
    public Point position;
    public List<PImage> images;
    public int imageIndex;
    public int resourceLimit;
    public int resourceCount;
    public int actionPeriod;
    public int animationPeriod;
    public int health;
    public int healthLimit;

    public Entity(
            EntityKind kind,
            String id,
            Point position,
            List<PImage> images,
            int resourceLimit,
            int resourceCount,
            int actionPeriod,
            int animationPeriod,
            int health,
            int healthLimit)
    {
        this.kind = kind;
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        this.resourceLimit = resourceLimit;
        this.resourceCount = resourceCount;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
        this.health = health;
        this.healthLimit = healthLimit;
    }
    public void executeSaplingActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        this.health++;
        if (!transformPlant(world, scheduler, imageStore))
        {
            Functions.scheduleEvent(scheduler, this,
                    Functions.createActivityAction(this, world, imageStore),
                    this.actionPeriod);
        }
    }

    public void executeTreeActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {

        if (!transformPlant(world, scheduler, imageStore)) {

            Functions.scheduleEvent(scheduler, this,
                    Functions.createActivityAction(this, world, imageStore),
                    this.actionPeriod);
        }
    }

    public void executeFairyActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        Optional<Entity> fairyTarget =
                Functions.findNearest(world, this.position, new ArrayList<>(Arrays.asList(EntityKind.STUMP)));

        if (fairyTarget.isPresent()) {
            Point tgtPos = fairyTarget.get().position;

            if (moveToFairy(this, world, fairyTarget.get(), scheduler)) {
                Entity sapling = Functions.createSapling("sapling_" + this.id, tgtPos,
                        Functions.getImageList(imageStore, Functions.SAPLING_KEY));

                world.addEntity(sapling);
                sapling.scheduleActions(scheduler, world, imageStore);
            }
        }

        Functions.scheduleEvent(scheduler, this,
                Functions.createActivityAction(this, world, imageStore),
                this.actionPeriod);
    }

    public void executeDudeNotFullActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        Optional<Entity> target =
                Functions.findNearest(world, this.position, new ArrayList<>(Arrays.asList(EntityKind.TREE, EntityKind.SAPLING)));

        if (!target.isPresent() || !moveToNotFull(this, world,
                target.get(),
                scheduler)
                || !transformNotFull(world, scheduler, imageStore))
        {
            Functions.scheduleEvent(scheduler, this,
                    Functions.createActivityAction(this, world, imageStore),
                    this.actionPeriod);
        }
    }

    public void executeDudeFullActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        Optional<Entity> fullTarget =
                Functions.findNearest(world, this.position, new ArrayList<>(Arrays.asList(EntityKind.HOUSE)));

        if (fullTarget.isPresent() && moveToFull(this, world,
                fullTarget.get(), scheduler))
        {
            transformFull(world, scheduler, imageStore);
        }
        else {
            Functions.scheduleEvent(scheduler, this,
                    Functions.createActivityAction(this, world, imageStore),
                    this.actionPeriod);
        }
    }
    public boolean transformNotFull(
            WorldModel world,
            EventScheduler scheduler,
            ImageStore imageStore)
    {
        if (this.resourceCount >= this.resourceLimit) {
            Entity miner = Functions.createDudeFull(this.id,
                    this.position, this.actionPeriod,
                    this.animationPeriod,
                    this.resourceLimit,
                    this.images);

            Functions.removeEntity(world, this);
            Functions.unscheduleAllEvents(scheduler, this);

            world.addEntity(miner);
            miner.scheduleActions(scheduler, world, imageStore);

            return true;
        }

        return false;
    }

    public void transformFull(
            WorldModel world,
            EventScheduler scheduler,
            ImageStore imageStore)
    {
        Entity miner = Functions.createDudeNotFull(this.id,
                this.position, this.actionPeriod,
                this.animationPeriod,
                this.resourceLimit,
                this.images);

        Functions.removeEntity(world, this);
        Functions.unscheduleAllEvents(scheduler, this);

        world.addEntity(miner);
        miner.scheduleActions(scheduler, world, imageStore);
    }

    public Point nextPositionFairy(
             WorldModel world, Point destPos)
    {
        int horiz = Integer.signum(destPos.x - this.position.x);
        Point newPos = new Point(this.position.x + horiz, this.position.y);

        if (horiz == 0 || Functions.isOccupied(world, newPos)) {
            int vert = Integer.signum(destPos.y - this.position.y);
            newPos = new Point(this.position.x, this.position.y + vert);

            if (vert == 0 || Functions.isOccupied(world, newPos)) {
                newPos = this.position;
            }
        }

        return newPos;
    }

    public Point nextPositionDude(
            WorldModel world, Point destPos)
    {
        int horiz = Integer.signum(destPos.x - this.position.x);
        Point newPos = new Point(this.position.x + horiz, this.position.y);

        if (horiz == 0 || Functions.isOccupied(world, newPos) && Functions.getOccupancyCell(world, newPos).kind != EntityKind.STUMP) {
            int vert = Integer.signum(destPos.y - this.position.y);
            newPos = new Point(this.position.x, this.position.y + vert);

            if (vert == 0 || Functions.isOccupied(world, newPos) &&  Functions.getOccupancyCell(world, newPos).kind != EntityKind.STUMP) {
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
        switch (this.kind) {
            case DUDE_FULL:
                Functions.scheduleEvent(scheduler, this,
                        Functions.createActivityAction(this, world, imageStore),
                        this.actionPeriod);
                Functions.scheduleEvent(scheduler, this,
                        Functions.createAnimationAction(this, 0),
                        getAnimationPeriod());
                break;

            case DUDE_NOT_FULL:
                Functions.scheduleEvent(scheduler, this,
                        Functions.createActivityAction(this, world, imageStore),
                        this.actionPeriod);
                Functions.scheduleEvent(scheduler, this,
                        Functions.createAnimationAction(this, 0),
                        getAnimationPeriod());
                break;

            case OBSTACLE:
                Functions.scheduleEvent(scheduler, this,
                        Functions.createAnimationAction(this, 0),
                        getAnimationPeriod());
                break;

            case FAIRY:
                Functions.scheduleEvent(scheduler, this,
                        Functions.createActivityAction(this, world, imageStore),
                        this.actionPeriod);
                Functions.scheduleEvent(scheduler, this,
                        Functions.createAnimationAction(this, 0),
                        getAnimationPeriod());
                break;

            case SAPLING:
                Functions.scheduleEvent(scheduler, this,
                        Functions.createActivityAction(this, world, imageStore),
                        this.actionPeriod);
                Functions.scheduleEvent(scheduler, this,
                        Functions.createAnimationAction(this, 0),
                        getAnimationPeriod());
                break;

            case TREE:
                Functions.scheduleEvent(scheduler, this,
                        Functions.createActivityAction(this, world, imageStore),
                        this.actionPeriod);
                Functions.scheduleEvent(scheduler, this,
                        Functions.createAnimationAction(this, 0),
                        getAnimationPeriod());
                break;

            default:
        }
    }
    public PImage getCurrentImage() {
            return (images.get(imageIndex));
        }

    public int getAnimationPeriod() {
        switch (this.kind) {
            case DUDE_FULL:
            case DUDE_NOT_FULL:
            case OBSTACLE:
            case FAIRY:
            case SAPLING:
            case TREE:
                return this.animationPeriod;
            default:
                throw new UnsupportedOperationException(
                        String.format("getAnimationPeriod not supported for %s",
                                this.kind));
        }
    }
    public void nextImage() {
        this.imageIndex = (this.imageIndex + 1) % this.images.size();
    }

    public boolean transformPlant( WorldModel world,
                                          EventScheduler scheduler,
                                          ImageStore imageStore)
    {
        if (this.kind == EntityKind.TREE)
        {
            return transformTree(world, scheduler, imageStore);
        }
        else if (this.kind == EntityKind.SAPLING)
        {
            return transformSapling(world, scheduler, imageStore);
        }
        else
        {
            throw new UnsupportedOperationException(
                    String.format("transformPlant not supported for %s", this));
        }
    }

    public boolean transformTree(
            WorldModel world,
            EventScheduler scheduler,
            ImageStore imageStore)
    {
        if (this.health <= 0) {
            Entity stump = Functions.createStump(this.id,
                    this.position,
                    Functions.getImageList(imageStore, Functions.STUMP_KEY));

            Functions.removeEntity(world, this);
            Functions.unscheduleAllEvents(scheduler, this);

            world.addEntity(stump);
            stump.scheduleActions(scheduler, world, imageStore);

            return true;
        }

        return false;
    }

    public boolean transformSapling(
            WorldModel world,
            EventScheduler scheduler,
            ImageStore imageStore)
    {
        if (this.health <= 0) {
            Entity stump = Functions.createStump(this.id,
                    this.position,
                    Functions.getImageList(imageStore, Functions.STUMP_KEY));

            Functions.removeEntity(world, this);
            Functions.unscheduleAllEvents(scheduler, this);

            world.addEntity(stump);
            stump.scheduleActions(scheduler, world, imageStore);

            return true;
        }
        else if (this.health >= this.healthLimit)
        {
            Entity tree = Functions.createTree("tree_" + this.id,
                    this.position,
                    Functions.getNumFromRange(Functions.TREE_ACTION_MAX, Functions.TREE_ACTION_MIN),
                    Functions.getNumFromRange(Functions.TREE_ANIMATION_MAX, Functions.TREE_ANIMATION_MIN),
                    Functions.getNumFromRange(Functions.TREE_HEALTH_MAX, Functions.TREE_HEALTH_MIN),
                    Functions.getImageList(imageStore, Functions.TREE_KEY));

            Functions.removeEntity(world, this);
            Functions.unscheduleAllEvents(scheduler, this);

            world.addEntity(tree);
            tree.scheduleActions(scheduler, world, imageStore);

            return true;
        }

        return false;
    }

    public boolean moveToFairy(
            Entity fairy,
            WorldModel world,
            Entity target,
            EventScheduler scheduler)
    {
        if (Functions.adjacent(fairy.position, target.position)) {
            Functions.removeEntity(world, target);
            Functions.unscheduleAllEvents(scheduler, target);
            return true;
        }
        else {
            Point nextPos = fairy.nextPositionFairy(world, target.position);

            if (!fairy.position.equals(nextPos)) {
                Optional<Entity> occupant = Functions.getOccupant(world, nextPos);
                if (occupant.isPresent()) {
                    Functions.unscheduleAllEvents(scheduler, occupant.get());
                }

                Functions.moveEntity(world, fairy, nextPos);
            }
            return false;
        }
    }

    public boolean moveToNotFull(
            Entity dude,
            WorldModel world,
            Entity target,
            EventScheduler scheduler)
    {
        if (Functions.adjacent(dude.position, target.position)) {
            dude.resourceCount += 1;
            target.health--;
            return true;
        }
        else {
            Point nextPos = dude.nextPositionDude(world, target.position);

            if (!dude.position.equals(nextPos)) {
                Optional<Entity> occupant = Functions.getOccupant(world, nextPos);
                if (occupant.isPresent()) {
                    Functions.unscheduleAllEvents(scheduler, occupant.get());
                }

                Functions.moveEntity(world, dude, nextPos);
            }
            return false;
        }
    }

    public boolean moveToFull(
            Entity dude,
            WorldModel world,
            Entity target,
            EventScheduler scheduler)
    {
        if (Functions.adjacent(dude.position, target.position)) {
            return true;
        }
        else {
            Point nextPos = dude.nextPositionDude(world, target.position);

            if (!dude.position.equals(nextPos)) {
                Optional<Entity> occupant = Functions.getOccupant(world, nextPos);
                if (occupant.isPresent()) {
                    Functions.unscheduleAllEvents(scheduler, occupant.get());
                }

                Functions.moveEntity(world, dude, nextPos);
            }
            return false;
        }
    }

}


