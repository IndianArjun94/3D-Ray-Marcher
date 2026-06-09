package game;

public interface SceneLight {
    double distance(Vec3 pos);
    Vec3 getDirectionFrom(Vec3 pos);

    // getters and setters
    Vec3 getColor();
    void setColor(Vec3 rgb);

    double getBrightness();
    void setBrightness(double brightness);

    Vec3 getPos();

    double calculateRayTravel(Vec3 rayOrigin, Vec3 rayDir);
    Vec3 calculateRayTravelPos(Vec3 rayOrigin, Vec3 rayDir);
}
