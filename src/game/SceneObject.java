package game;

public interface SceneObject {
    double distance(Vec3 pos);
    Vec3 normal(Vec3 pos, Vec3 dir);

    // getters and setters
    Vec3 getColor();
    void setColor(Vec3 rgb);

    double getReflectivity();
    void setReflectivity(double reflectivity);

    double calculateRayTravel(Vec3 rayOrigin, Vec3 rayDir);
    Vec3 calculateRayTravelPos(Vec3 rayOrigin, Vec3 rayDir);
}