package game;

import java.util.ArrayList;

import static game.Shader.*;

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
        // --- CLEAR THE OLD OBJECTS AND LIGHTS, THEN ADD THESE ---

// 1. The Environment (Floor and Back Wall)
        this.sceneObjects.add(new Plane(new Vec3(0, -2.5, 0), new Vec3(0, 1, 0), new Vec3(200, 200, 200), 0.2));  // Light Gray Floor
        this.sceneObjects.add(new Plane(new Vec3(0, 0, -5.5), new Vec3(0, 0, 1), new Vec3(150, 150, 150), 0.5));  // Matte Back Wall

// 2. The Foreground Elements (Staggered Spheres)
        this.sceneObjects.add(new Sphere(new Vec3(-1.2, -0.5, -4.0), 1.0, new Vec3(255, 60, 60), 0.1));   // Large, Shiny Crimson Sphere (Left/Deep)
        this.sceneObjects.add(new Sphere(new Vec3(1.1, -0.8, -3.2), 0.7, new Vec3(50, 180, 255), 0.4));  // Medium, Semi-Gloss Cyan Sphere (Right/Close)
        this.sceneObjects.add(new Sphere(new Vec3(0.0, 0.8, -2.8), 0.45, new Vec3(255, 215, 0), 0.0));   // Small, Pure Mirror Gold Sphere (Center/High)

// 3. The Dual Light Rig (Cross-Lighting Setup)
        this.sceneLights.add(new LightPoint(new Vec3(-3.0, 3.0, -1.0), new Vec3(255, 255, 255), 1.0)); // Main Key Light (Top Left, Front)
        this.sceneLights.add(new LightPoint(new Vec3(3.0, 1.0, -4.5), new Vec3(255, 255, 255), 0.8));  // Rim/Fill Light (Right, Deep)
//         --- 1. THE FIVE-SIDED ENCLOSURE (Walls, Floor, Ceiling) ---
//// Floor (Light Gray)
//        this.sceneObjects.add(new Plane(new Vec3(0, -3.0, 0), new Vec3(0, 1, 0), new Vec3(220, 220, 220), 0.2));
//// Back Wall (Medium Gray)
//        this.sceneObjects.add(new Plane(new Vec3(0, 0, -7.0), new Vec3(0, 0, 1), new Vec3(160, 160, 160), 0.5));
//// Left Wall (Distinct Matte Crimson Red - showcases colored bounces later)
//        this.sceneObjects.add(new Plane(new Vec3(-4.5, 0, 0), new Vec3(1, 0, 0), new Vec3(255, 65, 65), 0.6));
//// Right Wall (Distinct Matte Forest Green)
//        this.sceneObjects.add(new Plane(new Vec3(4.5, 0, 0), new Vec3(-1, 0, 0), new Vec3(65, 255, 65), 0.6));
//// Ceiling (Dark Gray)
//        this.sceneObjects.add(new Plane(new Vec3(0, 4.0, 0), new Vec3(0, -1, 0), new Vec3(100, 100, 100), 0.8));
//
//// --- 2. THE SEVEN-SPHERE GEOMETRIC VARIETY ---
//// Central Anchor: Large Perfect Mirror Sphere
//        this.sceneObjects.add(new Sphere(new Vec3(0.0, -1.0, -4.2), 1.1, new Vec3(255, 255, 255), 0.8));
//
//// Low Foreground Pillars: Small flanking glossy spheres
//        this.sceneObjects.add(new Sphere(new Vec3(-2.0, -2.2, -3.5), 0.5, new Vec3(255, 165, 0), 0)); // Glossy Orange (Left)
//        this.sceneObjects.add(new Sphere(new Vec3(2.0, -2.2, -3.5), 0.5, new Vec3(0, 191, 255), 1));  // Glossy Deep Blue (Right)
//
//// Mid-Ground Floating Elements: Medium semi-gloss spheres hovering high
//        this.sceneObjects.add(new Sphere(new Vec3(-1.8, 1.3, -4.8), 0.7, new Vec3(238, 130, 238), 0)); // Violet (Upper Left)
//        this.sceneObjects.add(new Sphere(new Vec3(1.8, 1.3, -4.8), 0.7, new Vec3(255, 255, 0), 1));   // Yellow (Upper Right)
//
//// Deep Background Sentinels: Larger matte spheres tucked into the back corners
//        this.sceneObjects.add(new Sphere(new Vec3(-3.0, -0.8, -6.0), 0.8, new Vec3(0, 255, 255), 0.5));   // Cyan (Deep Back Left)
//        this.sceneObjects.add(new Sphere(new Vec3(3.0, -0.8, -6.0), 0.8, new Vec3(255, 20, 147), 1));  // Pink (Deep Back Right)
//
//// --- 3. THE THREE-WAY ADDITIVE LIGHT RIG ---
//// Left Key Light (Imbues a cool cyan cast into left-side shading)
//        this.sceneLights.add(new LightPoint(new Vec3(-3.5, 3.0, -2.5), new Vec3(150, 255, 255), 0.9));
//// Right Key Light (Imbues a warm magenta/red cast into right-side shading)
//        this.sceneLights.add(new LightPoint(new Vec3(3.5, 3.0, -2.5), new Vec3(255, 150, 255), 0.9));
//// High Overhead Center Fill (Crisp white light casting baseline geometry silhouettes)
//        this.sceneLights.add(new LightPoint(new Vec3(0.0, 3.8, -4.5), new Vec3(255, 255, 255), 0.5));


    }

    public void start() {
        thread = new Thread(this, "rayManager");
        thread.setPriority(1);
        thread.start();
    }

    public void run() {


        for (Ray ray : primaryRays) {
            rayCounter++;

            boolean hit = false;
            Vec3 finalColor = new Vec3(0,0,0);
            Vec3 firstLocalColor = new Vec3(0,0,0); // todo for testing, remove this

            for (int i = 0; i < MAX_STEPS && !hit; i++) { // max steps

                ray.tick();

                for (SceneObject object : sceneObjects) {
                    if (object.distance(ray.getPos()) < MIN_DIST) { // replace to loop through all scene objects
                        hit = true;

                        firstLocalColor = Shader.localColor( // todo add "Vec3" here
                                ray,
                                object,
                                sceneObjects,
                                sceneLights
                        );

                        // reflection ray logic ------------------------------------

                        Ray reflectionRay = new Ray(ray);
                        reflectionRay.reflect(object);

                        Vec3 normal = object.normal(reflectionRay.getPos(), reflectionRay.getDir());
                        reflectionRay.getPos().add(normal.multiply(EPSILON));

                        for (int j = 0; j < 4; j++) {

                            boolean reflected = false;

                            for (int k = 0; k < MAX_STEPS; k++) {
                                reflectionRay.tick();

                                for (SceneObject reflectionObject : sceneObjects) {
                                    if (reflectionObject.distance(reflectionRay.getPos()) < MIN_DIST) {
                                        Vec3 newRayColor = new Vec3(firstLocalColor);
                                        newRayColor.multiply(1-reflectionObject.getReflectivity());
                                        newRayColor.add(new Vec3(reflectionRay.getColor()).multiply(reflectionObject.getReflectivity()));

                                        reflectionRay.setColor(newRayColor);

                                        reflectionRay.reflect(reflectionObject);

                                        Vec3 reflectionNormal = reflectionObject.normal(reflectionRay.getPos(), reflectionRay.getDir());
                                        reflectionRay.getPos().add(reflectionNormal.multiply(EPSILON));

                                        reflected = true;
                                        break;
                                    }
                                }

                                if (reflected) {
                                    break;
                                }
                            }

                            if (!reflected) {
                                if (j == 0) {
                                    reflectionRay.setColor(firstLocalColor);
                                }
                                break;
                            }
                        }

                        finalColor = reflectionRay.getColor();

                        break;
                    }
                }
            }

            if (hit) {
//                window.innerGameRenderer.setPixel((int) ray.getPx(), (int) ray.getPy(), finalColor);
                window.innerGameRenderer.setPixel((int) ray.getPx(), (int) ray.getPy(), finalColor);
            }

            if (ray.getPx() == 0) {
                window.innerGameRenderer.setPixel(0, (int) ray.getPy(), new Vec3(255, 255, 255));
            }
        }

        System.out.println(rayCounter);
    }
}
