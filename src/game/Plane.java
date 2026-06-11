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
        double Dx = rayDir.x;
        double Dy = rayDir.y;
        double Dz = rayDir.z;

        double Ox = rayOrigin.x;
        double Oy = rayOrigin.y;
        double Oz = rayOrigin.z;

        double P0x = coord1.x;
        double P0y = coord1.y;
        double P0z = coord1.z;

        double edge1VectorX = coord2.x-coord1.x;
        double edge1VectorY = coord2.y-coord1.y;
        double edge1VectorZ = coord2.z-coord1.z;

        double edge2VectorX = coord3.x-coord2.x;
        double edge2VectorY = coord3.y-coord2.y;
        double edge2VectorZ = coord3.z-coord2.z;

        double nx = edge1VectorY*edge2VectorZ - edge1VectorZ*edge2VectorY;
        double ny = edge1VectorZ*edge2VectorX - edge1VectorX*edge2VectorZ;
        double nz = edge1VectorX*edge2VectorY - edge1VectorY*edge2VectorX;

        double nD_dot = nx*Dx + ny*Dy + nz*Dz;

        if (Math.abs(nD_dot) < 0.0001) {
            return -1;
        }

        double t = (-nx*(Ox-P0x) - ny*(Oy-P0y) - nz*(Oz-P0z)) / nD_dot;

        double Px = Dx*t + Ox;
        double Py = Dy*t + Oy;
        double Pz = Dz*t + Oz;

        double edge3VectorX = coord1.x - coord3.x;
        double edge3VectorY = coord1.y - coord3.y;
        double edge3VectorZ = coord1.z - coord3.z;

        double edge4VectorX = coord4.x - coord3.x;
        double edge4VectorY = coord4.y - coord3.y;
        double edge4VectorZ = coord4.z - coord3.z;

        double edge5VectorX = coord1.x - coord4.x;
        double edge5VectorY = coord1.y - coord4.y;
        double edge5VectorZ = coord1.z - coord4.z;

        double e1InsideNormalX = ny*edge1VectorZ - nz*edge1VectorY;
        double e1InsideNormalY = nz*edge1VectorX - nx*edge1VectorZ;
        double e1InsideNormalZ = nx*edge1VectorY - ny*edge1VectorX;

        double e2InsideNormalX = ny*edge2VectorZ - nz*edge2VectorY;
        double e2InsideNormalY = nz*edge2VectorX - nx*edge2VectorZ;
        double e2InsideNormalZ = nx*edge2VectorY - ny*edge2VectorX;

        double e3InsideNormalT1X = ny*edge3VectorZ - nz*edge3VectorY;
        double e3InsideNormalT1Y = nz*edge3VectorX - nx*edge3VectorZ;
        double e3InsideNormalT1Z = nx*edge3VectorY - ny*edge3VectorX;

        double e3InsideNormalT2X = edge3VectorY*nz - edge3VectorZ*ny;
        double e3InsideNormalT2Y = edge3VectorZ*nx - edge3VectorX*nz;
        double e3InsideNormalT2Z = edge3VectorX*ny - edge3VectorY*nx;

        double e4InsideNormalX = ny*edge4VectorZ - nz*edge4VectorY;
        double e4InsideNormalY = nz*edge4VectorX - nx*edge4VectorZ;
        double e4InsideNormalZ = nx*edge4VectorY - ny*edge4VectorX;

        double e5InsideNormalX = ny*edge5VectorZ - nz*edge5VectorY;
        double e5InsideNormalY = nz*edge5VectorX - nx*edge5VectorZ;
        double e5InsideNormalZ = nx*edge5VectorY - ny*edge5VectorX;

        int inHalves = 0;

        if ((Px-coord1.x)*e1InsideNormalX + (Py-coord1.y)*e1InsideNormalY + (Pz-coord1.z)*e1InsideNormalZ > 0) {
            inHalves++;
        } if ((Px-coord2.x)*e2InsideNormalX + (Py-coord2.y)*e2InsideNormalY + (Pz-coord2.z)*e2InsideNormalZ > 0) {
            inHalves++;
        } if ((Px-coord3.x)*e3InsideNormalT1X + (Py-coord3.y)*e3InsideNormalT1Y + (Pz-coord3.z)*e3InsideNormalT1Z > 0) {
            inHalves++;
        }

        if (inHalves == 3) {
            return t;
        } else {
            inHalves = 0;
        }

        if ((Px-coord3.x)*e3InsideNormalT2X + (Py-coord3.y)*e3InsideNormalT2Y + (Pz-coord3.z)*e3InsideNormalT2Z > 0) {
            inHalves++;
        } if ((Px-coord4.x)*e4InsideNormalX + (Py-coord4.y)*e4InsideNormalY + (Pz-coord4.z)*e4InsideNormalZ > 0) {
            inHalves++;
        } if ((Px-coord4.x)*e5InsideNormalX + (Py-coord4.y)*e5InsideNormalY + (Pz-coord4.z)*e5InsideNormalZ > 0) {
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

        Vec3 finalHitPoint = new Vec3(rayDir);
        finalHitPoint.multiply(t);
        finalHitPoint.add(rayOrigin);

        return finalHitPoint;

    }

    @Override
    public Vec3 normal(Vec3 pos, Vec3 dir) {
        double edge1VectorX = coord2.x-coord1.x;
        double edge1VectorY = coord2.y-coord1.y;
        double edge1VectorZ = coord2.z-coord1.z;

        double edge2VectorX = coord3.x-coord2.x;
        double edge2VectorY = coord3.y-coord2.y;
        double edge2VectorZ = coord3.z-coord2.z;

        double nx = edge1VectorY*edge2VectorZ - edge1VectorZ*edge2VectorY;
        double ny = edge1VectorZ*edge2VectorX - edge1VectorX*edge2VectorZ;
        double nz = edge1VectorX*edge2VectorY - edge1VectorY*edge2VectorX;

        double length = Math.sqrt(nx*nx + ny*ny + nz*nz);

        nx /= length;
        ny /= length;
        nz /= length;

        if (dir.x*nx + dir.y*ny + dir.z*nz > 0) {
            return new Vec3(-nx, -ny, -nz);
        }

        return new Vec3(nx, ny, nz);
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
