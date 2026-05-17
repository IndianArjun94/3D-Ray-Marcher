package game;

public class Plane implements SceneObject {
    private Vec3 point;
    private Vec3 normal;
    private Vec3 color;
    double roughness;

    public Plane(Vec3 point, Vec3 normal, Vec3 color, double roughness) {
        this.point = point;
        this.normal = normal;
        this.color = color;
        this.roughness = roughness;
    }

    @Override
    public double distance(Vec3 pos) {
        Vec3 dist = new Vec3(pos).subtract(point);
        return dist.dot(normal);
    }

    @Override
    public Vec3 normal(Vec3 pos, Vec3 dir) {
        if (normal.dot(dir) > 0) {
            return new Vec3(normal).multiply(-1);
        } else if (normal.dot(dir) < 0) {
            return normal;
        } else {
            return null;
        }
    }

    @Override
    public Vec3 getColor() {
        return color;
    }

    @Override
    public void setColor(Vec3 color) {
        this.color = color;
    }

    @Override
    public double getRoughness() {
        return roughness;
    }

    @Override
    public void setRoughness(double roughness) {
        this.roughness = roughness;
    }
}
