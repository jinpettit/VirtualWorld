public interface ActionEntity extends Entity {
    void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler);
}
