import processing.core.PImage;

public abstract class Entity {
    abstract String getId();
    abstract Point getPosition();
    abstract void setPosition(Point position);
    abstract PImage getCurrentImage();
}
