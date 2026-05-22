package game;

import java.util.ArrayList;
import java.util.Random;

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

//// 1. The Environment (Floor and Back Wall)
//        this.sceneObjects.add(new Plane(new Vec3(0, -2.5, 0), new Vec3(0, 1, 0), new Vec3(200, 200, 200), 0));  // Light Gray Floor
//        this.sceneObjects.add(new Plane(new Vec3(0, 0, -5.5), new Vec3(0, 0, 1), new Vec3(150, 150, 150), 0));  // Matte Back Wall
//
//// 2. The Foreground Elements (Staggered Spheres)
//        this.sceneObjects.add(new Sphere(new Vec3(-1.2, -0.5, -4.0), 1.0, new Vec3(255, 60, 60), 0.0));   // Large, Shiny Crimson Sphere (Left/Deep)
//        this.sceneObjects.add(new Sphere(new Vec3(1.1, -0.8, -3.2), 0.7, new Vec3(50, 180, 255), 0.0));  // Medium, Semi-Gloss Cyan Sphere (Right/Close)
//        this.sceneObjects.add(new Sphere(new Vec3(0.0, 0.8, -2.8), 0.45, new Vec3(255, 215, 0), 0.3));   // Small, Pure Mirror Gold Sphere (Center/High)
//
//// 3. The Dual Light Rig (Cross-Lighting Setup)
//        this.sceneLights.add(new LightPoint(new Vec3(-3.0, 3.0, -1.0), new Vec3(255, 255, 255), 1.0)); // Main Key Light (Top Left, Front)
//        this.sceneLights.add(new LightPoint(new Vec3(3.0, 1.0, -4.5), new Vec3(255, 255, 255), 0.8));  // Rim/Fill Light (Right, Deep)
////         --- 1. THE FIVE-SIDED ENCLOSURE (Walls, Floor, Ceiling) ---
// Floor (Light Gray)
        this.sceneObjects.add(new Plane(new Vec3(0, -3.0, 0), new Vec3(0, 1, 0), new Vec3(220, 220, 220), 0.1));
// Back Wall (Medium Gray)
        this.sceneObjects.add(new Plane(new Vec3(0, 0, -7.0), new Vec3(0, 0, 1), new Vec3(160, 160, 160), 0.2));
// Left Wall (Distinct Matte Crimson Red - showcases colored bounces later)
        this.sceneObjects.add(new Plane(new Vec3(-4.5, 0, 0), new Vec3(1, 0, 0), new Vec3(255, 65, 65), 0.3));
// Right Wall (Distinct Matte Forest Green)
        this.sceneObjects.add(new Plane(new Vec3(4.5, 0, 0), new Vec3(-1, 0, 0), new Vec3(65, 255, 65), 0.2));
// Ceiling (Dark Gray)
        this.sceneObjects.add(new Plane(new Vec3(0, 4.0, 0), new Vec3(0, -1, 0), new Vec3(100, 100, 100), 0.1));

// --- 2. THE SEVEN-SPHERE GEOMETRIC VARIETY ---
// Central Anchor: Large Perfect Mirror Sphere
        this.sceneObjects.add(new Sphere(new Vec3(0.0, -1.0, -4.2), 1.1, new Vec3(255, 255, 255), 0.8));

// Low Foreground Pillars: Small flanking glossy spheres
        this.sceneObjects.add(new Sphere(new Vec3(-2.0, -2.2, -3.5), 0.5, new Vec3(255, 165, 0), new Random().nextDouble(0, 0.5))); // Glossy Orange (Left)
        this.sceneObjects.add(new Sphere(new Vec3(2.0, -2.2, -3.5), 0.5, new Vec3(0, 191, 255), new Random().nextDouble(0, 1)));  // Glossy Deep Blue (Right)

// Mid-Ground Floating Elements: Medium semi-gloss spheres hovering high
        this.sceneObjects.add(new Sphere(new Vec3(-1.8, 1.3, -4.8), 0.7, new Vec3(238, 130, 238), new Random().nextDouble(0, 1))); // Violet (Upper Left)
        this.sceneObjects.add(new Sphere(new Vec3(1.8, 1.3, -4.8), 0.7, new Vec3(255, 255, 0), new Random().nextDouble(0, 0.5)));   // Yellow (Upper Right)

// Deep Background Sentinels: Larger matte spheres tucked into the back corners
        this.sceneObjects.add(new Sphere(new Vec3(-3.0, -0.8, -6.0), 0.8, new Vec3(0, 255, 255), new Random().nextDouble(0, 1)));   // Cyan (Deep Back Left)
        this.sceneObjects.add(new Sphere(new Vec3(3.0, -0.8, -6.0), 0.8, new Vec3(255, 20, 147), new Random().nextDouble(0, 0.5)));  // Pink (Deep Back Right)

// --- 3. THE THREE-WAY ADDITIVE LIGHT RIG ---
// Left Key Light (Imbues a cool cyan cast into left-side shading)
        this.sceneLights.add(new LightPoint(new Vec3(-3.5, 3.0, -2.5), new Vec3(150, 255, 255), 0.9));
// Right Key Light (Imbues a warm magenta/red cast into right-side shading)
        this.sceneLights.add(new LightPoint(new Vec3(3.5, 3.0, -2.5), new Vec3(255, 150, 255), 0.9));
// High Overhead Center Fill (Crisp white light casting baseline geometry silhouettes)
        this.sceneLights.add(new LightPoint(new Vec3(0.0, 3.8, -4.5), new Vec3(255, 255, 255), 0.5));

//        this.sceneLights.add(new LightPoint(new Vec3(0, 2, 0), new Vec3(255, 255, 255), 0.5));
//        this.sceneObjects.add(new Plane(new Vec3(0, 0, -5), new Vec3(0, 0, 1), new Vec3(0, 0, 255), 1));  // Light Gray Floor
//        this.sceneObjects.add(new Sphere(new Vec3(-1, 0, -1), 0.8, new Vec3(0, 0, 255), 0.75));   // Cyan (Deep Back Left)
//        this.sceneObjects.add(new Sphere(new Vec3(1, 0, -1), 0.8, new Vec3(255, 0, 0), 0.75));   // Cyan (Deep Back Left)

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
            Vec3 finalColor = null;

            for (int i = 0; i < MAX_STEPS && !hit; i++) { // max steps

                ray.tick();

                for (SceneObject object : sceneObjects) {
                    if (object.distance(ray.getPos()) < MIN_DIST) { // replace to loop through all scene objects
                        hit = true;

                        finalColor = runReflectionAndShadowRays(
                                ray,
                                object,
                                sceneObjects,
                                sceneLights);

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

        System.out.println("Rays Created: " + rayCounter);
    }
}
