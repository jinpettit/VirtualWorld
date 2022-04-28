import processing.core.PImage;

public interface AnimationEntity extends Entity{
    void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore);
    int getAnimationPeriod();
    PImage getCurrentImage();
    void nextImage();
}
