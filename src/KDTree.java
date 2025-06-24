import processing.core.PApplet;

public class KDTree implements SpatialIndex {
    private Node root;

    public KDTree() {
        this.root = null;
    }

    public void insert(Point point) {
        root = insert(root, point, 0);
    }

    private Node insert(Node node, Point point, int depth) {
        if (node == null) {
            return new Node(point);
        }

        if (node.point.equals(point)) {
            return node; // Avoid duplicates
        }

        int axis = depth % 2;
        if ((axis == 0 && point.x() < node.point.x()) || (axis == 1 && point.y() < node.point.y())) {
            node.left = insert(node.left, point, depth + 1);
        } else {
            node.right = insert(node.right, point, depth + 1);
        }
        return node;
    }

    public boolean contains(Point point) {
        return contains(root, point, 0);
    }

    private boolean contains(Node node, Point point, int depth) {
        if (node == null)
            return false;
        if (node.point.equals(point))
            return true;

        int axis = depth % 2;
        if ((axis == 0 && point.x() < node.point.x()) || (axis == 1 && point.y() < node.point.y())) {
            return contains(node.left, point, depth + 1);
        } else {
            return contains(node.right, point, depth + 1);
        }
    }

    public Point nearest(Point target) {
        return nearest(root, target, 0, null, Double.POSITIVE_INFINITY);
    }

    private Point nearest(Node node, Point target, int depth, Point best, double bestDist) {
        if (node == null)
            return best;

        double dist = distanceSquared(node.point, target);
        if (dist < bestDist) {
            bestDist = dist;
            best = node.point;
        }

        int axis = depth % 2;
        Node nearChild = (axis == 0 && target.x() < node.point.x()) || (axis == 1 && target.y() < node.point.y())
                ? node.left
                : node.right;
        Node farChild = nearChild == node.left ? node.right : node.left;

        best = nearest(nearChild, target, depth + 1, best, bestDist);
        bestDist = distanceSquared(best, target);

        double axisDist = axis == 0 ? Math.pow(target.x() - node.point.x(), 2)
                : Math.pow(target.y() - node.point.y(), 2);
        if (axisDist < bestDist) {
            best = nearest(farChild, target, depth + 1, best, bestDist);
        }

        return best;
    }

    private double distanceSquared(Point a, Point b) {
        double dx = a.x() - b.x();
        double dy = a.y() - b.y();
        return dx * dx + dy * dy;
    }

    private static class Node {
        Point point;
        Node left, right;

        Node(Point point) {
            this.point = point;
        }
    }

    public void draw(PApplet p) {
        drawNode(p, root, 0, 0, p.width, 0, p.height);
    }

    private void drawNode(PApplet p, Node node, int depth, float minX, float maxX, float minY, float maxY) {
        if (node == null)
            return;

        int axis = depth % 2;
        float x = node.point.x();
        float y = node.point.y();

        node.point.draw(p); // Draw the point

        // Draw the splitting line
        if (axis == 0) {
            // Vertical line (splitting on x)
            p.stroke(255, 0, 0); // red
            p.line(x, minY, x, maxY);
            drawNode(p, node.left, depth + 1, minX, x, minY, maxY);
            drawNode(p, node.right, depth + 1, x, maxX, minY, maxY);
        } else {
            // Horizontal line (splitting on y)
            p.stroke(255, 0, 0); // red
            p.line(minX, y, maxX, y);
            drawNode(p, node.left, depth + 1, minX, maxX, minY, y);
            drawNode(p, node.right, depth + 1, minX, maxX, y, maxY);
        }
    }
}