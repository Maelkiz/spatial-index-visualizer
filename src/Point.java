import processing.core.PApplet;

public record Point(float x, float y) {
    public void draw(PApplet p) {
        p.push();
        p.fill(255);
        p.circle(x, y, 5);
        p.pop();
    }
}
