public interface TreeEntity extends ActionEntity{
    int getHealth();
    boolean transformPlant(WorldModel world, EventScheduler scheduler, ImageStore imageStore);
    void setHealth(int health);
}
