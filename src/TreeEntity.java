public interface TreeEntity extends Entity {
    int getHealth();
    boolean transformPlant(WorldModel world, EventScheduler scheduler, ImageStore imageStore);
}
