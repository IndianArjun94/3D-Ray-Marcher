package game;

public class Plane implements SceneObject {
    private Vec3 coord1, coord2, coord3, coord4;
    private Vec3 color;
    private double metallic;
    private double roughness;

    public Plane(Vec3 coord1, Vec3 coord2, Vec3 coord3, Vec3 coord4, Vec3 color, double metallic, double roughness) {
        this.coord1 = coord1;
        this.coord2 = coord2;
        this.coord3 = coord3;
        this.coord4 = coord4;
        this.color = color;
        this.metallic = metallic;
        this.roughness = roughness;
    }

    @Override
    public double distance(Vec3 pos) {
        return this.normal(new Vec3(0, 0, 0), new Vec3(0, 0, 0)).dot(new Vec3(pos).subtract(coord1));
    }

    public double calculateRayTravel(Vec3 rayOrigin, Vec3 rayDir) {
        Vec3 D = rayDir;
        Vec3 O = rayOrigin;
        Vec3 P0 = coord1;

        Vec3 edge1Vector = new Vec3(coord2).subtract(coord1); // [triangle 1] we don't normalize because it wastes system resources and is unnecessary at this step
        Vec3 edge2Vector = new Vec3(coord3).subtract(coord2); // [triangle 1]

        Vec3 n = Vec3.cross(edge1Vector, edge2Vector).normalize();

        if (Math.abs(n.dot(D)) < 0.0001) {
            return -1;
        }

        double t = (-n.dot(new Vec3(O).subtract(P0))) / (n.dot(D));

        Vec3 P = new Vec3(D).multiply(t).add(O); // final hit point


        Vec3 edge3Vector = new Vec3(coord1).subtract(coord3); // [shared diagonal]
        Vec3 edge4Vector = new Vec3(coord4).subtract(coord3); // [triangle 2]
        Vec3 edge5Vector = new Vec3(coord1).subtract(coord4); // [triangle 2]

        Vec3 e1InsideNormal = Vec3.cross(n, edge1Vector);
        Vec3 e2InsideNormal = Vec3.cross(n, edge2Vector);
        Vec3 e3InsideNormalT1 = Vec3.cross(n, edge3Vector);

        Vec3 e3InsideNormalT2 = Vec3.cross(edge3Vector, n);
        Vec3 e4InsideNormal = Vec3.cross(n, edge4Vector);
        Vec3 e5InsideNormal = Vec3.cross(n, edge5Vector);

        int inHalves = 0;

        if (new Vec3(P).subtract(coord1).dot(e1InsideNormal) > 0) {
            inHalves++;
        } if (new Vec3(P).subtract(coord2).dot(e2InsideNormal) > 0) {
            inHalves++;
        } if (new Vec3(P).subtract(coord3).dot(e3InsideNormalT1) > 0) {

            inHalves++;
        }

        if (inHalves == 3) {
            return t;
        } else {
            inHalves = 0;
        }

        if (new Vec3(P).subtract(coord3).dot(e3InsideNormalT2) > 0) {
            inHalves++;
        } if (new Vec3(P).subtract(coord4).dot(e4InsideNormal) > 0) {
            inHalves++;
        } if (new Vec3(P).subtract(coord4).dot(e5InsideNormal) > 0) {
            inHalves++;
        }

        if (inHalves == 3) {
            return t;
        } else {
            return -1;
        }

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

    @Override
    public Vec3 normal(Vec3 pos, Vec3 dir) {
        Vec3 edge1Vector = new Vec3(coord2).subtract(coord1); // [triangle 1]
        Vec3 edge2Vector = new Vec3(coord3).subtract(coord2); // [triangle 1]

        Vec3 n = Vec3.cross(edge1Vector, edge2Vector).normalize();

        if (dir.dot(n) > 0) {
            return Vec3.cross(edge2Vector, edge1Vector).normalize();
        }

        return n;
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
    public double getMetallic() {
        return metallic;
    }

    @Override
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

}
