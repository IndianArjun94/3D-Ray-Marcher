package game;

public class PointLight implements SceneLight {
    private Vec3 pos;
    private Vec3 color;
    private double brightness;

    public PointLight(Vec3 pos, Vec3 color, double brightness) {
        this.pos = pos;
        this.color = color;
        this.brightness = brightness;
    }

    public double distance(Vec3 pos) {
        double distX = pos.x-this.pos.x;
        double distY = pos.y-this.pos.y;
        double distZ = pos.z-this.pos.z;

        return Math.sqrt(distX*distX + distY*distY + distZ*distZ);
    }

    public Vec3 getDirectionFrom(Vec3 surfacePos) { // uses sphere logic
        Vec3 dirToLight = new Vec3(this.pos).subtract(surfacePos);
        dirToLight.normalize();
        return dirToLight;

    }

    public Vec3 getPos() {
        return pos;
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

        double dirX = rayDir.x;
        double dirY = rayDir.y;
        double dirZ = rayDir.z;

        double oX = rayOrigin.x;
        double oY = rayOrigin.y;
        double oZ = rayOrigin.z;

        double ocX = oX - this.pos.x;
        double ocY = oY - this.pos.y;
        double ocZ = oZ - this.pos.z;

        // double r = radius; already have this defined in this instance of sphere

        double a = dirX*dirX + dirY*dirY + dirZ*dirZ;
        double b = 2*(dirX*ocX + dirY*ocY + dirZ*ocZ);
        double c = (ocX*ocX + ocY*ocY + ocZ*ocZ) - (0.0001);

        if (b > 0) {
            return -1;
        }

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
    }

    public Vec3 calculateRayTravelPos(Vec3 rayOrigin, Vec3 rayDir) {
        double t = calculateRayTravel(rayOrigin, rayDir);

        if (t == -1) {
            return null;
        }

        Vec3 finalHitPoint = new Vec3(rayDir);
        finalHitPoint.multiply(t);
        finalHitPoint.add(rayOrigin);

        return finalHitPoint;

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
