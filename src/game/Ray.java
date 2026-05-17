package game;

public class Ray {
    public double x = 0;
    public double y = 0;
    public double z = 0; // pos

    public double px, py;

    public final double originalDx, originalDy, originalDz; // dir
    public double dx, dy, dz; // dir
    public double stepSize = 0.01;

    public double FOV;


    public Ray(int px, int py, double W, double H, double FOV) {
        this.px = px;
        this.py = py;


        // 1. screen space
        double _dx = (px / W) * 2 - 1; // between (left)-1 and (right)+1
        double _dy = 1 - (py / H) * 2; // between (top)+1 and (bottom)-1
        // y uses a different formula to because y is by default inverted (top)+1 and (bottom)-1

        double _dz = -1; // starts as -1, going straight forward out of the camera (origin, starting point)

        /*
        * sets the direction of the rays (x, y)
        * Aspect ratio 1:1, why?
        * the formulas scale to: -1 to 1 for BOTH x and y, REGARDLESS of the Width and Height
        *
        * */

        // 2. aspect ratio
        _dx *= (W / H); // this changes the ASPECT RATIO to match the window size (example: _dx range: -1 to 1 -> -1.4 to 1

        // 3. apply FOV
        double fovScale = Math.tan(Math.toRadians(FOV / 2));
        _dx *= fovScale;
        _dy *= fovScale;

        // 5. normalize ONCE
        double length = Math.sqrt(_dx * _dx + _dy * _dy + _dz * _dz);
        originalDx =  dx / length;
        originalDy = dy / length;
        originalDz = dz / length;
        /*
        * keeps the directions equivalent to 1
        * the distance adding all 3 will give you is equivalent to 1 (via the pythagorean theorem)
        * divides by the total current length (via pythagorean theorem) to keep the length = 1 (example: 3/3 = 1)
         */

        dx = originalDx;
        dy = originalDy;
        dz = originalDz;


        this.FOV = FOV;
    }

    public void tick() {
        x += dx * stepSize;
        y += dy * stepSize;
        z += dz * stepSize;
    }

    public void resetPos() {
        x = 0;
        y = 0;
        z = 0;
    }

    public void reflect(double[] normal) {
        /* D = (dx,dy,dz) | incoming direction
         * N = -> double[] normal | surface normal
         *
         * Formula: D - 2(D * N)N
         * Dot: D*N - how close D is to N (-1 means opposite direction, 1 is exact direction match)
         * NOTE: a dot of -1 (or similar) means the ray points TOWARDS the surface, because the normal points AWAY from the surface
         *
         * The Dot product only measures the axes of D and N that contribute to the bounce.
         * Example:
         * D = (0.6, -0.8, 0.0)
         * N = (0.0. 1.0, 0.0)
         * dot = -0.8, ignoring dx = 0.6 because N has no x value (0) and faces straight upward
         * when the ray bounces, the -0.8 will cause the ray to bounce back up, and the 0.6 will persist because the plane the Normal has no x value and is perfectly flat
         * imagine a ray bouncing off your desk at an angle, the dx or dz won't change, just the dy, because your desk's normal only has a ny.
         */

        double nx = normal[0];
        double ny = normal[1];
        double nz = normal[2];

        double dot = dx * nx + dy * ny + dz * nz;

        dx = dx - 2 * dot * nx;
        dy = dy - 2 * dot * ny;
        dz = dz - 2 * dot * nz;

        // normalize again *sigh*
        double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
        dx /= length;
        dy /= length;
        dz /= length;
    }

}
