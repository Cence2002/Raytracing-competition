package uk.ac.cam.cl.gfxintro.bh525.tick1star;

public class Sphere extends SceneObject {

    // Sphere coefficients
    private final double SPHERE_KD = 0.6;
    private final double SPHERE_KS = 1.4;
    private final double SPHERE_ALPHA = 10;
    public double SPHERE_REFLECTIVITY = 0.2;
    public int label = -100;
    public String colorName = "#FFFFFF";

    // The world-space position of the sphere
    protected Vector3 position;

    public Vector3 getPosition() {
        return position;
    }

    // The radius of the sphere in world units
    protected double radius;

    public Sphere(Vector3 position, double radius, ColorRGB colour) {
        this.position = position;
        this.radius = radius;
        this.colour = colour;

        this.phong_kD = SPHERE_KD;
        this.phong_kS = SPHERE_KS;
        this.phong_alpha = SPHERE_ALPHA;
        this.reflectivity = SPHERE_REFLECTIVITY;
    }

    public Sphere(Vector3 position, double radius, ColorRGB colour, double kD, double kS, double alphaS, double reflectivity) {
        this.position = position;
        this.radius = radius;
        this.colour = colour;

        this.phong_kD = kD;
        this.phong_kS = kS;
        this.phong_alpha = alphaS;
        this.reflectivity = reflectivity;
    }

    public Sphere setLabel(int label) {
        this.label = label;
        return this;
    }

    public Sphere setColorName(String name) {
        colorName = name;
        return this;
    }

    /*
     * Calculate intersection of the sphere with the ray. If the ray starts inside the sphere,
     * intersection with the surface is also found.
     */
    public RaycastHit intersectionWith(Ray ray) {

        // Get ray parameters
        Vector3 O = ray.getOrigin();
        Vector3 D = ray.getDirection();

        // Get sphere parameters
        Vector3 C = position;
        double r = radius;

        // Calculate quadratic coefficients
        double a = D.dot(D);
        double b = 2 * D.dot(O.subtract(C));
        double c = (O.subtract(C)).dot(O.subtract(C)) - Math.pow(r, 2);

        double d = b * b - 4 * a * c;
        if (d >= 0) {
            d = Math.sqrt(d);
            double s = (-b - d) / (2 * a);
            if (s >= 0) {
                double distance = s * D.magnitude();
                Vector3 location = O.add(D.scale(s));
                Vector3 normal = location.subtract(C).normalised();
                normal.normalised();
                return new RaycastHit(this, distance, location, getNormalAt(location));
            }
        }

        return new RaycastHit();
    }

    @Override
    public Sphere invert(Sphere base) {
        double s = Math.pow(base.radius, 2) / (
                Math.pow(position.subtract(base.position).magnitude(), 2) - Math.pow(radius, 2));
        double r = Math.abs(s) * radius;
        Vector3 p = position.subtract(base.position).scale(s).add(base.position);
        Sphere sphere = new Sphere(p, r, colour);
        return sphere;
    }

    // Get normal to surface at position
    @Override
    public Vector3 getNormalAt(Vector3 position) {
        return position.subtract(this.position).normalised();
    }

    public String toXML() {
        return String.format("<sphere x=\"%f\" y=\"%f\" z=\"%f\" radius=\"%f\" colour=\"%s\" kd=\"0.8\" kS=\"1.2\" alphaS=\"50\"/>\n", position.x, position.y, position.z, radius, colorName);
    }
}
