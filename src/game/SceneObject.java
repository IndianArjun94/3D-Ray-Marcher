package game;

public interface SceneObject {
    double distance(Vec3 pos);
    Vec3 normal(Vec3 pos);

    // getters and setters
    Vec3 getColor();
    void setColor(Vec3 rgb);

    double getRoughness();
    void setRoughness(double roughness);
}