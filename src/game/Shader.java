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



    public static Vec3 runReflectionAndShadowRays(Ray ray, SceneObject object, List<SceneObject> sceneObjects, List<SceneLight> sceneLights) {
        Vec3 finalColor;

        Ray reflectionRay = new Ray(ray);
        reflectionRay.setColor(localColor(ray, object, sceneObjects, sceneLights));

        Vec3 normal = object.normal(reflectionRay.getPos(), reflectionRay.getDir());
        reflectionRay.getPos().add(normal.multiply(EPSILON));

        reflectionRay.reflect(object);

        double currentReflectivity = object.getReflectivity();


        for (int j = 0; j < 10; j++) {

            boolean reflected = false;

            for (int k = 0; k < MAX_STEPS; k++) {
                reflectionRay.tick();

                for (SceneObject reflectionObject : sceneObjects) {
                    if (reflectionObject.distance(reflectionRay.getPos()) < MIN_DIST) {
                        Vec3 newRayColor = new Vec3(
                                Shader.localColor(
                                        reflectionRay,
                                        reflectionObject,
                                        sceneObjects,
                                        sceneLights
                                ));

                        newRayColor.multiply(currentReflectivity); // [REFLECTION] base color of HIT object (HIGHER the reflectivity, MORE of this)
//                                        System.out.print(newRayColor.x + " | ");
                        newRayColor.add(new Vec3(reflectionRay.getColor()).multiply(1-currentReflectivity)); // [BASE COLOR] base color of CURRENT object (HIGHER the reflectivity, LESS of this)
//                                        System.out.println(newRayColor.x);

                        reflectionRay.setColor(newRayColor);

                        currentReflectivity *= reflectionObject.getReflectivity();

                        Vec3 reflectionNormal = reflectionObject.normal(reflectionRay.getPos(), reflectionRay.getDir());
                        reflectionRay.getPos().add(reflectionNormal.multiply(EPSILON));

                        reflectionRay.reflect(reflectionObject);


                        reflected = true;
                        break;
                    }
                }

                if (reflected) {
                    break;
                }
            }

            if (!reflected) {
                break;
            }
        }

        finalColor = reflectionRay.getColor();

        return finalColor;
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
