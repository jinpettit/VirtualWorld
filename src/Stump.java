import processing.core.PImage;
import java.util.*;

public class Stump implements Entity {
    private final String id;
    private Point position;
    private final List<PImage> images;

    public Stump(
            String id,
            Point position,
            List<PImage> images) {
        this.id = id;
        this.position = position;
        this.images = images;
    }

    public String getId() {
        return id;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public PImage getCurrentImage() {
        return images.get(0);


    }
}
