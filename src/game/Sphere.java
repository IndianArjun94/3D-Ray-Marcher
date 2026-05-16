package game;

public class Sphere implements SceneObject {
    double x, y, z, r;

    public Sphere(double x, double y, double z, double r) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.r = r;
    }

    public double distance(double x, double y, double z) {
        double dx = x - this.x;
        double dy = y - this.y;
        double dz = z - this.z;
        return Math.sqrt(dx*dx + dy*dy + dz*dz) - r;
    }

    public double[] normal(double x, double y, double z) {
        double nx = x-this.x; // converts (x,y,z) into relative coordinates to the sphere origin
        double ny = y-this.y; // example: if origin is (0,0,0), if provided coords are (5,5,5),
        double nz = z-this.z; // we get: (5-0, 5-0, 5-0) = (5,5,5)

        /* These coordinates are directions, but they need to be "normalized" ( = 1)
         * to be normals. We do this by following a few steps:
         * 1) find the distance from origin to (nx,ny,nz): sqrt(nx^2 + ny^2 + nz^2) distance formula
         * 2) divide that distance from nx, ny, and nz individually: nx/length, ny/length, nz/length
         */

        double length = Math.sqrt(nx*nx + ny*ny + nz*nz);

        nx /= length;
        ny /= length;
        nz /= length;

        return new double[]{nx, ny, nz};

    }
}