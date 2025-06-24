import processing.core.PApplet;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpatialIndexVisualizer extends PApplet {
    private SpatialIndex spatialIndex;

    public static void main(String[] args) {
        PApplet.main("SpatialIndexVisualizer");
    }

    @Override
    public void settings() {
        fullScreen(P2D);
    }

    @Override
    public void setup() {
        spatialIndex = buildSpatialIndex(new QuadTree(0, 0, width, height), 2500);
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
            spatialIndex = buildSpatialIndex(new QuadTree(0, 0, width, height), 2500);
        }
        if (key == '2') {
            spatialIndex = buildSpatialIndex(new KDTree(), 2500);
        }
        if (key == '3') {
            spatialIndex = buildSpatialIndex(new RTree(), 2500);
        }
    }

    private SpatialIndex buildSpatialIndex(SpatialIndex spatialIndex, int numPoints) {
        long seed = System.currentTimeMillis();
        noiseSeed(seed);

        float scale = 0.005f; // Lower = smoother gradients

        for (int i = 0; i < numPoints * 2; i++) {
            float seedX = random(width);
            float seedY = random(height);
            float noiseValue = noise(seedX * scale, seedY * scale);

            if (noiseValue > 0.5f) {
                spatialIndex.insert(new Point(seedX, seedY));
            }
        }

        return spatialIndex;
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
