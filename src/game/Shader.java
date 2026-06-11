package game;

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

        double I = Math.max(0, N.dot(L)); // positive, usually < 1

        return I * light.getBrightness();
    }

    public static Vec3 localColor(Ray ray, SceneObject object, List<SceneObject> sceneObjects, List<SceneLight> sceneLights) {
//        List<Ray> shadowRays = runShadowRays(
//                ray,
//                object,
//                sceneObjects,
//                sceneLights
//        );

//        Vec3 ambient = new Vec3(object.getColor()).multiply(0.1);

        double ambientR = object.getColor().x * 0.1;
        double ambientG = object.getColor().y * 0.1;
        double ambientB = object.getColor().z * 0.1;

//        Vec3 finalColor = new Vec3(object.getColor());

        double finalR = object.getColor().x;
        double finalG = object.getColor().y;
        double finalB = object.getColor().z;

        Vec3 totalLight = getLightingFromShadowRays(
                ray,
                object,
                sceneObjects,
                sceneLights
        );

//        finalColor.multiply(totalLight);

        finalR *= totalLight.x;
        finalG *= totalLight.y;
        finalB *= totalLight.z;

        finalR += ambientR;
        finalG += ambientG;
        finalB += ambientB;

        finalR = Math.min(255, finalR);
        finalG = Math.min(255, finalG);
        finalB = Math.min(255, finalB);

        finalR *= 1-object.getMetallic();
        finalG *= 1-object.getMetallic();
        finalB *= 1-object.getMetallic();

        return new Vec3(finalR, finalG, finalB);
    }

    private static Vec3 getLightingFromShadowRays(Ray ray, SceneObject object, List<SceneObject> sceneObjects, List<SceneLight> sceneLights) {

//        Vec3 totalColor = new Vec3(0, 0, 0);

        double totalR = 0;
        double totalG = 0;
        double totalB = 0;

        for (int i = 0; i < sceneLights.size(); i++) {
            SceneLight light = sceneLights.get(i);

            Ray shadowRay = new Ray(ray);
//            shadowRay.setColor(light.getColor());

//            Vec3 newDir = new Vec3(light.getPos()).subtract(shadowRay.getPos());

            double dirX = light.getPos().x - shadowRay.getPos().x;
            double dirY = light.getPos().y - shadowRay.getPos().y;
            double dirZ = light.getPos().z - shadowRay.getPos().z;

//            newDir.normalize();

            double length = Math.sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);

            dirX = dirX / length;
            dirY = dirY / length;
            dirZ = dirZ / length;

//            shadowRay.setDir(newDir); // make the ray face the light

            shadowRay.setDir(dirX, dirY, dirZ);

            Vec3 currentPos = shadowRay.getPos();

            shadowRay.setPos(currentPos.x + dirX * EPSILON, currentPos.y + dirY * EPSILON, currentPos.z + dirZ * EPSILON);

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

//                shadowRay.setColor(
//                        new Vec3(shadowRay.getColor()).multiply(
//                                Shader.getBrightness(ray, object, light)
//                        ));

                double brightness = Shader.getBrightness(ray, object, light);

                totalR += light.getColor().x * brightness;
                totalG += light.getColor().y * brightness;
                totalB += light.getColor().z * brightness;

//                totalColor.add(new Vec3(shadowRay.getColor()).multiply(
//                        Shader.getBrightness(ray, object, light)
//                ));
            }
        }

        rayCounter.addAndGet(sceneLights.size());

//        totalColor.divide(255);

        totalR /= 255;
        totalG /= 255;
        totalB /= 255;

        return new Vec3(totalR, totalG, totalB);
    }

//    Full Calculations

    public static Vec3 findColor(Ray _ray, List<SceneObject> sceneObjects, List<SceneLight> sceneLights) {
//        Vec3 throughput = new Vec3(1, 1, 1); // tint
//        Vec3 accumulatedColor = new Vec3(0,0,0); // color

        double throughputX = 1;
        double throughputY = 1;
        double throughputZ = 1;

        double accumulatedX = 0;
        double accumulatedY = 0;
        double accumulatedZ = 0;

        Ray reflectionRay = new Ray(_ray);

        int i;

        for (i = 0; i < 10; i++) {
            boolean hit = false;
            double lowest_t = Double.MAX_VALUE;
            SceneObject hitObject = null;

            for (int j = 0; j < sceneObjects.size(); j++) {
                SceneObject object = sceneObjects.get(j);

                double t = object.calculateRayTravel(reflectionRay.getPos(), reflectionRay.getDir());
                if (t > 0 && t < lowest_t) {
                    lowest_t = t;
                    hitObject = object;
                    hit = true;
                }
            }

            if (!hit) {
                break;
            }

            reflectionRay.setPos(hitObject.calculateRayTravelPos(reflectionRay.getPos(), reflectionRay.getDir()));

            Vec3 localColor = localColor(reflectionRay, hitObject, sceneObjects, sceneLights);

            localColor.x *= throughputX;
            localColor.y *= throughputY;
            localColor.z *= throughputZ;

            accumulatedX += localColor.x;
            accumulatedY += localColor.y;
            accumulatedZ += localColor.z;

//            Vec3 dielectricTint = new Vec3(255 * 0.04, 255 * 0.04, 255 * 0.04).multiply(1-hitObject.getMetallic()); // 4% white base
//            Vec3 metallicTint = new Vec3(hitObject.getColor()).multiply(hitObject.getMetallic());

            double defaultDielectric = 255 * 0.04 * 1 - hitObject.getMetallic();

            double metallicX = hitObject.getColor().x * hitObject.getMetallic();
            double metallicY = hitObject.getColor().y * hitObject.getMetallic();
            double metallicZ = hitObject.getColor().z * hitObject.getMetallic();

//            Vec3 combinedTint = new Vec3(dielectricTint).add(metallicTint).divide(255);

            double combinedX = (defaultDielectric + metallicX) / 255;
            double combinedY = (defaultDielectric + metallicY) / 255;
            double combinedZ = (defaultDielectric + metallicZ) / 255;

//            throughput.multiply(combinedTint);

            throughputX *= combinedX;
            throughputY *= combinedY;
            throughputZ *= combinedZ;

            Vec3 normal = hitObject.normal(reflectionRay.getPos(), reflectionRay.getDir());
            reflectionRay.getPos().add(normal.multiply(EPSILON)); // Prevent getting stuck in the wall
            reflectionRay.reflect(hitObject);

            if (hitObject.getRoughness() != 0) {
                Random random = ThreadLocalRandom.current();
                Vec3 perfectDir = reflectionRay.getDir();
                double variable = hitObject.getRoughness() * ROUGHNESS_SCALER;

                double dx = random.nextDouble(-variable, variable);
                double dy = random.nextDouble(-variable, variable);
                double dz = random.nextDouble(-variable, variable);

                dx += perfectDir.x;
                dy += perfectDir.y;
                dz += perfectDir.z;

                double length = Math.sqrt(dx * dx + dy * dy + dz * dz);

                dx /= length;
                dy /= length;
                dz /= length;

                reflectionRay.setDir(dx, dy, dz);
            }
        }

        rayCounter.addAndGet(i);

        return new Vec3(
                Math.min(255, accumulatedX),
                Math.min(255, accumulatedY),
                Math.min(255, accumulatedZ));
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
