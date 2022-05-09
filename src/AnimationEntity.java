public abstract class AnimationEntity extends Entity{
    abstract int getAnimationPeriod();
    abstract void nextImage();
    abstract void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore);

}
