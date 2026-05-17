package game;

import java.util.ArrayList;

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
        this.sceneObjects.add(new Sphere(new Vec3(0, 0, -2), 0.5, new Vec3(255, 100, 100), 0.5));
        this.sceneObjects.add(new Sphere(new Vec3(-1, 0, -2), 0.5, new Vec3(100, 100, 255), 0.5));
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
        final int MAX_STEPS = 2000;

        int rayCounter = 0;
        for (Ray ray : primaryRays) {

            boolean hit = false;
            Vec3 finalColor = new Vec3(0,0,0);

            for (int i = 0; i < MAX_STEPS && !hit; i++) { // max steps

                ray.tick();

                for (SceneObject object : sceneObjects) {
                    if (object.distance(ray.getPos()) < MIN_DIST) { // replace to loop through all scene objects
                        ray.reflect(object);
                        hit = true;

                        // shadow ray logic ------------------------------------

                        ArrayList<Ray> goodShadowRays = new ArrayList<>(); // shadow rays that aren't blocked

                        for (SceneLight light : sceneLights) { // create shadow rays
                            Ray shadowRay = new Ray(ray);
                            shadowRay.setColor(light.getColor());

                            while (object.distance(shadowRay.getPos()) <= MIN_DIST) { // make sure the shadow ray is not already touching the object
                                shadowRay.tick();
                            }

                            Vec3 newDir = new Vec3(light.normal(shadowRay.getPos()));
                            shadowRay.setDir(newDir); // make the ray face the light

                            boolean isShadowed = false;

                            for (int j = i; j < MAX_STEPS; j++) {
                                shadowRay.tick();

                                for (SceneObject shadowObject : sceneObjects) {
                                    if (shadowObject.distance(shadowRay.getPos()) <= MIN_DIST) {
                                        isShadowed = true;
                                        break;
                                    }
                                }

                                if (isShadowed) {
                                    break;
                                }
                            }

                            if (!isShadowed) {
//                                System.out.println("shadow ray: r: " + shadowRay.getColor().x + ", g: " + shadowRay.getColor().y + ", b: " + shadowRay.getColor().z);

                                shadowRay.setColor(
                                        new Vec3(shadowRay.getColor()).multiply(
                                                Shader.getBrightness(ray, object, light)
                                        ));

//                                System.out.println(Shader.getBrightness(ray, object, light));
                                goodShadowRays.add(shadowRay);
                            }
                        }

                        // light logic ------------------------------------

                        if (!goodShadowRays.isEmpty()) {
                            finalColor = Shader.calcColor(object, goodShadowRays);
                        }

                        break;
                    }
                }
            }

            if (hit) {
//                System.out.println("hit | " + "r: " + finalColor.x + ", g: " + finalColor.y + ", b: " + finalColor.z);

                window.innerGameRenderer.setPixel((int) ray.getPx(), (int) ray.getPy(), finalColor);
            } else {
//                System.out.println(rayCounter);
            }

            if (ray.getPx() == 0) {
                window.innerGameRenderer.setPixel(0, (int) ray.getPy(), new Vec3(255, 255, 255));
            }

            rayCounter++;
        }
    }
}
