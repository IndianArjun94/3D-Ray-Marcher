package game;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

import static game.Shader.*;

public class SceneManager implements Runnable {

    public Window window;
    public ArrayList<SceneObject> sceneObjects = new ArrayList<>();
    public ArrayList<SceneLight> sceneLights = new ArrayList<>();
    public Thread thread;

    public SceneManager(Window window) {
        this.window = window;

        PresetScenes.setScene(2, sceneObjects, sceneLights);

    }

    public void start() {
        thread = new Thread(this, "rayManager");
        thread.setPriority(10);
        thread.start();
    }

    public void run() {
        final int GRID_SAMPLES = 16; // per pixel
        final int SAMPLES_PER_PIXEL = GRID_SAMPLES * GRID_SAMPLES;
        final double INVERTED_GRID_SAMPLES = (double) 1 /GRID_SAMPLES;
        final double INVERTED_SAMPLES_PER_PIXEL = (double) 1/(SAMPLES_PER_PIXEL);

        ForkJoinPool customThreadPool = new ForkJoinPool(24);

        long time = System.nanoTime();

        customThreadPool.submit(() -> {

            java.util.stream.IntStream.range(0, window.WIDTH * window.HEIGHT).parallel().forEach(pixel -> {
                Random random = ThreadLocalRandom.current();

                int localRayCount = 0;

                int y = Math.floorDiv(pixel, window.WIDTH);
                int x = pixel - y * window.WIDTH;

                Ray ray = new Ray(x, y, window.WIDTH, window.HEIGHT, window.FOV, new Vec3(255, 255, 255));

                Vec3 accumulatedColor = new Vec3(0, 0, 0);

                for (int gx = 0; gx < GRID_SAMPLES; gx++) {
                    for (int gy = 0; gy < GRID_SAMPLES; gy++) {

                        double cellOffsetX = (gx + random.nextDouble(0, 1)) * INVERTED_GRID_SAMPLES;
                        double cellOffsetY = (gy + random.nextDouble(0, 1)) * INVERTED_GRID_SAMPLES;

                        double jitteredX = x + cellOffsetX;
                        double jitteredY = y + cellOffsetY;

                        accumulatedColor.add(findColor(ray.reset(jitteredX,jitteredY, window.WIDTH, window.HEIGHT, window.FOV, ray.getColor()), sceneObjects, sceneLights));
                        localRayCount++;
                    }
                }

                accumulatedColor.multiply(INVERTED_SAMPLES_PER_PIXEL); // keep the range to 0-255

                window.innerGameRenderer.setPixel(x, y, accumulatedColor);

                if (x == 0) {
                    window.innerGameRenderer.setPixel(0, y, new Vec3(255, 255, 255));
                }

                rayCounter.addAndGet(localRayCount);
            });
        }).join();

        System.out.println("Rays Created: " + rayCounter + ", in " + (float)((System.nanoTime()-time)/1_000_000_0)/100 + " seconds");
    }
}
