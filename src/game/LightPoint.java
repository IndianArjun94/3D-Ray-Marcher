package game;

public class LightPoint implements SceneLight {
    private Vec3 pos;
    private Vec3 color;
    private double brightness;

    public LightPoint(Vec3 pos, Vec3 color, double brightness) {
        this.pos = pos;
        this.color = color;
        this.brightness = brightness;
    }

    public double distance(Vec3 pos) {
        Vec3 distance = new Vec3(pos);

        distance.subtract(this.pos);

        return distance.length();
    }

    public Vec3 normal(Vec3 pos) { // uses sphere logic
        Vec3 normal = new Vec3(this.pos);

        normal.subtract(pos);
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

    public Vec3 getColor() {
        return color;
    }

    public void setColor(Vec3 rgb) {
        this.color = rgb;
    }

    public double getBrightness() {
        return brightness;
    }

    public void setBrightness(double brightness) {
        this.brightness = brightness;
    }
}
