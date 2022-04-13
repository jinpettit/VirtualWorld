import java.util.*;

import processing.core.PImage;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public final class Entity
{
    private final EntityKind kind;
    private final String id;
    private Point position;
    private final List<PImage> images;
    private int imageIndex;
    private final int resourceLimit;
    private int resourceCount;
    private final int actionPeriod;
    private final int animationPeriod;
    private int health;
    private final int healthLimit;

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

    public EntityKind getKind() {
        return kind;
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

    public int getHealth() {
        return health;
    }

    public void executeSaplingActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        this.health++;
        if (!transformPlant(world, scheduler, imageStore))
        {
            scheduler.scheduleEvent(this,
                    this.createActivityAction(world, imageStore),
                    this.actionPeriod);
        }
    }

    public void executeTreeActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {

        if (!transformPlant(world, scheduler, imageStore)) {

            scheduler.scheduleEvent(this,
                    this.createActivityAction(world, imageStore),
                    this.actionPeriod);
        }
    }

    public void executeFairyActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        Optional<Entity> fairyTarget =
        world.findNearest( this.position, new ArrayList<>(Arrays.asList(EntityKind.STUMP)));

        if (fairyTarget.isPresent()) {
            Point tgtPos = fairyTarget.get().position;

            if (moveToFairy(world, fairyTarget.get(), scheduler)) {
                Entity sapling = this.createSapling("sapling_" + this.id, tgtPos,
                        imageStore.getImageList(Functions.SAPLING_KEY));

                world.addEntity(sapling);
                sapling.scheduleActions(scheduler, world, imageStore);
            }
        }

        scheduler.scheduleEvent(this,
                this.createActivityAction(world, imageStore),
                this.actionPeriod);
    }

    public void executeDudeNotFullActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        Optional<Entity> target =
        world.findNearest(this.position, new ArrayList<>(Arrays.asList(EntityKind.TREE, EntityKind.SAPLING)));

        if (!target.isPresent() || !moveToNotFull(world,
                target.get(),
                scheduler)
                || !transformNotFull(world, scheduler, imageStore))
        {
            scheduler.scheduleEvent(this,
                    this.createActivityAction(world, imageStore),
                    this.actionPeriod);
        }
    }

    public void executeDudeFullActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        Optional<Entity> fullTarget =
        world.findNearest(this.position, new ArrayList<>(Arrays.asList(EntityKind.HOUSE)));

        if (fullTarget.isPresent() && moveToFull(world,
                fullTarget.get(), scheduler))
        {
            transformFull(world, scheduler, imageStore);
        }
        else {
            scheduler.scheduleEvent(this,
                    this.createActivityAction(world, imageStore),
                    this.actionPeriod);
        }
    }
    private boolean transformNotFull(
            WorldModel world,
            EventScheduler scheduler,
            ImageStore imageStore)
    {
        if (this.resourceCount >= this.resourceLimit) {
            Entity miner = this.createDudeFull(this.id,
                    this.position, this.actionPeriod,
                    this.animationPeriod,
                    this.resourceLimit,
                    this.images);

            world.removeEntity(this);
            scheduler.unscheduleAllEvents(this);

            world.addEntity(miner);
            miner.scheduleActions(scheduler, world, imageStore);

            return true;
        }

        return false;
    }

    private void transformFull(
            WorldModel world,
            EventScheduler scheduler,
            ImageStore imageStore)
    {
        Entity miner = this.createDudeNotFull(this.id,
                this.position, this.actionPeriod,
                this.animationPeriod,
                this.resourceLimit,
                this.images);

        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);

        world.addEntity(miner);
        miner.scheduleActions(scheduler, world, imageStore);
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

    private Point nextPositionDude(
            WorldModel world, Point destPos)
    {
        int horiz = Integer.signum(destPos.x - this.position.x);
        Point newPos = new Point(this.position.x + horiz, this.position.y);

        if (horiz == 0 || world.isOccupied(newPos) && world.getOccupancyCell(newPos).kind != EntityKind.STUMP) {
            int vert = Integer.signum(destPos.y - this.position.y);
            newPos = new Point(this.position.x, this.position.y + vert);

            if (vert == 0 || world.isOccupied(newPos) &&  world.getOccupancyCell(newPos).kind != EntityKind.STUMP) {
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
                scheduler.scheduleEvent(this,
                        this.createActivityAction(world, imageStore),
                        this.actionPeriod);
                scheduler.scheduleEvent(this,
                        this.createAnimationAction(0),
                        getAnimationPeriod());
                break;

            case DUDE_NOT_FULL:
                scheduler.scheduleEvent(this,
                        this.createActivityAction(world, imageStore),
                        this.actionPeriod);
                scheduler.scheduleEvent(this,
                        this.createAnimationAction(0),
                        getAnimationPeriod());
                break;

            case OBSTACLE:
                scheduler.scheduleEvent(this,
                        this.createAnimationAction(0),
                        getAnimationPeriod());
                break;

            case FAIRY:
                scheduler.scheduleEvent(this,
                        this.createActivityAction(world, imageStore),
                        this.actionPeriod);
                scheduler.scheduleEvent(this,
                        this.createAnimationAction(0),
                        getAnimationPeriod());
                break;

            case SAPLING:
                scheduler.scheduleEvent(this,
                        this.createActivityAction(world, imageStore),
                        this.actionPeriod);
                scheduler.scheduleEvent(this,
                        this.createAnimationAction(0),
                        getAnimationPeriod());
                break;

            case TREE:
                scheduler.scheduleEvent(this,
                        this.createActivityAction(world, imageStore),
                        this.actionPeriod);
                scheduler.scheduleEvent(this,
                        this.createAnimationAction(0),
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

    private boolean transformPlant( WorldModel world,
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

    private boolean transformTree(
            WorldModel world,
            EventScheduler scheduler,
            ImageStore imageStore)
    {
        if (this.health <= 0) {
            Entity stump = createStump(this.id,
                    this.position,
                    imageStore.getImageList(Functions.STUMP_KEY));

            world.removeEntity(this);
            scheduler.unscheduleAllEvents(this);

            world.addEntity(stump);
            stump.scheduleActions(scheduler, world, imageStore);

            return true;
        }

        return false;
    }

    private boolean transformSapling(
            WorldModel world,
            EventScheduler scheduler,
            ImageStore imageStore)
    {
        if (this.health <= 0) {
            Entity stump = createStump(this.id,
                    this.position,
                    imageStore.getImageList(Functions.STUMP_KEY));

            world.removeEntity(this);
            scheduler.unscheduleAllEvents(this);

            world.addEntity(stump);
            stump.scheduleActions(scheduler, world, imageStore);

            return true;
        }
        else if (this.health >= this.healthLimit)
        {
            Entity tree = createTree("tree_" + this.id,
                    this.position,
                    Functions.getNumFromRange(Functions.TREE_ACTION_MAX, Functions.TREE_ACTION_MIN),
                    Functions.getNumFromRange(Functions.TREE_ANIMATION_MAX, Functions.TREE_ANIMATION_MIN),
                    Functions.getNumFromRange(Functions.TREE_HEALTH_MAX, Functions.TREE_HEALTH_MIN),
                    imageStore.getImageList(Functions.TREE_KEY));

            world.removeEntity(this);
            scheduler.unscheduleAllEvents(this);

            world.addEntity(tree);
            tree.scheduleActions(scheduler, world, imageStore);

            return true;
        }

        return false;
    }

    private boolean moveToFairy(
            WorldModel world,
            Entity target,
            EventScheduler scheduler)
    {
        if (this.position.adjacent(target.position)) {
            world.removeEntity(target);
            scheduler.unscheduleAllEvents(target);
            return true;
        }
        else {
            Point nextPos = this.nextPositionFairy(world, target.position);

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

    private boolean moveToNotFull(
            WorldModel world,
            Entity target,
            EventScheduler scheduler)
    {
        if (this.position.adjacent(target.position)) {
            this.resourceCount += 1;
            target.health--;
            return true;
        }
        else {
            Point nextPos = this.nextPositionDude(world, target.position);

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

    private boolean moveToFull(
            WorldModel world,
            Entity target,
            EventScheduler scheduler)
    {
        if (this.position.adjacent(target.position)) {
            return true;
        }
        else {
            Point nextPos = this.nextPositionDude(world, target.position);

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

    public Action createAnimationAction(int repeatCount) {
        return new Action(ActionKind.ANIMATION, this, null, null,
                repeatCount);
    }

    private Action createActivityAction(
            WorldModel world, ImageStore imageStore)
    {
        return new Action(ActionKind.ACTIVITY, this, world, imageStore, 0);
    }

    public static Entity createHouse(
            String id, Point position, List<PImage> images)
    {
        return new Entity(EntityKind.HOUSE, id, position, images, 0, 0, 0,
                0, 0, 0);
    }

    public static Entity createObstacle(
            String id, Point position, int animationPeriod, List<PImage> images)
    {
        return new Entity(EntityKind.OBSTACLE, id, position, images, 0, 0, 0,
                animationPeriod, 0, 0);
    }

    public static Entity createTree(
            String id,
            Point position,
            int actionPeriod,
            int animationPeriod,
            int health,
            List<PImage> images)
    {
        return new Entity(EntityKind.TREE, id, position, images, 0, 0,
                actionPeriod, animationPeriod, health, 0);
    }

    private static Entity createStump(
            String id,
            Point position,
            List<PImage> images)
    {
        return new Entity(EntityKind.STUMP, id, position, images, 0, 0,
                0, 0, 0, 0);
    }

    // health starts at 0 and builds up until ready to convert to Tree
    private static Entity createSapling(
            String id,
            Point position,
            List<PImage> images)
    {
        return new Entity(EntityKind.SAPLING, id, position, images, 0, 0,
                Functions.SAPLING_ACTION_ANIMATION_PERIOD, Functions.SAPLING_ACTION_ANIMATION_PERIOD, 0, Functions.SAPLING_HEALTH_LIMIT);
    }

    public static Entity createFairy(
            String id,
            Point position,
            int actionPeriod,
            int animationPeriod,
            List<PImage> images)
    {
        return new Entity(EntityKind.FAIRY, id, position, images, 0, 0,
                actionPeriod, animationPeriod, 0, 0);
    }

    // need resource count, though it always starts at 0
    public static Entity createDudeNotFull(
            String id,
            Point position,
            int actionPeriod,
            int animationPeriod,
            int resourceLimit,
            List<PImage> images)
    {
        return new Entity(EntityKind.DUDE_NOT_FULL, id, position, images, resourceLimit, 0,
                actionPeriod, animationPeriod, 0, 0);
    }

    // don't technically need resource count ... full
    private static Entity createDudeFull(
            String id,
            Point position,
            int actionPeriod,
            int animationPeriod,
            int resourceLimit,
            List<PImage> images) {
        return new Entity(EntityKind.DUDE_FULL, id, position, images, resourceLimit, 0,
                actionPeriod, animationPeriod, 0, 0);
    }

}


