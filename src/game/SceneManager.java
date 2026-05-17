package game;

import java.util.ArrayList;

public class SceneManager implements Runnable {

    public Window window;
    public Ray[] rays;
    public ArrayList<SceneObject> sceneObjects = new ArrayList<>();
    public Thread thread;
    public boolean running = true;

    public SceneManager(Window window) {
        this.window = window;
        this.rays = new Ray[window.WIDTH * window.HEIGHT];
        for (int x = 0; x < window.WIDTH; x++) {
            for (int y = 0; y < window.HEIGHT; y++) {
                this.rays[x + y * window.WIDTH] = new Ray(x, y, window.WIDTH, window.HEIGHT, window.FOV);
            }
        }
        this.sceneObjects.add(new Sphere(new Vec3(0, 0, -2), 0.5, new Vec3(255, 100, 100), 0));
    }

    public void start() {
        thread = new Thread(this, "rayManager");
        thread.setPriority(1);
        thread.start();
    }

    public void run() {
        final float MIN_DIST = 0.001f;

        int rayCounter = 0;
        for (Ray ray : rays) {

            for (int i = 0; i < 2000; i++) { // max steps

                ray.tick();

                for (SceneObject obj : sceneObjects) {
                    if (obj.distance(ray.pos) < MIN_DIST) { // replace to loop through all scene objects
                        window.innerGameRenderer.setPixel((int) ray.px, (int) ray.py, new Vec3((int) Math.abs((ray.pos.z + 1.9) * 255), (int) Math.abs((ray.pos.z + 1.9) * 255), (int) Math.abs((ray.pos.z + 1.9) * 255)));
//                        ray.reflect(obj.normal(ray.pos));
                    }
                }

            }

            rayCounter++;
        }
    }
}
