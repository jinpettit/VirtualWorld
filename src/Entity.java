import processing.core.PImage;

import java.util.List;

public interface Entity {
    String getId();
    Point getPosition();
    void setPosition(Point position);
    PImage getCurrentImage();
}
