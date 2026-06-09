package game;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static game.Shader.*;

public class SceneManager implements Runnable {

    public Window window;
    //    public ArrayList<Ray> primaryRays = new ArrayList<>();
    public ArrayList<SceneObject> sceneObjects = new ArrayList<>();
    public ArrayList<SceneLight> sceneLights = new ArrayList<>();
    public Thread thread;

    public SceneManager(Window window) {
        this.window = window;
//        for (int y = 0; y < window.HEIGHT; y++) {
//            for (int x = 0; x < window.WIDTH; x++) {
//                this.primaryRays.add(new Ray(x, y, window.WIDTH, window.HEIGHT, window.FOV, new Vec3(255, 255, 255)));
//            }
//        }

// --- 1. THE FINITE ROOM QUADS (CCW Winding Order) ---

// Floor (Light Gray) - Normal points +Y (Up)
        this.sceneObjects.add(new Plane(
                new Vec3(-4.5, -3.0, 5.0),   // Front Left
                new Vec3(4.5, -3.0, 5.0),    // Front Right
                new Vec3(4.5, -3.0, -7.0),   // Back Right
                new Vec3(-4.5, -3.0, -7.0),  // Back Left
                new Vec3(220, 220, 220), 0.3, 0.7
        ));

// Back Wall (Medium Gray) - Normal points +Z (Forward)
        this.sceneObjects.add(new Plane(
                new Vec3(4.5, -3.0, -7.0),   // Bottom Right
                new Vec3(4.5, 4.0, -7.0),    // Top Right
                new Vec3(-4.5, 4.0, -7.0),   // Top Left
                new Vec3(-4.5, -3.0, -7.0),  // Bottom Left
                new Vec3(160, 160, 160), 0.3, 0.5
        ));

// Left Wall (Distinct Matte Crimson Red) - Normal points +X (Right)
        this.sceneObjects.add(new Plane(
                new Vec3(-4.5, -3.0, -7.0),  // Bottom Back
                new Vec3(-4.5, 4.0, -7.0),   // Top Back
                new Vec3(-4.5, 4.0, 5.0),    // Top Front
                new Vec3(-4.5, -3.0, 5.0),   // Bottom Front
                new Vec3(255, 65, 65), 0.4, 0.8
        ));

// Right Wall (Distinct Matte Forest Green) - Normal points -X (Left)
        this.sceneObjects.add(new Plane(
                new Vec3(4.5, -3.0, 5.0),    // Bottom Front
                new Vec3(4.5, 4.0, 5.0),     // Top Front
                new Vec3(4.5, 4.0, -7.0),    // Top Back
                new Vec3(4.5, -3.0, -7.0),   // Bottom Back
                new Vec3(65, 255, 65), 0.2, 0.6
        ));

// Ceiling (Dark Gray) - Normal points -Y (Down)
        this.sceneObjects.add(new Plane(
                new Vec3(-4.5, 4.0, -7.0),   // Back Left
                new Vec3(4.5, 4.0, -7.0),    // Back Right
                new Vec3(4.5, 4.0, 5.0),     // Front Right
                new Vec3(-4.5, 4.0, 5.0),    // Front Left
                new Vec3(100, 100, 100), 0.3, 0.9
        ));

// --- 2. THE SEVEN-SPHERE GEOMETRIC VARIETY ---
// Central Anchor: Large Perfect Mirror Sphere
        this.sceneObjects.add(new Sphere(new Vec3(0.0, -1.0, -4.2), 1.1, new Vec3(255, 255, 255), 1, 0.3));

// Low Foreground Pillars: Small flanking glossy spheres
        this.sceneObjects.add(new Sphere(new Vec3(-2.0, -2.2, -3.5), 0.5, new Vec3(255, 165, 0), 0.2, 0.8)); // Glossy Orange (Left)
        this.sceneObjects.add(new Sphere(new Vec3(2.0, -2.2, -3.5), 0.5, new Vec3(0, 191, 255), 0.5, 0.6));  // Glossy Deep Blue (Right)

// Mid-Ground Floating Elements: Medium semi-gloss spheres hovering high
        this.sceneObjects.add(new Sphere(new Vec3(-1.8, 1.3, -4.8), 0.7, new Vec3(238, 130, 238), 0, 0.4)); // Violet (Upper Left)
        this.sceneObjects.add(new Sphere(new Vec3(1.8, 1.3, -4.8), 0.7, new Vec3(255, 255, 0), 0.5, 0.2));   // Yellow (Upper Right)

// Deep Background Sentinels: Larger matte spheres tucked into the back corners
//        this.sceneObjects.add(new Sphere(new Vec3(-3.0, -0.8, -6.0), 0.8, new Vec3(0, 255, 255), new Random().nextDouble(0, 1)));   // Cyan (Deep Back Left)
//        this.sceneObjects.add(new Sphere(new Vec3(3.0, -0.8, -6.0), 0.8, new Vec3(255, 20, 147), new Random().nextDouble(0, 0.5)));  // Pink (Deep Back Right)

// --- 3. THE THREE-WAY ADDITIVE LIGHT RIG ---
// Left Key Light (Imbues a cool cyan cast into left-side shading)
//        this.sceneLights.add(new PointLight(new Vec3(-3.5, 3.0, -2.5), new Vec3(150, 255, 255), 0.9));
// Right Key Light (Imbues a warm magenta/red cast into right-side shading)
//        this.sceneLights.add(new PointLight(new Vec3(3.5, 3.0, -2.5), new Vec3(255, 150, 255), 0.9));
// High Overhead Center Fill (Crisp white light casting baseline geometry silhouettes)
        this.sceneLights.add(new PointLight(new Vec3(0.0, 3.8, -4.5), new Vec3(255, 255, 255), 0.75));


    }

    public void start() {
        thread = new Thread(this, "rayManager");
        thread.setPriority(10);
        thread.start();
    }

    public void run() {
        final int GRID_SAMPLES = 16; // per pixel
        final int SAMPLES_PER_PIXEL = GRID_SAMPLES * GRID_SAMPLES;


        java.util.stream.IntStream.range(0, window.WIDTH * window.HEIGHT).parallel().forEach(pixel -> {
            Random random = ThreadLocalRandom.current();

            int y = Math.floorDiv(pixel, window.WIDTH);
            int x = pixel-y*window.WIDTH;

            Ray ray = new Ray(x, y, window.WIDTH, window.HEIGHT, window.FOV, new Vec3(255, 255, 255));

            Vec3 accumulatedColor = new Vec3(0, 0, 0);

            for (int gx = 0; gx < GRID_SAMPLES; gx++) {
                for (int gy = 0; gy < GRID_SAMPLES; gy++) {

                    double cellOffsetX = (gx + random.nextDouble(0, 1)) / GRID_SAMPLES;
                    double cellOffsetY = (gy + random.nextDouble(0, 1)) / GRID_SAMPLES;

                    double jitteredX = x + cellOffsetX;
                    double jitteredY = y + cellOffsetY;

                    accumulatedColor.add(findColor(new Ray(jitteredX, jitteredY, window.WIDTH, window.HEIGHT, window.FOV, new Vec3(ray.getColor())), sceneObjects, sceneLights));
                    rayCounter.incrementAndGet();
                }
            }

            accumulatedColor.divide(SAMPLES_PER_PIXEL); // keep the range to 0-255

            window.innerGameRenderer.setPixel((int) ray.getPx(), (int) ray.getPy(), accumulatedColor);

            if (ray.getPx() == 0) {
                window.innerGameRenderer.setPixel(0, (int) ray.getPy(), new Vec3(255, 255, 255));
            }
//            }
        });

        System.out.println("Rays Created: " + rayCounter);
    }
}
