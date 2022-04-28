import processing.core.PImage;

import java.util.*;

public class Sapling implements Entity {
        private final String id;
        private Point position;
        private final List<PImage> images;
        private int imageIndex;
        private final int actionPeriod;
        private final int animationPeriod;
        private int health;
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
            this.id = id;
            this.position = position;
            this.images = images;
            this.imageIndex = 0;
            this.actionPeriod = actionPeriod;
            this.animationPeriod = animationPeriod;
            this.health = health;
            this.healthLimit = healthLimit;
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
                        Functions.createActivityAction(this, world, imageStore),
                        this.actionPeriod);
            }
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

        private boolean transformPlant( WorldModel world,
                                        EventScheduler scheduler,
                                        ImageStore imageStore)
        {
                return transformSapling(world, scheduler, imageStore);
        }

        private boolean transformSapling(
                WorldModel world,
                EventScheduler scheduler,
                ImageStore imageStore)
        {
            if (this.health <= 0) {
                Entity stump = Functions.createStump(this.id,
                        this.position,
                        imageStore.getImageList(Functions.STUMP_KEY));

                world.removeEntity(this);
                scheduler.unscheduleAllEvents(this);

                world.addEntity(stump);
                ((Sapling)stump).scheduleActions(scheduler, world, imageStore);

                return true;
            }
            else if (this.health >= this.healthLimit)
            {
                Entity tree = Functions.createTree("tree_" + this.id,
                        this.position,
                        this.getNumFromRange(TREE_ACTION_MAX, TREE_ACTION_MIN),
                        this.getNumFromRange(TREE_ANIMATION_MAX, TREE_ANIMATION_MIN),
                        this.getNumFromRange(TREE_HEALTH_MAX, TREE_HEALTH_MIN),
                        imageStore.getImageList(Functions.TREE_KEY));

                world.removeEntity(this);
                scheduler.unscheduleAllEvents(this);

                world.addEntity(tree);
                ((Sapling)tree).scheduleActions(scheduler, world, imageStore);

                return true;
            }

            return false;
        }


        private static int getNumFromRange(int max, int min)
        {
            Random rand = new Random();
            return min + rand.nextInt(
                    max
                            - min);
        }
}
