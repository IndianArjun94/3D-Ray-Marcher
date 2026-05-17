package game;

public class Ray {
    private Vec3 pos = new Vec3(0,0,0);

    private double px, py;

    private Vec3 originalDir;
    private Vec3 dir;
    private final double stepSize = 0.01;

    private Vec3 color;

    public Ray(Ray ray) {
        pos = ray.getPos();
        px = ray.getPx();
        py = ray.getPy();
        originalDir = ray.getOriginalDir();
        dir = ray.getDir();
        color = ray.getColor();
    }

    public Ray(int px, int py, double W, double H, double FOV, Vec3 color) {
        this.px = px;
        this.py = py;


        // 1. screen space ----------------------------------------------------------------
        Vec3 _dir = new Vec3(
                (px / W) * 2 - 1,
                1 - (py / H) * 2,
                -1);

        /* (px / W) * 2 - 1; between (left)-1 and (right)+1
         * 1 - (py / H) * 2; between (top)+1 and (bottom)-1
         * starts as -1, going straight forward out of the camera (origin, starting point)
         * y uses a different formula to because y is by default inverted (top)+1 and (bottom)-1
         *
         * sets the direction of the rays (x, y)
         * Aspect ratio 1:1, why?
         * the formulas scale to: -1 to 1 for BOTH x and y, REGARDLESS of the Width and Height
         */

        // 2. adjust aspect ratio ----------------------------------------------------------------
        _dir.multiply((W / H),1,1); // this changes the ASPECT RATIO to match the window size (example: _dx range: [-1 to 1] -> [-1.4 to 1])

        // 3. apply FOV ----------------------------------------------------------------
        double fovScale = Math.tan(Math.toRadians(FOV / 2));

        _dir.multiply(fovScale,fovScale,1);

        // 4. normalize ONCE ----------------------------------------------------------------
        double length = Math.sqrt(_dir.x * _dir.x + _dir.y * _dir.y + _dir.z * _dir.z);
        originalDir = new Vec3(_dir);
        originalDir.divide(length);

        /*
         * keeps the directions equivalent to 1
         * the distance adding all 3 will give you is equivalent to 1 (via the pythagorean theorem)
         * divides by the total current length (via pythagorean theorem) to keep the length = 1 (example: 3/3 = 1)
         */

        dir = new Vec3(originalDir);

        this.color = color;
    }

    public void tick() {
        pos.add(
                dir.x * stepSize,
                dir.y * stepSize,
                dir.z * stepSize
        );
    }

    public void resetPos() {
        pos = new Vec3(0,0,0);
    }

    public void reflect(SceneObject object) {
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

        dir.reflect(object.normal(pos));
    }

    // getters and setters

    public Vec3 getPos() {
        return pos;
    }

    public void setPos(Vec3 pos) {
        this.pos = pos;
    }

    public double getPx() {
        return px;
    }

    public void setPx(double px) {
        this.px = px;
    }

    public double getPy() {
        return py;
    }

    public void setPy(double py) {
        this.py = py;
    }

    public Vec3 getColor() {
        return color;
    }

    public void setColor(Vec3 color) {
        this.color = color;
    }

    public Vec3 getOriginalDir() {
        return originalDir;
    }

    public void setOriginalDir(Vec3 originalDir) {
        this.originalDir = originalDir;
    }

    public Vec3 getDir() {
        return dir;
    }

    public void setDir(Vec3 dir) {
        this.dir = dir;
    }
}
