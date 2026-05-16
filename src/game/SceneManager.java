package game;

import static game.Window.RGB.rgb;

public class SceneManager implements Runnable {

    private Window window;
    private Ray[] rays;
    private SceneObject[] sceneObjects;
    private Thread thread;
    private boolean running = true;

    public SceneManager(Window window) {
        this.window = window;
        this.rays = new Ray[window.WIDTH * window.HEIGHT];
        for (int x = 0; x < window.WIDTH; x++) {
            for (int y = 0; y < window.HEIGHT; y++) {
                this.rays[x + y * window.WIDTH] = new Ray(x, y, window.WIDTH, window.HEIGHT, window.FOV);
            }
        }
        this.sceneObjects = new SceneObject[100];
        this.sceneObjects[0] = new Sphere(0, 0, -2, 0.5);
    }

    public void start() {
        thread = new Thread(this, "rayManager");
        thread.setPriority(1);
        thread.start();
    }

    public void run() {
        final float size = 0.5f;
        final float MIN_DIST = 0.001f;

        while (running) {

            for (Ray ray : rays) {

                ray.resetPos();

                for (int i = 0; i < 2000; i++) { // max steps

                    ray.tick();

                    if (sceneObjects[0].distance(ray.x, ray.y, ray.z) < MIN_DIST) {
                        window.innerGameRenderer.setPixel((int)ray.px, (int)ray.py, rgb(255, 0, 0));
                    }

//                    if (ray.x <= size && ray.x >= -size &&
//                            ray.y <= size && ray.y >= -size &&
//                            ray.z <= -2 && ray.z >= -4) {
//
//                        window.innerGameRenderer.setPixel((int)ray.px, (int)ray.py,
//                                rgb((int)(
//                                        Math.sqrt(ray.x*ray.x + ray.y*ray.y * Math.abs(ray.z*ray.z))*255/ray.stepSize), 0, 255));
//                        break;
//                    }

                    if (ray.z < -10) {
                        break;
                    }
                }
            }

        }
    }
}
