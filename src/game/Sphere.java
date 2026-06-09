package game;

public class Sphere implements SceneObject {
    private Vec3 pos;
    private double r;
    private Vec3 color;
    private double metallic;
    private double roughness;

    public Sphere(Vec3 pos, double r, Vec3 rgb, double metallic, double roughness) {
        this.pos = pos;
        this.r = r;
        this.color = rgb;
        this.metallic = metallic;
        this.roughness = roughness;
    }

    public double distance(Vec3 pos) {
        Vec3 distance = new Vec3(pos);

        distance.subtract(this.pos);

        return distance.length() - r;
    }

    public double calculateRayTravel(Vec3 rayOrigin, Vec3 rayDir) {
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
        // BUT FIRST, simplify (O + tD - C)^2 into (oc + tD)^2 -> oc is a new var: O-C

        // oc = O - C (ray origin - sphere center)

        // NEW FORMULA:
        // (tD + oc)^2 = r^2
        // (tD + oc)(tD + oc) = r^2
        // (tD*tD) + 2(tD * oc) + oc^2
        // t^2(D*D) + 2t(D * oc) + (oc*oc) - r^2 = 0

        // if the ray misses, that means that (tD + oc)^2 must be greater than r^2, and the ray misses
        // this would be represented by if (b^2 - 4ac) is negative.
        // if (b^2 - 4ac) is = 0, then the ray skims the circle at one point, and if its > 0, then the ray could pass through the sphere, in one end and out the other.

        // in  t^2(D*D) + 2t(D * oc) + (oc*oc) - r^2 = 0
        // ax^2 + bx + c = 0
        // x = t
        // taking out x, we get
        // a = D*D
        // b = 2(D * oc)
        // c = (oc*oc) - r^2

        Vec3 D = rayDir;
        Vec3 O = rayOrigin;
        Vec3 C = pos;
        Vec3 oc = new Vec3(O).subtract(C);
        // double r = radius; already have this defined in this instance of sphere

        double a = D.dot(D);
        double b = 2*D.dot(oc);
        double c = oc.dot(oc) - (r*r);

        double discriminant = (b*b - 4*a*c);

        if (discriminant < 0) {
            return -1;
        }

        double[] solutions = new double[2];

        solutions[0] = (-b - Math.sqrt(discriminant))/(2*a);
        solutions[1] = (-b + Math.sqrt(discriminant))/(2*a);

        double t = -1;

        if (solutions[0] > 0) {
            t = solutions[0];
        } else if (solutions[1] > 0) {
            t = solutions[1];
        }

        if (t < 0) {
            return -1; // ray misses the sphere entirely
        }

        return t;

//        Vec3 finalHitPoint = new Vec3(D);
//        finalHitPoint.multiply(t);
//        finalHitPoint.add(O);
//
//        return finalHitPoint;

    }

    public Vec3 calculateRayTravelPos(Vec3 rayOrigin, Vec3 rayDir) {
        double t = calculateRayTravel(rayOrigin, rayDir);

        if (t == -1) {
            return null;
        }

        Vec3 D = rayDir;
        Vec3 O = rayOrigin;

        Vec3 finalHitPoint = new Vec3(D);
        finalHitPoint.multiply(t);
        finalHitPoint.add(O);

        return finalHitPoint;

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

    public double getMetallic() {
        return metallic;
    }

    public void setMetallic(double metallic) {
        this.metallic = metallic;
    }

    @Override
    public double getRoughness() {
        return roughness;
    }

    @Override
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