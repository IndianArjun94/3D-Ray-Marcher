package game;

import java.util.List;

public class PresetScenes {
    public static void setScene(int scene, List<SceneObject> sceneObjects, List<SceneLight> sceneLights) {
        if (scene == 1) {

            sceneObjects.add(new Plane(
                    new Vec3(-4.5, -3.0, 5.0),   // Front Left
                    new Vec3(4.5, -3.0, 5.0),    // Front Right
                    new Vec3(4.5, -3.0, -7.0),   // Back Right
                    new Vec3(-4.5, -3.0, -7.0),  // Back Left
                    new Vec3(220, 220, 220), 0.3, 0.7
            ));

            sceneObjects.add(new Plane(
                    new Vec3(4.5, -3.0, -7.0),   // Bottom Right
                    new Vec3(4.5, 4.0, -7.0),    // Top Right
                    new Vec3(-4.5, 4.0, -7.0),   // Top Left
                    new Vec3(-4.5, -3.0, -7.0),  // Bottom Left
                    new Vec3(160, 160, 160), 0.3, 0.5
            ));

            sceneObjects.add(new Plane(
                    new Vec3(-4.5, -3.0, -7.0),  // Bottom Back
                    new Vec3(-4.5, 4.0, -7.0),   // Top Back
                    new Vec3(-4.5, 4.0, 5.0),    // Top Front
                    new Vec3(-4.5, -3.0, 5.0),   // Bottom Front
                    new Vec3(255, 65, 65), 0.7, 0.4
            ));

            sceneObjects.add(new Plane(
                    new Vec3(4.5, -3.0, 5.0),    // Bottom Front
                    new Vec3(4.5, 4.0, 5.0),     // Top Front
                    new Vec3(4.5, 4.0, -7.0),    // Top Back
                    new Vec3(4.5, -3.0, -7.0),   // Bottom Back
                    new Vec3(65, 255, 65), 0.7, 0.4
            ));

            sceneObjects.add(new Plane(
                    new Vec3(-4.5, 4.0, -7.0),   // Back Left
                    new Vec3(4.5, 4.0, -7.0),    // Back Right
                    new Vec3(4.5, 4.0, 5.0),     // Front Right
                    new Vec3(-4.5, 4.0, 5.0),    // Front Left
                    new Vec3(100, 100, 100), 0.7, 0.9
            ));

            sceneObjects.add(new Sphere(new Vec3(0.0, -1.0, -4.2), 1.1, new Vec3(255, 255, 255), 1, 0.3));

            sceneObjects.add(new Sphere(new Vec3(-2.0, -2.2, -3.5), 0.5, new Vec3(255, 165, 0), 0.2, 0.8)); // Glossy Orange (Left)
            sceneObjects.add(new Sphere(new Vec3(2.0, -2.2, -3.5), 0.5, new Vec3(0, 191, 255), 0.5, 0.6));  // Glossy Deep Blue (Right)

            sceneObjects.add(new Sphere(new Vec3(-1.8, 1.3, -4.8), 0.7, new Vec3(238, 130, 238), 0, 0.4)); // Violet (Upper Left)
            sceneObjects.add(new Sphere(new Vec3(1.8, 1.3, -4.8), 0.7, new Vec3(255, 255, 0), 0.5, 0.2));   // Yellow (Upper Right)

            sceneLights.add(new PointLight(new Vec3(0.0, 3.8, -4.5), new Vec3(255, 255, 255), 0.75));
        } else if (scene == 2) {

            sceneObjects.add(new Plane(
                    new Vec3(0, -0.1, -7.0),   // Front Left
                    new Vec3(4.5, -0.1, -1.0),    // Front Right
                    new Vec3(0, -0.1, 5.0),   // Back Right
                    new Vec3(-4.5, -0.1, -1.0),  // Back Left
                    new Vec3(220, 220, 220), 0.9, 0.15
            ));

            sceneObjects.add(new Sphere(
                    new Vec3(-1.5, 0.5, -3.0),   // Center position
                    0.5,                          // Radius
                    new Vec3(255, 255, 0),      // Color
                    0.8, 0.2                      // Fully metallic, 0 roughness
            ));

            sceneObjects.add(new Sphere(
                    new Vec3(0.0, 0.5, -3.0),     // Center position (Pushed forward on Z)
                    0.5,                          // Radius
                    new Vec3(0, 255, 255),      // Color
                    0.8, 0.2                      // Fully metallic, 0 roughness
            ));

            sceneObjects.add(new Sphere(
                    new Vec3(1.5, 0.5, -3.0),    // Center position
                    0.5,                          // Radius
                    new Vec3(255, 0, 255),      // Color
                    0.8, 0.2                      // Fully metallic, 0 roughness
            ));

            sceneLights.add(new PointLight(new Vec3(0.0, 2, -2), new Vec3(255, 255, 255), 1));
        }
    }
}
