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
        final double MIN_DIST = 0.001f;
        final int MAX_STEPS = 4000;
        final double EPSILON = 0.1f;

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

                            Vec3 normal = object.normal(shadowRay.getPos(), shadowRay.getDir());

                            shadowRay.getPos().add(normal.multiply(EPSILON));

                            Vec3 newDir = new Vec3(light.normal(shadowRay.getPos()));
                            shadowRay.setDir(newDir); // make the ray face the light

                            boolean isShadowed = false;

                            Vec3 startPos = new Vec3(shadowRay.getPos());
                            double distanceToLight = light.distance(shadowRay.getPos());

                            for (int j = 0; j < MAX_STEPS; j++) {
                                shadowRay.tick();

                                Vec3 travel = new Vec3(shadowRay.getPos());
                                travel.subtract(startPos);

                                if (travel.length() >= distanceToLight) {
//                                    System.out.println(light.distance(shadowRay.getPos()));
                                    break;
                                }

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
                                shadowRay.setColor(
                                        new Vec3(shadowRay.getColor()).multiply(
                                                Shader.getBrightness(ray, object, light)
                                        ));

                                goodShadowRays.add(shadowRay);
                            }
                        }

                        // light logic ------------------------------------

                        Vec3 ambient = new Vec3(object.getColor()).multiply(0.2);

                        finalColor = new Vec3(ambient);

                        if (!goodShadowRays.isEmpty()) {
                            Vec3 diffuseColor = Shader.calcColor(object, goodShadowRays);
                            finalColor.add(diffuseColor);

                            finalColor.x = Math.min(255, finalColor.x);
                            finalColor.y = Math.min(255, finalColor.y);
                            finalColor.z = Math.min(255, finalColor.z);
                        }

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
