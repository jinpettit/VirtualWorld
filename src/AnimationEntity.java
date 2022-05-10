import processing.core.PImage;

import java.util.List;

public abstract class AnimationEntity extends Entity{
    private final int animationPeriod;

    public AnimationEntity(String id, Point position, List<PImage> images, int animationPeriod) {
        super(id, position, images);
        this.animationPeriod = animationPeriod;
    }

    public int getAnimationPeriod() {
        return this.animationPeriod;
    }
    public void nextImage() {
        setImageIndex((getImageIndex() + 1) % getImages().size());
    }
    public void scheduleActions(
            EventScheduler scheduler,
            WorldModel world,
            ImageStore imageStore) {

        scheduler.scheduleEvent(this,
                Factory.createAnimationAction(this, 0),
                getAnimationPeriod());
    }

}
