package game;

import java.util.List;

public class Shader {
    public static double getBrightness(Ray primaryRay, SceneObject object, SceneLight light) {
        Vec3 N = object.normal(primaryRay.getPos());
        Vec3 L = light.normal(primaryRay.getPos());

        double I = Math.max(0, N.dot(L));

        return I;

//        return primaryRay.getColor().multiply(new Vec3(
//                object.getColor().x / 255,
//                object.getColor().y / 255,
//                object.getColor().z / 255)).multiply(I);
    }

    public static Vec3 calcColor(SceneObject object, List<Ray> shadowRays) { // calculate from shadow rays
        Vec3 finalColor = object.getColor();

        for (int i = 0; i < shadowRays.size(); i++) {
            Vec3 shadowRayColor = new Vec3(shadowRays.get(i).getColor());
            finalColor.multiply(shadowRayColor.divide(255));
        }

        return finalColor;
    }
}
