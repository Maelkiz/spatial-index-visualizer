import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;

public class QuadTree implements SpatialIndex {
    private final int MAX_POINTS = 4;
    private final float x, y, width, height;
    private final QuadTree[] quadrants;
    private final List<Point> points;

    public QuadTree(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.quadrants = new QuadTree[4];
        this.points = new ArrayList<>();
    }

    public void query(Rectangle range, List<Point> found) {
        if (!intersects(range))
            return;

        for (Point p : points) {
            if (range.contains(p)) {
                found.add(p);
            }
        }

        if (quadrants[0] != null) {
            for (QuadTree child : quadrants) {
                child.query(range, found);
            }
        }
    }

    public void insert(Point point) {
        if (!contains(point)) {
            return;
        }

        if (points.size() < MAX_POINTS) {
            points.add(point);
            return;
        }

        if (quadrants[0] == null) {
            subdivide();
        }

        for (QuadTree child : quadrants) {
            child.insert(point);
        }
    }

    private void subdivide() {
        float halfWidth = width / 2;
        float halfHeight = height / 2;

        quadrants[0] = new QuadTree(x, y, halfWidth, halfHeight); // Top-left
        quadrants[1] = new QuadTree(x + halfWidth, y, halfWidth, halfHeight); // Top-right
        quadrants[2] = new QuadTree(x, y + halfHeight, halfWidth, halfHeight); // Bottom-left
        quadrants[3] = new QuadTree(x + halfWidth, y + halfHeight, halfWidth, halfHeight); // Bottom-right
    }

    public boolean contains(Point point) {
        return point.x() >= x
                && point.x() <= x + width
                && point.y() >= y
                && point.y() <= y + height;
    }

    private boolean intersects(Rectangle range) {
        return !(range.x() > x + width ||
                range.x() + range.width() < x ||
                range.y() > y + height ||
                range.y() + range.height() < y);
    }

    public void draw(PApplet p) {
        p.stroke(255, 0, 0);
        p.noFill();
        p.rect(x, y, width, height);
        for (Point point : points) {
            p.fill(255, 0, 0);
            point.draw(p);
        }
        if (quadrants[0] != null) {
            for (QuadTree child : quadrants) {
                child.draw(p);
            }
        }
    }
}