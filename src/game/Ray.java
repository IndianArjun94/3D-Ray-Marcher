package game;

public class Ray {
    public double x = 0;
    public double y = 0;
    public double z = 0; // pos

    public double px, py;

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

        /*
        * sets the direction of the rays (x, y)
        * Aspect ratio 1:1, why?
        * the formulas scale to: -1 to 1 for BOTH x and y, REGARDLESS fo the Width and Height
        *
        * */

        // 2. aspect ratio
        _dx *= (W / H); // this changes the ASPECT RATIO to match the window size

        // 3. apply FOV
        double fovScale = Math.tan(Math.toRadians(FOV / 2));
        _dx *= fovScale;
        _dy *= fovScale;

        // 4. construct direction
        dx = _dx;
        dy = _dy;
        dz = -1;

        // 5. normalize ONCE
        double length = Math.sqrt(dx*dx + dy*dy + dz*dz);
        dx /= length;
        dy /= length;
        dz /= length;
        /*
        * keeps the directions equivalent to 1
        * the distance adding all 3 will give you is equivalent to 1 (via the pythagorean theorem)
        * divides by the total current length (via pythagorean theorem) to keep the length = 1 (example: 3/3 = 1)
         */


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

}
