import processing.core.PImage;

import java.util.List;

public abstract class ActionEntity extends AnimationEntity {
   private final int actionPeriod;

   public ActionEntity(String id, Point position, List<PImage> images, int animationPeriod, int actionPeriod) {
      super(id, position, images, animationPeriod);
      this.actionPeriod = actionPeriod;
   }

   abstract void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler);

   public int getActionPeriod() {
      return actionPeriod;
   }

   public void scheduleActions(
           EventScheduler scheduler,
           WorldModel world,
           ImageStore imageStore)
   {
      scheduler.scheduleEvent(this,
              Factory.createActivityAction(this,world, imageStore),
              getActionPeriod());
      scheduler.scheduleEvent(this,
              Factory.createAnimationAction(this,0),
              getAnimationPeriod());
   }
}
