import processing.core.PImage;

import java.util.List;

public class Factory {

    public static Action createAnimationAction(AnimationEntity entity, int repeatCount) {
        return new AnimationAction(entity,
                repeatCount);
    }

    public static Action createActivityAction(ActionEntity entity,
                                              WorldModel world, ImageStore imageStore)
    {
        return new ActivityAction(entity, world, imageStore);
    }

    public static ActionEntity createTree(
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
    public static ActionEntity createSapling(
            String id,
            Point position,
            List<PImage> images)
    {
        return new Sapling(id, position, images,
                Parse.SAPLING_ACTION_ANIMATION_PERIOD, Parse.SAPLING_ACTION_ANIMATION_PERIOD, 0, Parse.SAPLING_HEALTH_LIMIT);
    }


    // need resource count, though it always starts at 0
    public static ActionEntity createDudeNotFull(
            String id,
            Point position,
            int actionPeriod,
            int animationPeriod,
            int resourceLimit,
            List<PImage> images)
    {
        return new Dude_Not_Full(id, position, images, animationPeriod, actionPeriod, resourceLimit, 0);
    }

    // don't technically need resource count ... full
    public static ActionEntity createDudeFull(
            String id,
            Point position,
            int actionPeriod,
            int animationPeriod,
            int resourceLimit,
            List<PImage> images) {
        return new Dude_Full(id, position, images, animationPeriod, actionPeriod,resourceLimit);
    }

    public static Entity createHouse(
            String id, Point position, List<PImage> images)
    {
        return new House(id, position, images);
    }

    public static AnimationEntity createObstacle(
            String id, Point position, int animationPeriod, List<PImage> images)
    {
        return new Obstacle(id, position, images,
                animationPeriod);
    }

    public static ActionEntity createFairy(
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
