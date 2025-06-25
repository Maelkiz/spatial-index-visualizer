import processing.core.PApplet;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpatialIndexVisualizer extends PApplet {
    private SpatialIndex<SpatialObject> spatialIndex;

    public static void main(String[] args) {
        PApplet.main("SpatialIndexVisualizer");
    }

    @Override
    public void settings() {
        fullScreen(P2D);
    }

    @Override
    public void setup() {
        spatialIndex = buildSpatialIndex(new QuadTree<>(0, 0, width, height), 2500);
        keyRepeatEnabled = true;
    }

    @Override
    public void draw() {
        background(0);
        spatialIndex.draw(this);
    }

    @Override
    public void keyPressed() {
        if (key == 's') {
            int saveIdx = findHighestIndex() + 1;
            String idx = nf(saveIdx, 4);
            String base = spatialIndex.getClass().getSimpleName();
            saveFrame("output/" + base + "-" + idx + ".png");
            println("Saved: " + base + "-" + idx + ".png");
            saveIdx++;
        }

        if (key == '1') {
            spatialIndex = buildSpatialIndex(new QuadTree<>(0, 0, width, height), 1000);
        }
        if (key == '2') {
            spatialIndex = buildSpatialIndex(new KDTree<>(), 1000);
        }
        if (key == '3') {
            spatialIndex = buildSpatialIndex(new RTree<>(), 1000);
        }
    }

    private SpatialIndex<SpatialObject> buildSpatialIndex(SpatialIndex<SpatialObject> spatialIndex, int numObjects) {
        long seed = System.currentTimeMillis();
        noiseSeed(seed);

        float scale = 0.005f; // Lower = smoother gradients

        for (int i = 0; i < numObjects * 2; i++) {
            float seedX = random(width);
            float seedY = random(height);
            float noiseValue = noise(seedX * scale, seedY * scale);

            if (noiseValue > 0.5f) {
                SpatialObject object = createSpatialObject(seedX, seedY);
                spatialIndex.insert(object);
            }
        }

        return spatialIndex;
    }

    private SpatialObject createSpatialObject(float x, float y) {
        return new Rectangle(x, y, random(10, 50), random(10, 50));
    }



    private int findHighestIndex() {
        String base = spatialIndex.getClass().getSimpleName();
        File dir = new File(sketchPath("output"));
        File[] files = dir.listFiles();
        if (files == null)
            return 0;

        Pattern p = Pattern.compile(Pattern.quote(base) + "-(\\d{4})\\.png");
        int max = 0;
        for (File f : files) {
            Matcher m = p.matcher(f.getName());
            if (m.matches()) {
                int v = Integer.parseInt(m.group(1));
                if (v > max)
                    max = v;
            }
        }
        return max;
    }
}
