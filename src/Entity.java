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
        health++;
        if (!Functions.transformPlant(this, world, scheduler, imageStore))
        {
            Functions.scheduleEvent(scheduler, this,
                    Functions.createActivityAction(this, world, imageStore),
                    actionPeriod);
        }
    }

    public void executeTreeActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {

        if (!Functions.transformPlant(this, world, scheduler, imageStore)) {

            Functions.scheduleEvent(scheduler, this,
                    Functions.createActivityAction(this, world, imageStore),
                    actionPeriod);
        }
    }

    public void executeFairyActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        Optional<Entity> fairyTarget =
                Functions.findNearest(world, position, new ArrayList<>(Arrays.asList(EntityKind.STUMP)));

        if (fairyTarget.isPresent()) {
            Point tgtPos = fairyTarget.get().position;

            if (Functions.moveToFairy(this, world, fairyTarget.get(), scheduler)) {
                Entity sapling = Functions.createSapling("sapling_" + id, tgtPos,
                        Functions.getImageList(imageStore, Functions.SAPLING_KEY));

                Functions.addEntity(world, sapling);
                Functions.scheduleActions(sapling, scheduler, world, imageStore);
            }
        }

        Functions.scheduleEvent(scheduler, this,
                Functions.createActivityAction(this, world, imageStore),
                actionPeriod);
    }

    public void executeDudeNotFullActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        Optional<Entity> target =
                Functions.findNearest(world, position, new ArrayList<>(Arrays.asList(EntityKind.TREE, EntityKind.SAPLING)));

        if (!target.isPresent() || !Functions.moveToNotFull(this, world,
                target.get(),
                scheduler)
                || !transformNotFull(world, scheduler, imageStore))
        {
            Functions.scheduleEvent(scheduler, this,
                    Functions.createActivityAction(this, world, imageStore),
                    actionPeriod);
        }
    }

    public void executeDudeFullActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        Optional<Entity> fullTarget =
                Functions.findNearest(world, position, new ArrayList<>(Arrays.asList(EntityKind.HOUSE)));

        if (fullTarget.isPresent() && Functions.moveToFull(this, world,
                fullTarget.get(), scheduler))
        {
            transformFull(world, scheduler, imageStore);
        }
        else {
            Functions.scheduleEvent(scheduler, this,
                    Functions.createActivityAction(this, world, imageStore),
                    actionPeriod);
        }
    }
    public boolean transformNotFull(
            WorldModel world,
            EventScheduler scheduler,
            ImageStore imageStore)
    {
        if (resourceCount >= resourceLimit) {
            Entity miner = Functions.createDudeFull(id,
                    position, actionPeriod,
                    animationPeriod,
                    resourceLimit,
                    images);

            Functions.removeEntity(world, this);
            Functions.unscheduleAllEvents(scheduler, this);

            Functions.addEntity(world, miner);
            Functions.scheduleActions(miner, scheduler, world, imageStore);

            return true;
        }

        return false;
    }

    public void transformFull(
            WorldModel world,
            EventScheduler scheduler,
            ImageStore imageStore)
    {
        Entity miner = Functions.createDudeNotFull(id,
                position, actionPeriod,
                animationPeriod,
                resourceLimit,
                images);

        Functions.removeEntity(world, this);
        Functions.unscheduleAllEvents(scheduler, this);

        Functions.addEntity(world, miner);
        Functions.scheduleActions(miner, scheduler, world, imageStore);
    }
}
