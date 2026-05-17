package game;

public class Sphere implements SceneObject {
    public Vec3 pos;
    public double r;
    public Vec3 color;
    public double roughness;

    public Sphere(Vec3 pos, double r, Vec3 rgb, double roughness) {
        this.pos = pos;
        this.r = r;
        this.color = rgb;
        this.roughness = roughness;
    }

    public double distance(Vec3 pos) {
        Vec3 distance = new Vec3(pos);

        distance.subtract(this.pos);

        return distance.length() - r;
    }

    public Vec3 normal(Vec3 pos) {
        Vec3 normal = new Vec3(pos);

        normal.subtract(this.pos);
        /* Converts (x,y,z) into relative coordinates to the sphere origin
         * example: if origin is (0,0,0), if provided coords are (5,5,5),
         * we get: (5-0, 5-0, 5-0) = (5,5,5)
         *
         * These coordinates are directions, but they need to be "normalized" ( = 1)
         * to be normals. We do this by following a few steps:
         * 1) find the distance from origin to (nx,ny,nz): sqrt(nx^2 + ny^2 + nz^2) distance formula
         * 2) divide that distance from nx, ny, and nz individually: nx/length, ny/length, nz/length
         */

        normal.normalize();

        return normal;

    }

    // getters and setters

    public double getRoughness() {
        return roughness;
    }

    public void setRoughness(double roughness) {
        this.roughness = roughness;
    }

    public Vec3 getColor() {
        return color;
    }

    public void setColor(Vec3 color) {
        this.color = color;
    }
}