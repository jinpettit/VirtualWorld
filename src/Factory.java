import processing.core.PImage;

import java.util.List;

public class Factory {

    public static final int SAPLING_HEALTH_LIMIT = 5;
    public static final int SAPLING_ACTION_ANIMATION_PERIOD = 1000; // have to be in sync since grows and gains health at same time

    public static Action createAnimationAction(AnimationEntity entity, int repeatCount) {
        return new AnimationAction(entity,
                repeatCount);
    }

    public static Action createActivityAction(ActionEntity entity,
                                              WorldModel world, ImageStore imageStore)
    {
        return new ActivityAction(entity, world, imageStore);
    }

    public static AnimationEntity createTree(
            String id,
            Point position,
            int actionPeriod,
            int animationPeriod,
            int health,
            List<PImage> images)
    {
        return new Tree(id, position, images,
                actionPeriod, animationPeriod, health);
    }

    public static Entity createStump(
            String id,
            Point position,
            List<PImage> images)
    {
        return new Stump(id, position, images);
    }

    // health starts at 0 and builds up until ready to convert to Tree
    public static AnimationEntity createSapling(
            String id,
            Point position,
            List<PImage> images)
    {
        return new Sapling(id, position, images,
                SAPLING_ACTION_ANIMATION_PERIOD, SAPLING_ACTION_ANIMATION_PERIOD, 0, SAPLING_HEALTH_LIMIT);
    }


    // need resource count, though it always starts at 0
    public static AnimationEntity createDudeNotFull(
            String id,
            Point position,
            int actionPeriod,
            int animationPeriod,
            int resourceLimit,
            List<PImage> images)
    {
        return new Dude_Not_Full( id, position, images, resourceLimit, 0,
                actionPeriod, animationPeriod);
    }

    // don't technically need resource count ... full
    public static AnimationEntity createDudeFull(
            String id,
            Point position,
            int actionPeriod,
            int animationPeriod,
            int resourceLimit,
            List<PImage> images) {
        return new Dude_Full(id, position, images, resourceLimit, 0,
                actionPeriod, animationPeriod);
    }

    public static Entity createHouse(
            String id, Point position, List<PImage> images)
    {
        return new House(id, position, images);
    }

    public static Entity createObstacle(
            String id, Point position, int animationPeriod, List<PImage> images)
    {
        return new Obstacle(id, position, images,
                animationPeriod);
    }

    public static Entity createFairy(
            String id,
            Point position,
            int actionPeriod,
            int animationPeriod,
            List<PImage> images)
    {
        return new Fairy(id, position, images,
                actionPeriod, animationPeriod);
    }
}
