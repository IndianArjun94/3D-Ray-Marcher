package game;

import java.util.List;

public class Shader {
    public static double getBrightness(Ray primaryRay, SceneObject object, SceneLight light) {
        Vec3 N = object.normal(primaryRay.getPos(), primaryRay.getDir()); // surface normal
        Vec3 L = light.normal(primaryRay.getPos()); // light normal

        double I = Math.max(0, N.dot(L));

        return I * light.getBrightness();
    }

    public static Vec3 calcLocalColor(SceneObject object, List<Ray> shadowRays) { // calculate from shadow rays
        Vec3 finalColor = new Vec3(object.getColor());

        for (int i = 0; i < shadowRays.size(); i++) {
            Vec3 shadowRayColor = new Vec3(shadowRays.get(i).getColor());
            finalColor.multiply(shadowRayColor.divide(255));
        }

        return finalColor;
    }
}
