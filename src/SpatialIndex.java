import processing.core.PApplet;

public interface SpatialIndex {
    void insert(Point point);

    void draw(PApplet p);
}
