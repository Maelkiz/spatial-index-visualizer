public record Rectangle(float x, float y, float width, float height) {

    public boolean contains(Point point) {
        return point.x() >= x && point.x() < x + width &&
                point.y() >= y && point.y() < y + height;
    }

    public boolean intersects(Rectangle other) {
        return !(other.x > x + width ||
                other.x + other.width < x ||
                other.y > y + height ||
                other.y + other.height < y);
    }
}