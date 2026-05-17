package game;

import java.util.ArrayList;

public class SceneManager implements Runnable {

    public Window window;
    public Ray[] rays;
    public ArrayList<SceneObject> sceneObjects = new ArrayList<>();
    public ArrayList<SceneLight> sceneLights = new ArrayList<>();
    public Thread thread;

    public SceneManager(Window window) {
        this.window = window;
        this.rays = new Ray[window.WIDTH * window.HEIGHT];
        for (int x = 0; x < window.WIDTH; x++) {
            for (int y = 0; y < window.HEIGHT; y++) {
                this.rays[x + y * window.WIDTH] = new Ray(x, y, window.WIDTH, window.HEIGHT, window.FOV, new Vec3(255, 255, 255));
            }
        }
        this.sceneObjects.add(new Sphere(new Vec3(0, 0, -2), 0.5, new Vec3(255, 100, 100), 0.5));
        this.sceneObjects.add(new Sphere(new Vec3(-1, -0.5, -2), 0.5, new Vec3(100, 100, 255), 0.5));
//        this.sceneObjects.add(new Plane(new Vec3(0, -1, 0), new Vec3(0, 1, 0), new Vec3(255, 255, 255), 0));
        this.sceneLights.add(new LightPoint(new Vec3(-2, 2, -2), new Vec3(255, 255, 255), 1));
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

            boolean hit = false;

            for (int i = 0; i < 2000 && !hit; i++) { // max steps

                ray.tick();

                for (SceneObject object : sceneObjects) {
                    if (object.distance(ray.getPos()) < MIN_DIST) { // replace to loop through all scene objects
//                        window.innerGameRenderer.setPixel((int) ray.px, (int) ray.py, new Vec3((int) Math.abs((ray.pos.z + 1.9) * 255), (int) Math.abs((ray.pos.z + 1.9) * 255), (int) Math.abs((ray.pos.z + 1.9) * 255)));
                        ray.reflect(object);
                        hit = true;

                        for (SceneLight light : sceneLights) {
                            ray.updateLight(object, light);
                        }

                        break;
                    }
                }
            }

            if (hit) {
                System.out.println("hit!");
                window.innerGameRenderer.setPixel((int) ray.getPx(), (int) ray.getPy(), ray.getColor());
            } else {
                System.out.println(rayCounter);
            }

            rayCounter++;
        }
    }
}
