import processing.core.PImage;

import java.util.*;

public class Obstacle implements AnimationEntity{
    private final String id;
    private Point position;
    private final List<PImage> images;
    private int imageIndex;
    private final int animationPeriod;

        public Obstacle(
                String id,
                Point position,
                List<PImage> images,
                int animationPeriod)
        {
            this.id = id;
            this.position = position;
            this.images = images;
            this.imageIndex = 0;
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

        public PImage getCurrentImage() {
            return (images.get(imageIndex));
        }

        public void scheduleActions(
            EventScheduler scheduler,
            WorldModel world,
            ImageStore imageStore) {

        scheduler.scheduleEvent(this,
                Factory.createAnimationAction(this, 0),
                getAnimationPeriod());
        }

        public int getAnimationPeriod() {
                return this.animationPeriod;

    }

        public void nextImage() {
            this.imageIndex = (this.imageIndex + 1) % this.images.size();
        }

}
