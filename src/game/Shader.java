package game;

import java.util.ArrayList;
import java.util.List;

public class Shader {

    public static final double MIN_DIST = 0.001f;
    public static final int MAX_STEPS = 4000;
    public static final double EPSILON = 0.01f;
    public static int rayCounter = 0;


    private static double getBrightness(Ray primaryRay, SceneObject object, SceneLight light) {
        Vec3 N = object.normal(primaryRay.getPos(), primaryRay.getDir()); // surface normal
        Vec3 L = light.normal(primaryRay.getPos()); // light normal

        double I = Math.max(0, N.dot(L));

        return I * light.getBrightness();
    }

    private static Vec3 calcLocalColor(SceneObject object, List<Ray> shadowRays) { // calculate from shadow rays
        Vec3 ambient = new Vec3(object.getColor()).multiply(0.1);

        Vec3 finalColor = new Vec3(object.getColor());
        Vec3 totalLight = new Vec3(0,0,0);


        for (int i = 0; i < shadowRays.size(); i++) {
            totalLight.add(new Vec3(shadowRays.get(i).getColor()));
        }

        totalLight.divide(255);

        finalColor.multiply(totalLight);

        finalColor.add(ambient);

        finalColor.x = Math.min(255, finalColor.x);
        finalColor.y = Math.min(255, finalColor.y);
        finalColor.z = Math.min(255, finalColor.z);

        return finalColor;
    }

    public static Vec3 localColor(Ray ray, SceneObject object, List<SceneObject> sceneObjects, List<SceneLight> sceneLights) {
        List<Ray> goodShadowRays = runShadowRays(
                ray,
                object,
                sceneObjects,
                sceneLights
        );

        return calcLocalColor(object, goodShadowRays);

    }

    public static List<Ray> runShadowRays(Ray ray, SceneObject object, List<SceneObject> sceneObjects, List<SceneLight> sceneLights) {
        ArrayList<Ray> goodShadowRays = new ArrayList<>();

        for (SceneLight light : sceneLights) { // create shadow rays
            Ray shadowRay = new Ray(ray);
            rayCounter++;
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

        return goodShadowRays;
    }
}
