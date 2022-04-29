import processing.core.PImage;

public interface Entity {
    String getId();
    Point getPosition();
    void setPosition(Point position);
    PImage getCurrentImage();
}
