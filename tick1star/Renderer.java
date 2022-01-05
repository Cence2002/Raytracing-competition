package uk.ac.cam.cl.gfxintro.bh525.tick1star;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Renderer {

    // Distributed shadow ray constants
    private final boolean USE_SOFT_SHADOW = true; //TODO finish this
    private final int SHADOW_RAY_COUNT = 1; // no. of spawned
    private final double LIGHT_SIZE = 0; // size of spherical light

    // Distributed depth-of-field constants
    private final boolean USE_DOF = false;
    private final int DOF_RAY_COUNT = 5; // no. of spawned DoF rays
    private final double DOF_FOCAL_PLANE = 8.8; // focal length of camera
    private final double DOF_AMOUNT = 0.30; // amount of DoF effect

    // The width and height of the image in pixels
    private int width, height;

    // Bias factor for reflected and shadow rays
    private final double EPSILON = 0.0001;

    // The number of times a ray can bounce for reflection
    private int bounces;

    // Background colour of the image
    private ColorRGB backgroundColor = new ColorRGB(0.001);

    public HashSet<SceneObject> visibleObjects = new HashSet<>();
    public HashMap<Integer, Integer> visibleObjectHashes = new HashMap<>();

    public Renderer(int width, int height, int bounces) {
        this.width = width;
        this.height = height;
        this.bounces = bounces;
    }

    /*
     * Trace the ray through the supplied scene, returning the colour to be rendered.
     * The bouncesLeft parameter is for rendering reflective surfaces.
     */
    public static int hashPos(Vector3 position) {
        Vector3 pos = position.scale(123243);
        int xx = (int) pos.x;
        int yy = (int) pos.y;
        int zz = (int) pos.z;
        int val = xx * 103 + 1234 * yy - 27385345 * zz + 123124;
        return val;
    }

    protected ColorRGB trace(Scene scene, Ray ray, int bouncesLeft) {

        // Find closest intersection of ray in the scene
        RaycastHit closestHit = scene.findClosestIntersection(ray);

        // If no object has been hit, return a background colour
        SceneObject object = closestHit.getObjectHit();
        if (object == null) {
            return backgroundColor;
        }

        visibleObjects.add(object);
        if (true) {
            int val = 0;//hashPos(((Sphere) object).position);
            if (visibleObjectHashes.containsKey(val)) {
                visibleObjectHashes.put(val, visibleObjectHashes.get(val) + 1);
            } else {
                visibleObjectHashes.put(val, 1);
            }
        }

        // Otherwise calculate colour at intersection and return
        // Get properties of surface at intersection - location, surface normal
        Vector3 P = closestHit.getLocation();
        Vector3 N = closestHit.getNormal();
        Vector3 O = ray.getOrigin();

        // Illuminate the surface

        // Calculate direct illumination at the point
        ColorRGB directIllumination = this.illuminate(scene, object, P, N, O);
        // Get reflectivity of object
        double reflectivity = object.getReflectivity();
        if (bouncesLeft == 0 || reflectivity == 0) {
            // Base case - if no bounces left or non-reflective surface
            return directIllumination;
        } else { // Recursive case
            ColorRGB reflectedIllumination;
            Vector3 R = (O.subtract(P)).reflectIn(N).normalised();

            Ray reflectedRay = new Ray(P.add(R.scale(EPSILON)), R);

            reflectedIllumination = trace(scene, reflectedRay, bouncesLeft - 1);


            // Scale direct and reflective illumination to conserve light
            directIllumination = directIllumination.scale(1.0 - reflectivity);
            reflectedIllumination = reflectedIllumination.scale(reflectivity);
            // Return total illumination
            return directIllumination.add(reflectedIllumination);
        }
    }

    /*
     * Illuminate a surface on and object in the scene at a given position P and surface normal N,
     * relative to ray originating at O
     */
    private ColorRGB illuminate(Scene scene, SceneObject object, Vector3 P, Vector3 N, Vector3 O) {

        ColorRGB colourToReturn = new ColorRGB(0);

        ColorRGB I_a = scene.getAmbientLighting(); // Ambient illumination intensity

        ColorRGB C_diff = object.getColour(P); // Diffuse colour defined by the object

        // Get Phong coefficients
        double k_d = object.getPhong_kD();
        double k_s = object.getPhong_kS();
        double alpha = object.getPhong_alpha();

        colourToReturn = colourToReturn.add(C_diff.scale(I_a));

        // Loop over each point light source
        List<PointLight> pointLights = scene.getPointLights();
        for (int i = 0; i < pointLights.size(); i++) {
            PointLight light = pointLights.get(i); // Select point light

            // Calculate point light constants
            double distanceToLight = light.getPosition().subtract(P).magnitude();
            ColorRGB C_spec = light.getColour();
            ColorRGB I = light.getIlluminationAt(distanceToLight);


            N = N.normalised();
            Vector3 L = (light.getPosition().subtract(P)).normalised();
            Vector3 V = (O.subtract(P)).normalised();
            Vector3 R = (L.scale(-1)).add(N.scale(2 * L.dot(N)));


            int visibleCount = 0;
            for (int j = 0; j < SHADOW_RAY_COUNT; j++) {
                Vector3 offset = Vector3.randomInsideUnitSphere().scale(LIGHT_SIZE);
                Vector3 lightPos = light.getPosition().add(offset);
                Vector3 L2 = (lightPos.subtract(P)).normalised();
                Ray shadowRay = new Ray(P.add(N.scale(EPSILON)), L2);
                RaycastHit firstHit = scene.findClosestIntersection(shadowRay);
                double distanceFromLight = (lightPos.subtract(P)).magnitude();
                if (firstHit.getDistance() >= distanceFromLight) {
                    // Point P is in shadow (behind an object from light)
                    visibleCount++;
                }
            }

            double visibleRatio = (double) visibleCount / SHADOW_RAY_COUNT;

            ColorRGB diffuse = C_diff.scale(k_d).scale(I).scale(Math.max(0, N.dot(L)));
            ColorRGB specular = C_spec.scale(k_s).scale(I).scale(Math.pow(Math.max(0, R.dot(V)), alpha));
            colourToReturn = colourToReturn.add(diffuse.scale(visibleRatio));
            colourToReturn = colourToReturn.add(specular.scale(visibleRatio));
        }
        return colourToReturn;
    }

    // Render image from scene, with camera at origin
    public BufferedImage render(Scene scene) {

        // Set up image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Set up camera
        Camera camera = new Camera(width, height);

        // Loop over all pixels
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                Ray ray = camera.castRay(x, y); // Cast ray through pixel
                ColorRGB sum = new ColorRGB(0);
                if (USE_DOF) {
                    double scale = DOF_FOCAL_PLANE / ray.getDirection().z;
                    Vector3 focal_plane_point = new Vector3(
                            ray.getDirection().x * scale,
                            ray.getDirection().y * scale,
                            DOF_FOCAL_PLANE
                    );
                    for (int i = 0; i < DOF_RAY_COUNT; i++) {
                        Vector3 from = ray.getOrigin().add(new Vector3(
                                -DOF_AMOUNT / 2 + DOF_AMOUNT * Math.random(),
                                -DOF_AMOUNT / 2 + DOF_AMOUNT * Math.random(),
                                0));
                        Vector3 direction = focal_plane_point.subtract(from).normalised();
                        Ray DOF_ray = new Ray(from, direction);
                        ColorRGB linearRGB = trace(scene, DOF_ray, bounces); // Trace path of cast ray and determine colour
                        ColorRGB gammaRGB = tonemap(linearRGB);
                        sum = sum.add(gammaRGB);
                    }
                    sum = sum.scale(1.0 / DOF_RAY_COUNT);
                } else {
                    ColorRGB linearRGB = trace(scene, ray, bounces); // Trace path of cast ray and determine colour
                    ColorRGB gammaRGB = tonemap(linearRGB);
                    sum = sum.add(gammaRGB);
                }
                image.setRGB(x, y, sum.toRGB()); // Set image colour to traced colour
            }
            // Display progress every 10 lines
            if (y % 4 == 3 | y == (height - 1))
                System.out.println(String.format("%.2f", 100 * y / (float) (height - 1)) + "% completed");
        }
        return image;
    }


    // Combined tone mapping and display encoding
    public ColorRGB tonemap(ColorRGB linearRGB) {
        double invGamma = 1. / 2.2;
        double a = 2;  // controls brightness
        double b = 1.3; // controls contrast

        // Sigmoidal tone mapping
        ColorRGB powRGB = linearRGB.power(b);
        ColorRGB displayRGB = powRGB.scale(powRGB.add(Math.pow(0.5 / a, b)).inv());

        // Display encoding - gamma
        ColorRGB gammaRGB = displayRGB.power(invGamma);

        return gammaRGB;
    }
}
