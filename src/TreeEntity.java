public abstract class TreeEntity extends ActionEntity{
    abstract int getHealth();
    abstract boolean transformPlant(WorldModel world, EventScheduler scheduler, ImageStore imageStore);
    abstract void setHealth(int health);
}
