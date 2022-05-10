import processing.core.PImage;

import java.util.*;

public class Sapling extends TreeEntity {
        private final int healthLimit;

        private static final int TREE_ANIMATION_MAX = 600;
        private static final int TREE_ANIMATION_MIN = 50;
        private static final int TREE_ACTION_MAX = 1400;
        private static final int TREE_ACTION_MIN = 1000;
        private static final int TREE_HEALTH_MAX = 3;
        private static final int TREE_HEALTH_MIN = 1;

        private static final Random rand = new Random();

        public Sapling(
                String id,
                Point position,
                List<PImage> images,
                int actionPeriod,
                int animationPeriod,
                int health,
                int healthLimit)
        {
            super(id, position, images, actionPeriod, animationPeriod, health);
            this.healthLimit = healthLimit;
        }

        public void executeActivity(
                WorldModel world,
                ImageStore imageStore,
                EventScheduler scheduler)
        {
            setHealth(getHealth() + 1);
            if (!transformPlant(world, scheduler, imageStore))
            {
                scheduler.scheduleEvent(this,
                        Factory.createActivityAction(this, world, imageStore),
                        getActionPeriod());
            }
        }

        private static int getNumFromRange(int max, int min)
        {
            Random rand = new Random();
            return min + rand.nextInt(
                    max
                            - min);
        }

         protected boolean _transformHelper(WorldModel world,
                                                EventScheduler scheduler,
                                                ImageStore imageStore) {
            if (getHealth() >= this.healthLimit)
        {
            AnimationEntity tree = Factory.createTree("tree_" + getId(),
                    getPosition(),
                    this.getNumFromRange(TREE_ACTION_MAX, TREE_ACTION_MIN),
                    this.getNumFromRange(TREE_ANIMATION_MAX, TREE_ANIMATION_MIN),
                    this.getNumFromRange(TREE_HEALTH_MAX, TREE_HEALTH_MIN),
                    imageStore.getImageList(Parse.TREE_KEY));

            world.removeEntity(this);
            scheduler.unscheduleAllEvents(this);

            world.addEntity(tree);
            tree.scheduleActions(scheduler, world, imageStore);

            return true;
        }

        return false;
    }
    }

