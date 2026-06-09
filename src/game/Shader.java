package game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class Shader {

    public static final double EPSILON = 0.01f;
    public static final double ROUGHNESS_SCALER = 0.4;
    public static AtomicInteger rayCounter = new AtomicInteger(0);
//    Helpers

    private static double getBrightness(Ray primaryRay, SceneObject object, SceneLight light) {
        Vec3 N = object.normal(primaryRay.getPos(), primaryRay.getDir()); // surface normal
        Vec3 L = new Vec3(light.getPos()).subtract(primaryRay.getPos());

        L.normalize();

        double I = Math.max(0, N.dot(L));

        return I * light.getBrightness();
    }

    public static Vec3 localColor(Ray ray, SceneObject object, List<SceneObject> sceneObjects, List<SceneLight> sceneLights) {
        List<Ray> shadowRays = runShadowRays(
                ray,
                object,
                sceneObjects,
                sceneLights
        );

        Vec3 ambient = new Vec3(object.getColor()).multiply(0.1);

        Vec3 finalColor = new Vec3(object.getColor());
        Vec3 totalLight = new Vec3(0, 0, 0);


        for (Ray shadowRay : shadowRays) {
            totalLight.add(new Vec3(shadowRay.getColor()));
        }

        totalLight.divide(255);

        finalColor.multiply(totalLight);

        finalColor.add(ambient);

        finalColor.x = Math.min(255, finalColor.x);
        finalColor.y = Math.min(255, finalColor.y);
        finalColor.z = Math.min(255, finalColor.z);

        return finalColor.multiply(1-object.getMetallic());
    }

    private static List<Ray> runShadowRays(Ray ray, SceneObject object, List<SceneObject> sceneObjects, List<SceneLight> sceneLights) {
        ArrayList<Ray> goodShadowRays = new ArrayList<>();

        for (SceneLight light : sceneLights) { // create shadow rays
            Ray shadowRay = new Ray(ray);
            rayCounter.incrementAndGet();
            shadowRay.setColor(light.getColor());

            Vec3 newDir = new Vec3(light.getPos()).subtract(shadowRay.getPos());
            newDir.normalize();
            shadowRay.setDir(newDir); // make the ray face the light

            shadowRay.getPos().add(new Vec3(newDir).multiply(EPSILON));

            boolean isShadowed = false;

            double distanceToLight = light.distance(shadowRay.getPos());

            for (SceneObject shadowObject : sceneObjects) {
                double t = shadowObject.calculateRayTravel(shadowRay.getPos(), shadowRay.getDir());
                if (t > 0 && t < distanceToLight) {
                    isShadowed = true;
                    break;
                }
            }

            if (!isShadowed) {
                shadowRay.setPos(light.calculateRayTravelPos(shadowRay.getPos(), shadowRay.getDir()));

                shadowRay.setColor(
                        new Vec3(shadowRay.getColor()).multiply(
                                Shader.getBrightness(ray, object, light)
                        ));

                goodShadowRays.add(shadowRay);
            }
        }

        return goodShadowRays;
    }

//    Full Calculations

    public static Vec3 findColor(Ray _ray, List<SceneObject> sceneObjects, List<SceneLight> sceneLights) {
        Vec3 throughput = new Vec3(1, 1, 1); // tint
        Vec3 accumulatedColor = new Vec3(0,0,0); // color

        Ray ray = new Ray(_ray);

        for (int i = 0; i < 10; i++) {
            boolean hit = false;
            double lowest_t = Double.MAX_VALUE;
            SceneObject hitObject = null;

            for (SceneObject object : sceneObjects) {
                double t = object.calculateRayTravel(ray.getPos(), ray.getDir());
                if (t > 0 && t < lowest_t) {
                    lowest_t = t;
                    hitObject = object;
                    hit = true;
                }
            }

            if (!hit) {
                break;
            }

            ray.setPos(hitObject.calculateRayTravelPos(ray.getPos(), ray.getDir()));

            Vec3 localColor = localColor(ray, hitObject, sceneObjects, sceneLights);
            localColor.multiply(throughput);
            accumulatedColor.add(localColor);

            Vec3 dielectricTint = new Vec3(255 * 0.04, 255 * 0.04, 255 * 0.04).multiply(1-hitObject.getMetallic()); // 4% white base
            Vec3 metallicTint = new Vec3(hitObject.getColor()).multiply(hitObject.getMetallic());

            Vec3 combinedTint = new Vec3(dielectricTint).add(metallicTint).divide(255);

            throughput.multiply(combinedTint);

            Vec3 normal = hitObject.normal(ray.getPos(), ray.getDir());
            ray.getPos().add(normal.multiply(EPSILON)); // Prevent getting stuck in the wall
            ray.reflect(hitObject);

            if (hitObject.getRoughness() != 0) {
                Random random = ThreadLocalRandom.current();
                Vec3 perfectDir = ray.getDir();
                double variable = hitObject.getRoughness()*ROUGHNESS_SCALER;

                Vec3 randomized = new Vec3(
                        random.nextDouble(-variable,variable),
                        random.nextDouble(-variable,variable),
                        random.nextDouble(-variable,variable));

                Vec3 roughDir = perfectDir.add(randomized);
                roughDir.normalize();

                ray.setDir(roughDir);
            }

            rayCounter.incrementAndGet();

        }

        return new Vec3(
                Math.min(255, accumulatedColor.x),
                Math.min(255, accumulatedColor.y),
                Math.min(255, accumulatedColor.z));
    }

    @Deprecated
    public static Vec3 runReflectionAndShadowRays(Ray ray, SceneObject object, List<SceneObject> sceneObjects, List<SceneLight> sceneLights) {
        Vec3 finalColor;

        Ray reflectionRay = new Ray(ray);

        Vec3 normal = object.normal(reflectionRay.getPos(), reflectionRay.getDir());
        reflectionRay.getPos().add(normal.multiply(EPSILON));

        reflectionRay.reflect(object);

        double currentReflectivity = object.getMetallic();

        reflectionRay.setColor(localColor(ray, object, sceneObjects, sceneLights).multiply(1 - currentReflectivity));


        for (int j = 0; j < 10; j++) {

            boolean reflected = false;

            double lowest_t = Double.MAX_VALUE;
            SceneObject associatedReflectionObject = null;

            for (SceneObject reflectionObject : sceneObjects) {
                double t = reflectionObject.calculateRayTravel(reflectionRay.getPos(), reflectionRay.getDir());

                if (t > 0 && t < lowest_t) {
                    lowest_t = t;
                    associatedReflectionObject = reflectionObject;
                    reflected = true;
                }

            }

            if (reflected) {
                reflectionRay.setPos(associatedReflectionObject.calculateRayTravelPos(reflectionRay.getPos(), reflectionRay.getDir()));

                Vec3 newRayColor = new Vec3(
                        Shader.localColor(
                                reflectionRay,
                                associatedReflectionObject,
                                sceneObjects,
                                sceneLights
                        ));

                newRayColor.multiply(currentReflectivity * (1 - associatedReflectionObject.getMetallic())); // [REFLECTION] color of HIT object (HIGHER the reflectivity, MORE of this)
//                                        System.out.print(newRayColor.x + " | ");
                newRayColor.add(reflectionRay.getColor()); // [BASE COLOR] base color of CURRENT object (HIGHER the reflectivity, LESS of this)
//                                        System.out.println(newRayColor.x);

                reflectionRay.setColor(newRayColor);

                currentReflectivity *= associatedReflectionObject.getMetallic();

                Vec3 reflectionNormal = associatedReflectionObject.normal(reflectionRay.getPos(), reflectionRay.getDir());
                reflectionRay.getPos().add(reflectionNormal.multiply(EPSILON));

                reflectionRay.reflect(associatedReflectionObject);
            }

            if (!reflected) {
                break;
            }
        }

        finalColor = reflectionRay.getColor();

        return finalColor;
    }

}
