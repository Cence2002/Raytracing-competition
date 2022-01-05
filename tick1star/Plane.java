package uk.ac.cam.cl.gfxintro.bh525.tick1star;

public class Plane extends SceneObject {

    // Plane constants
    private final double DEFAULT_PLANE_KD = 0.0;
    private final double DEFAULT_PLANE_KS = 0.0;
    private final double DEFAULT_PLANE_ALPHA = 0.0;
    private final double DEFAULT_PLANE_REFLECTIVITY = 1;

    // A point in the plane
    public Vector3 point;

    // The normal of the plane
    public Vector3 normal;

    public Plane(Vector3 point, Vector3 normal, ColorRGB colour) {
        this.point = point;
        this.normal = normal;
        this.colour = colour;

        this.phong_kD = DEFAULT_PLANE_KD;
        this.phong_kS = DEFAULT_PLANE_KS;
        this.phong_alpha = DEFAULT_PLANE_ALPHA;
        this.reflectivity = DEFAULT_PLANE_REFLECTIVITY;
    }

    public Plane(Vector3 point, Vector3 normal, ColorRGB colour, double kD, double kS, double alphaS, double reflectivity) {
        this.point = point;
        this.normal = normal;
        this.colour = colour;

        this.phong_kD = kD;
        this.phong_kS = kS;
        this.phong_alpha = alphaS;
        this.reflectivity = reflectivity;
    }

    // Intersect this plane with ray
    @Override
    public RaycastHit intersectionWith(Ray ray) {
        // Get ray parameters
        Vector3 O = ray.getOrigin();
        Vector3 D = ray.getDirection();

        // Get plane parameters
        Vector3 Q = this.point;
        Vector3 N = this.normal;

        double s = (Q.subtract(O)).dot(N) / (D.dot(N));
        if (s >= 0) {
            double distance = s * D.magnitude();
            Vector3 location = O.add(D.scale(s));
            Vector3 normal = N.normalised();
            return new RaycastHit(this, distance, location, normal);
        }

        return new RaycastHit();
    }

    // Get normal to the plane
    @Override
    public Vector3 getNormalAt(Vector3 position) {
        return normal; // normal is the same everywhere on the plane
    }

    public String toXML() {
        return String.format("<plane x=\"%f\" y=\"%f\" z=\"%f\" nx=\"%f\" ny=\"%f\" nz=\"%f\" colour=\"#111111\" kS=\"0.8\"/>\n", point.x, point.y, point.z, normal.x, normal.y, normal.z);
    }

    @Override
    public Sphere invert(Sphere base) {
        return null;
    }
}
