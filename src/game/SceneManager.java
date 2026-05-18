package game;

import java.util.ArrayList;
import java.util.List;

import static game.Shader.MAX_STEPS;
import static game.Shader.MIN_DIST;

public class SceneManager implements Runnable {

    public Window window;
    public ArrayList<Ray> primaryRays = new ArrayList<>();
    public ArrayList<SceneObject> sceneObjects = new ArrayList<>();
    public ArrayList<SceneLight> sceneLights = new ArrayList<>();
    public Thread thread;

    public SceneManager(Window window) {
        this.window = window;
        for (int y = 0; y < window.HEIGHT; y++) {
            for (int x = 0; x < window.WIDTH; x++) {
                this.primaryRays.add(new Ray(x, y, window.WIDTH, window.HEIGHT, window.FOV, new Vec3(255, 255, 255)));
            }
        }
        this.sceneObjects.add(new Sphere(new Vec3(0.5, -0.5, -3), 1.3, new Vec3(255, 100, 100), 0.5));
        this.sceneObjects.add(new Sphere(new Vec3(-0.5, 0.5, -1.5), 0.5, new Vec3(100, 100, 255), 0.5));
        this.sceneObjects.add(new Plane(new Vec3(0, -3, 0), new Vec3(0, 1, 0), new Vec3(255, 255, 255), 0));
        this.sceneLights.add(new LightPoint(new Vec3(-1, 1, 0), new Vec3(255, 255, 255), 1));
        this.sceneLights.add(new LightPoint(new Vec3(0, -2.3, 0), new Vec3(255, 255, 255), 1));
    }

    public void start() {
        thread = new Thread(this, "rayManager");
        thread.setPriority(1);
        thread.start();
    }

    public void run() {


        int rayCounter = 0;
        for (Ray ray : primaryRays) {

            boolean hit = false;
            Vec3 finalColor = new Vec3(0,0,0);

            for (int i = 0; i < MAX_STEPS && !hit; i++) { // max steps

                ray.tick();

                for (SceneObject object : sceneObjects) {
                    if (object.distance(ray.getPos()) < MIN_DIST) { // replace to loop through all scene objects
//                        ray.reflect(object);
                        hit = true;

                        // shadow ray logic ------------------------------------

//                        List<Ray> goodShadowRays = Shader.runShadowRays(ray, object, sceneObjects, sceneLights, EPSILON, MIN_DIST, MAX_STEPS);

                        // light logic ------------------------------------

                        finalColor = Shader.localColor(
                                ray,
                                object,
                                sceneObjects,
                                sceneLights
                        );

                        break;
                    }
                }
            }

            if (hit) {
                window.innerGameRenderer.setPixel((int) ray.getPx(), (int) ray.getPy(), finalColor);
            }

            if (ray.getPx() == 0) {
                window.innerGameRenderer.setPixel(0, (int) ray.getPy(), new Vec3(255, 255, 255));
            }

            rayCounter++;
        }
    }
}
