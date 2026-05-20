package game;

public class Sphere implements SceneObject {
    private Vec3 pos;
    private double r;
    private Vec3 color;
    private double reflectivity;

    public Sphere(Vec3 pos, double r, Vec3 rgb, double reflectivity) {
        this.pos = pos;
        this.r = r;
        this.color = rgb;
        this.reflectivity = reflectivity;
    }

    public double distance(Vec3 pos) {
        Vec3 distance = new Vec3(pos);

        distance.subtract(this.pos);

        return distance.length() - r;
    }

    public Vec3 calculateRayTravelTo(Vec3 rayOrigin, Vec3 rayDir) {
        // O = ray origin
        // D = ray direction
        // t = travel

        // Ray position (P) = O + tD

        // C = sphere center
        // r = sphere radius

        // Ray distance from Sphere Center |P - C|^2 = r^2 (radius)
        // this is just the distance formula, since P and C are both Vec3s

        // Ray distance to ray |(O + tD) - C|^2 = r^2 (radius)
        // We substitute "P" for the ray position formula

        // (O + tD - C)^2 = r^2
        // we must get a, b, and c from this to use the quadratic formula
        // if the ray misses, that means that (O + tD - C)^2 must be greater than r^2, and the ray misses
        // this would be represented by if (b^2 - 4ac) is negative.
        // if (b^2 - 4ac) is = 0, then the ray skims the circle at one point, and if its > 0, then the ray could pass through the sphere, in one end and out the other.


        // Formula:|tD + L - C|^2 = r^2
        // t = ray travel
        // D = ray direction

        // L = O - C


        // |tD + L|^2 = r^2
        // = (tD + L)(tD + L) = r^2

        // tD^2 + 2tDL + L^2 = r^2
        // (tD * tD) + 2(tDL) + (L * L) = r^2

        // t^2(D * D) + 2t(D * L) + (L * L) - r^2 = 0

        return null;

    }

    public Vec3 normal(Vec3 pos, Vec3 dir) {
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

    public double getReflectivity() {
        return reflectivity;
    }

    public void setReflectivity(double reflectivity) {
        this.reflectivity = reflectivity;
    }

    public Vec3 getColor() {
        return color;
    }

    public void setColor(Vec3 color) {
        this.color = color;
    }
}