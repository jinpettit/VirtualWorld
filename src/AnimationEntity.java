public interface AnimationEntity extends Entity{
    int getAnimationPeriod();
    void nextImage();
    void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore);
}
