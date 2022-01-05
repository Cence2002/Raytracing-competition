package uk.ac.cam.cl.gfxintro.bh525.tick1star;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Scene {
    public final static ColorRGB white = new ColorRGB(1);
    public final static ColorRGB red = new ColorRGB(1, 0, 0);
    public final static ColorRGB green = new ColorRGB(0, 1, 0);
    public final static ColorRGB blue = new ColorRGB(0, 0, 1);
    public final static ColorRGB yellow = new ColorRGB(1, 1, 0);
    public final static ColorRGB orange = new ColorRGB(1, 0.5, 0);
    public final static ColorRGB purple = new ColorRGB(1, 0, 1);
    public final static ColorRGB gray = new ColorRGB(0.5);

    // A list of 3D objects to be rendered
    private List<SceneObject> objects;

    // A list of point light sources
    private List<PointLight> pointLights;

    // The color of the ambient light in the scene
    private ColorRGB ambientLight;

    public Scene() {
        objects = new LinkedList<SceneObject>();
        pointLights = new LinkedList<PointLight>();
        ambientLight = new ColorRGB(1);
    }

    public void addObject(SceneObject object) {
        objects.add(object);
    }

    // Find the closest intersection of given ray with an object in the scene
    public RaycastHit findClosestIntersection(Ray ray) {
        RaycastHit closestHit = new RaycastHit(); // initially no intersection

        // Loop over objects and find closest intersection
        for (SceneObject object : objects) {
            RaycastHit trialHit = object.intersectionWith(ray);
            if (trialHit.getDistance() < closestHit.getDistance()) {
                closestHit = trialHit;
            }
        }
        return closestHit;
    }

    public ColorRGB getAmbientLighting() {
        return ambientLight;
    }

    public void setAmbientLight(ColorRGB ambientLight) {
        this.ambientLight = ambientLight;
    }

    public PointLight getPointLight() {
        return pointLights.get(0);
    }

    public List<PointLight> getPointLights() {
        return pointLights;
    }

    public void addPointLight(PointLight pointLight) {
        pointLights.add(pointLight);
    }

    public String toXML() {
        String result = "\n";
        result += "<scene>\n";
        result += "<ambient-light colour=\"#FFFFFF\" intensity=\"0.05\"/>\n";
        for (PointLight pointLight : pointLights) {
            result += pointLight.toXML();
        }
        for (SceneObject sceneObject : objects) {
            result += sceneObject.toXML();
        }
        result += "</scene>\n";
        return result;
    }

    public static ArrayList<Sphere> fractal2(Vector3 v, double size, double r, double dist, double d_size, int iterationCount) {
        ArrayList<Sphere> result = new ArrayList<>();
        if (iterationCount == 0) {
            return result;
        }
        Sphere sphere = new Sphere(v, size * r, white);
        result.add(sphere);

        double n = 6;
        double randomAngle = Math.random() * Math.PI * 2;
        for (int i = 0; i < n; i++) {
            double angle = 2 * Math.PI * i / n + randomAngle;
            result.addAll(fractal2(
                    v.add(Vector3.rotateVector(
                            new Vector3(size * dist, 0, 0),
                            new Vector3(0, 1, 0),
                            angle)),
                    size * d_size, r, dist, d_size, iterationCount - 1));
        }

        return result;
    }

    public static Scene create12() throws IOException {
        Scene scene = new Scene();

        scene.ambientLight = new ColorRGB(0.02);

        ArrayList<Sphere> spheres = new ArrayList<>();
        ArrayList<Plane> planes = new ArrayList<>();
        spheres.addAll(fractal2(new Vector3(0, 0, 0), 0.8, 0.3, 0.8, 0.4, 3));
        planes.add(new Plane(new Vector3(0, 0, 3), new Vector3(1, 0, 0.4), white));
        planes.add(new Plane(new Vector3(0, 0, 3), new Vector3(-1, 0, 0.4), white));


        Vector3 translate = new Vector3(0, -0.8, 8);
        for (Plane plane : planes) {
            plane.normal = Vector3.rotateVector(plane.normal, new Vector3(1, 0, 0), -0.4);
            plane.point = Vector3.rotateVector(plane.point, new Vector3(1, 0, 0), -0.4);
            plane.point = plane.point.add(translate);
        }
        for (Sphere sphere : spheres) {
            sphere.position = Vector3.rotateVector(sphere.position, new Vector3(1, 0, 0), -0.4);
            sphere.position = sphere.position.add(translate);
        }
        for (Plane plane : planes) scene.addObject(plane);
        for (Sphere sphere : spheres) scene.addObject(sphere);
        /*ColorRGB[][] earth = TexturedSphere.calculateTextureMap("textures/planets/2k_mars.png");
        for (Sphere sphere : spheres) {
            TexturedSphere temp = new TexturedSphere(sphere.position, sphere.radius, sphere.colour, earth, "2k_mars.png");
            temp.rotateRandomly();
            scene.addObject(temp);
        }

        ColorRGB[][] sun = TexturedSphere.calculateTextureMap("textures/planets/2k_sun.png");
        for (int i = 0; i < sun.length; i++) {
            for (int j = 0; j < sun[i].length; j++) {
                sun[i][j] = sun[i][j];
            }
        }
        TexturedSphere bigSun = new TexturedSphere(new Vector3(0.7, 0.3, 0.75), 0.58, white, sun, "2k_sun.png");
        scene.addObject(bigSun);*/

        //scene.addPointLight(new PointLight(new Vector3(5, 3, 5), new ColorRGB(1, 0.9, 0.3), 250));
        //scene.addPointLight(new PointLight(new Vector3(0, 0, 0), new ColorRGB(1, 0.7, 0.7), 20));
        //scene.addPointLight(new PointLight(new Vector3(2, 2, 5), new ColorRGB(1, 0.8, 0.5), 40));
        scene.addPointLight(new PointLight(new Vector3(0, 0, 0), new ColorRGB(0, 1, 0.5), 200));

        /*ColorRGB[][] stars = TexturedSphere.calculateTextureMap("textures/planets/2k_stars_milky_way.png");
        for (int i = 0; i < stars.length; i++) {
            for (int j = 0; j < stars[i].length; j++) {
                stars[i][j] = stars[i][j].scale(10);
            }
        }
        InsideOutSphere background = new InsideOutSphere(new Vector3(0, 0, 800), 1000, white, stars, "2k_stars_milky_way.png");
        scene.addObject(background);*/

        System.out.println("Number of objects: " + scene.objects.size());
        return scene;
    }

    public static Scene create11() {
        Scene scene = new Scene();
        scene.ambientLight = new ColorRGB(0.02);


        ArrayList<PointLight> pointLights = new ArrayList<>();
        pointLights.add(new PointLight(new Vector3(-1, -1, -2), white, 90));
        pointLights.add(new PointLight(new Vector3(1, 3, 4), white, 100));
        pointLights.add(new PointLight(new Vector3(2, 2, 5), white, 80));
        //scene.addPointLight(new PointLight(new Vector3(-2, -2, 4), white, 30));

        ColorRGB[] colors = new ColorRGB[]{blue, purple, orange, yellow};
        ColorRGB[] colors2 = new ColorRGB[]{
                new ColorRGB(30, 255, 23),
                new ColorRGB(45, 236, 145),
                new ColorRGB(229, 125, 0),
                new ColorRGB(140, 26, 135),
                new ColorRGB(83, 179, 203)};
        ColorRGB[] colors3 = new ColorRGB[]{
                new ColorRGB(45, 216, 129),
                new ColorRGB(229, 149, 0),
                new ColorRGB(160, 26, 125),
                new ColorRGB(83, 179, 203)};
        ColorRGB[] colors4 = new ColorRGB[]{
                new ColorRGB(255, 225, 86),
                new ColorRGB(212, 81, 19),
                new ColorRGB(23, 163, 152),
                new ColorRGB(55, 119, 255)};
        ColorRGB[] colors5 = new ColorRGB[]{
                new ColorRGB(10, 36, 99),
                new ColorRGB(251, 54, 64),
                new ColorRGB(30, 255, 188),
                new ColorRGB(251, 255, 18)};
        ColorRGB[] colors6 = new ColorRGB[]{
                new ColorRGB(33, 225, 88),
                new ColorRGB(9, 43, 250),
                new ColorRGB(230, 27, 207),
                new ColorRGB(255, 242, 0)};

        ColorRGB[] cols = colors6;
        for (int i = 0; i < cols.length; i++) {
            cols[i] = cols[i].scale(1.0 / 256);
        }

        String[] colNames = new String[]{"#21E158", "#092BFA", "#E61BCF", "#FFF200"};

        double r1 = Math.sqrt(0.5);
        double r2 = Math.sqrt(0.5) + 1;
        double r3 = Math.sqrt(2) + 1;

        ArrayList<Sphere> spheres = new ArrayList<>();
        spheres.add(new Sphere(new Vector3(0, 0, 0), r2, cols[0]).setColorName(colNames[0]));
        spheres.add(new Sphere(new Vector3(1, 0, 0), r1, cols[0]).setColorName(colNames[0]));
        spheres.add(new Sphere(new Vector3(-1, 0, 0), r1, cols[0]).setColorName(colNames[0]));
        spheres.add(new Sphere(new Vector3(0, 1, 0), r1, cols[0]).setColorName(colNames[0]));
        spheres.add(new Sphere(new Vector3(0, -1, 0), r1, cols[0]).setColorName(colNames[0]));
        spheres.add(new Sphere(new Vector3(0, 0, 1), r1, cols[0]).setColorName(colNames[0]));
        spheres.add(new Sphere(new Vector3(0, 0, -1), r1, cols[0]).setColorName(colNames[0]));

        ArrayList<Sphere> bases = new ArrayList<>();
        bases.add(new Sphere(new Vector3(r2, r2, r2), r3, white));
        bases.add(new Sphere(new Vector3(r2, r2, -r2), r3, white));
        bases.add(new Sphere(new Vector3(r2, -r2, r2), r3, white));
        bases.add(new Sphere(new Vector3(r2, -r2, -r2), r3, white));
        bases.add(new Sphere(new Vector3(-r2, r2, r2), r3, white));
        bases.add(new Sphere(new Vector3(-r2, r2, -r2), r3, white));
        bases.add(new Sphere(new Vector3(-r2, -r2, r2), r3, white));
        bases.add(new Sphere(new Vector3(-r2, -r2, -r2), r3, white));

        ArrayList<Sphere> newSpheres = new ArrayList<>();

        ArrayList<Sphere> allInverted = new ArrayList<>();

        for (int iteration = 0; iteration < 3; iteration++) {
            ColorRGB colour = cols[iteration + 1];
            newSpheres = new ArrayList<>();
            for (Sphere sphere : spheres) {
                for (Sphere base : bases) {
                    Sphere inverted = sphere.invert(base);
                    inverted.colour = colour;
                    inverted.colorName = colNames[iteration + 1];
                    newSpheres.add(inverted);
                }
            }
            spheres.addAll(newSpheres);

            if (iteration == 0) {
                Sphere border = new Sphere(new Vector3(0, 0, 0), 2.5, white);
                for (Sphere sphere : spheres) {
                    Sphere inverted = sphere.invert(border);
                    inverted.colorName = "#333333";
                    allInverted.add(inverted);
                }
            }
        }
        spheres.addAll(allInverted);

        newSpheres = new ArrayList<>();
        for (Sphere sphere : spheres) {
            boolean unique = true;
            for (Sphere newSphere : newSpheres) {
                if (sphere.position.subtract(newSphere.position).magnitude() < 0.001 && Math.abs(sphere.radius - newSphere.radius) < 0.001) {
                    unique = false;
                    break;
                }
            }
            if (unique) {
                newSpheres.add(sphere);
            }
        }
        spheres = newSpheres;

        Sphere remove = new Sphere(new Vector3(1, 5 + 1000, -2), 4, white);

        newSpheres = new ArrayList<>();
        for (Sphere sphere : spheres) {
            boolean okay = true;
            if (sphere.radius < 0.07) {
                okay = false;
            }
            if (sphere.radius > 0.7) {
                //okay = false;
            }
            if (sphere.radius == r2 && sphere.position.magnitude() < 0.01) {
                okay = false;
            }
            if (sphere.position.subtract(remove.position).magnitude() < sphere.radius + remove.radius) {
                okay = false;
            }
            if (sphere.position.magnitude() > 2) {
                sphere.colour = new ColorRGB(0.2);
            }
            if (okay) {
                newSpheres.add(sphere);
            }
        }
        spheres = newSpheres;

        for (Sphere sphere : spheres) {
            sphere.position = Vector3.rotateVector(sphere.position, new Vector3(0, 1, 0), 0.15);
            sphere.position = Vector3.rotateVector(sphere.position, new Vector3(1, 0, 0), -0.5);
            sphere.position = sphere.position.scale(1);
            sphere.position = sphere.position.add(new Vector3(0, 0, 10));
        }
        for (PointLight pointLight : pointLights) {
            //pointLight.position = Vector3.rotateVector(pointLight.position, new Vector3(0, 1, 0), 0.1);
            //pointLight.position = Vector3.rotateVector(pointLight.position, new Vector3(1, 0, 0), -0.5);
            //pointLight.position = pointLight.position.add(new Vector3(0, 0, 10));
        }

        remove = new Sphere(new Vector3(0, 0, 4), 4, white);

        newSpheres = new ArrayList<>();
        for (Sphere sphere : spheres) {
            boolean okay = true;

            if (sphere.position.magnitude() < sphere.radius * 0.8) {
                okay = false;
            }
            if (sphere.position.subtract(remove.position).magnitude() < sphere.radius + remove.radius) {
                okay = false;
            }


            int val = Renderer.hashPos(sphere.position);
            int[] visible = new int[]{-1295128372, -889574151, 1480989250, 242770558, 1786696823, 1564095283, -31833065, 1203711921, 2114726430, -1778953244, -40519807, -749618876, 106839624, 1228033316, 961439060, 19823413, -1683864642, 848419505, -921045189, -374010985, -998394279, -315612394, -18370880, 868861921, -2121170503, -1353312901, -1439927293, 1780120106, 129606541, -700669430, 648044700, 170053148, -973752395, -839685194, -1062414615, -1245326961, -305424000, 412264156, 142983883, 1359679321, -75315754, -1056416431, -1831591281, 1077783649, -815907581, -1072312757, -965814270, -348598442, 1175152229, 1811666827, -962653459, 1687456999, -969651646, -1650354213, 1361939287, -2119989470, -1537348904, -174819044, -1589795916, -95649595, -18815893, -1414590176, 92180977, 268746902, 1207026019, -61667130, 1046089410, -193041180, -1550591729, -381704293, -1735561175, 1952497355, 1776811351, -1039857484, -1547211920, 1715490042, 46882929, -668487524, 1667112857, 638841050, 467731752, -526562218, -1265723839, 307935371, 560379319, 1060037626, 1391117887, 1071566600, -355751396, -1808941325, 530335610, 1253584466, 1231026101, -1891430481, -497469272, -1778422516, -1050745942, 992131028, 1400061832, -969734857, 333989615, -639333647, 583609310, -1310363918, 452297708, -323682899, 33672991, 521226767, 1634888812, -275610484, -179861416, -2110713067, -2108749461, 1321600994, -2094440445, 42096044, -529385636, 1336193684, -2058143646, 1781388057, -375416661, 729611847, -814607793, 481669621, 1932393582, 554091584, 1221649815, -989901384, 308797230, 1364466206, 883644385, -945826092, -1129464324, 489302649, 649143268, -1486443984, 1523377556, 1271656083, -1814524282, -986921393, -1037931193, 423445082, 446204403, 1463629395, -1561942634, -1237721266, -788258182, -444751870, -418247020, -2082950064, 1362688177, -426579172, -861358809, 1392752785, -1095037655, 318003555, 1744392806, -1646315910, -621002407, 1035066388, 1615707114, 357749493, 130588257, 671492524, -62494210, 1593148749, 1344751517, 1323021671, 471132073, -1537066532, 1411061208, -1516760995, 194218587, 285138738, -1359316181, 1931461321, 979383735, 1336506992, -973270, 748360000, -771832289, -824400579, 966430329, -678536600, 1707793964, -315166499, -70099905, 2118284204, -1127959755, -1315070356, -1147530129, -1996466954, 178622813, -175237513, 1250887028, -245406556, -1289887514, -1731266253, 501251102};
            boolean existsInVisible = false;
            for (int i : visible) {
                if (val == i) existsInVisible = true;
            }
            if (!existsInVisible) okay = false;
            if (okay) {
                newSpheres.add(sphere);
            }
        }
        spheres = newSpheres;

        for (PointLight pointLight : pointLights) {
            scene.addPointLight(pointLight);
        }
        for (Sphere sphere : spheres) {
            scene.addObject(sphere);
        }


        //scene.addObject(new Plane(new Vector3(0, 0, 25), new Vector3(0, 0, -1), white));
        System.out.println("Number of objects: " + scene.objects.size());
        return scene;
    }

    public static Scene create10() throws IOException {
        Scene scene = new Scene();

        scene.ambientLight = new ColorRGB(0.03);

        ArrayList<Sphere> spheres = new ArrayList<>();
        //spheres.addAll(fractal2(new Vector3(0, 0, 0), 1, 4));


        for (Sphere sphere : spheres) {
            sphere.position = Vector3.rotateVector(sphere.position, new Vector3(0, 1, 0), 0.25);
            sphere.position = Vector3.rotateVector(sphere.position, new Vector3(1, 0, 0), -0.1);
            //sphere.position = Vector3.rotateVector(sphere.position, new Vector3(0, 0, 1), -0.1);
            sphere.position = sphere.position.add(new Vector3(-1.3, -0.5, 15));
        }
        ColorRGB[][] earth = TexturedSphere.calculateTextureMap("textures/planets/2k_mars.png");
        for (Sphere sphere : spheres) {
            TexturedSphere temp = new TexturedSphere(sphere.position, sphere.radius, sphere.colour, earth, "2k_mars.png");
            temp.rotateRandomly();
            scene.addObject(temp);
        }

        ColorRGB[][] sun = TexturedSphere.calculateTextureMap("textures/planets/2k_sun.png");
        for (int i = 0; i < sun.length; i++) {
            for (int j = 0; j < sun[i].length; j++) {
                sun[i][j] = sun[i][j];
            }
        }
        TexturedSphere bigSun = new TexturedSphere(new Vector3(0.7, 0.3, 0.75), 0.58, white, sun, "2k_sun.png");
        scene.addObject(bigSun);

        scene.addPointLight(new PointLight(new Vector3(5, 3, 5), new ColorRGB(1, 0.9, 0.3), 250));
        scene.addPointLight(new PointLight(new Vector3(0, 0, 0), new ColorRGB(1, 0.7, 0.7), 20));
        scene.addPointLight(new PointLight(new Vector3(2, 2, 5), new ColorRGB(1, 0.8, 0.5), 40));

        ColorRGB[][] stars = TexturedSphere.calculateTextureMap("textures/planets/2k_stars_milky_way.png");
        for (int i = 0; i < stars.length; i++) {
            for (int j = 0; j < stars[i].length; j++) {
                stars[i][j] = stars[i][j].scale(10);
            }
        }
        InsideOutSphere background = new InsideOutSphere(new Vector3(0, 0, 800), 1000, white, stars, "2k_stars_milky_way.png");
        scene.addObject(background);

        System.out.println("Number of objects: " + scene.objects.size());
        return scene;
    }

    //Planets
    public static Scene create9() throws IOException {
        Scene scene = new Scene();

        scene.ambientLight = new ColorRGB(0.03);

        double r1 = Math.sqrt(0.5);
        double r2 = Math.sqrt(0.5) + 1;
        double r3 = Math.sqrt(2) + 1;

        ArrayList<Sphere> bases = new ArrayList<>(Arrays.asList(
                new Sphere(new Vector3(r2, r2, r2), r3, white),
                new Sphere(new Vector3(r2, r2, -r2), r3, white),
                new Sphere(new Vector3(r2, -r2, r2), r3, white),
                new Sphere(new Vector3(r2, -r2, -r2), r3, white),
                new Sphere(new Vector3(-r2, r2, r2), r3, white),
                new Sphere(new Vector3(-r2, r2, -r2), r3, white),
                new Sphere(new Vector3(-r2, -r2, r2), r3, white),
                new Sphere(new Vector3(-r2, -r2, -r2), r3, white)
        ));

        int maxIterations = 3;
        int invertIteration = 1;
        ArrayList<Sphere> allInverted = new ArrayList<>();
        ArrayList<Sphere>[] iterations = new ArrayList[maxIterations + 1];

        iterations[0] = new ArrayList<>(Arrays.asList(
                new Sphere(new Vector3(0, 0, 0), r2, white).setLabel(0),
                new Sphere(new Vector3(1, 0, 0), r1, white).setLabel(0),
                new Sphere(new Vector3(-1, 0, 0), r1, white).setLabel(0),
                new Sphere(new Vector3(0, 1, 0), r1, white).setLabel(0),
                new Sphere(new Vector3(0, -1, 0), r1, white).setLabel(0),
                new Sphere(new Vector3(0, 0, 1), r1, white).setLabel(0),
                new Sphere(new Vector3(0, 0, -1), r1, white).setLabel(0)
        ));

        ArrayList<Sphere> spheres = new ArrayList<>(iterations[0]);
        for (int iteration = 1; iteration <= maxIterations; iteration++) {
            iterations[iteration] = new ArrayList<>();
            for (Sphere sphere : spheres) {
                for (Sphere base : bases) {
                    iterations[iteration].add(sphere.invert(base).setLabel(iteration));
                }
            }
            spheres.addAll(iterations[iteration]);

            if (iteration == invertIteration) {
                Sphere border = new Sphere(new Vector3(0, 0, 0), 2.5, white);
                for (Sphere sphere : spheres) {
                    allInverted.add(sphere.invert(border).setLabel(-1));
                }
            }
        }
        spheres.addAll(allInverted);

        ArrayList<Sphere> newSpheres = new ArrayList<>();
        for (Sphere sphere : spheres) {
            boolean unique = true;
            for (Sphere newSphere : newSpheres) {
                if (sphere.position.subtract(newSphere.position).magnitude() < 0.001 &&
                        Math.abs(sphere.radius - newSphere.radius) < 0.001) {
                    unique = false;
                }
            }
            if (unique) newSpheres.add(sphere);
        }
        spheres = newSpheres;

        newSpheres = new ArrayList<>();
        for (Sphere sphere : spheres) {
            boolean okay = true;
            if (sphere.radius < 0.065) okay = false;
            if (sphere.radius == r2 && sphere.position.magnitude() < 0.01) okay = false;
            if (sphere.position.magnitude() > r2 * 1.1) sphere.colour = gray;

            if (okay) newSpheres.add(sphere);
        }
        spheres = newSpheres;

        for (Sphere sphere : spheres) {
            sphere.position = Vector3.rotateVector(sphere.position, new Vector3(0, 1, 0), 0.25);
            sphere.position = Vector3.rotateVector(sphere.position, new Vector3(1, 0, 0), -0.4);
            //sphere.position = Vector3.rotateVector(sphere.position, new Vector3(0, 0, 1), -0.1);
            sphere.position = sphere.position.add(new Vector3(-1.3, -0.5, 9));
        }

        Sphere remove = new Sphere(new Vector3(0, 100000, 4), 4, white);
        newSpheres = new ArrayList<>();
        for (Sphere sphere : spheres) {
            boolean okay = true;

            if (sphere.position.magnitude() < sphere.radius) okay = false;
            if (sphere.position.subtract(remove.position).magnitude() < sphere.radius + remove.radius) okay = false;
            //if (sphere.position.z > 9.1) okay = false;
            int[] justVisible = new int[]{-278600875, -808391239, 420797800, 77070566, 1789875790, -572257562, -1881751354, 1109833632, 640763280, -284334934, -1507098224, 576854948, 2144973362};
            int val = Renderer.hashPos(sphere.position);
            for (int i : justVisible) {
                if (i == val) okay = false;
            }

            int[] visible = new int[]{597335448, 2143700931, -1652393083, -475534417, -989260030, 1564346152, 26804608, 1093479222, -816093376, -636318708, -1747360258, -2063626033, -1633757013, -355191583, 224472921, -485471958, -1267774383, 1225158983, -102569565, 1029173013, 1346453511, -1849896971, -1366257956, -2004657969, -1365242127, 1062381316, 919728777, 1017854707, -870307716, -175638053, 2051578389, -1986405383, -290855733, 423570263, -1195597611, 1620679927, -289849641, 1399137560, 1493620100, 477000424, 2117382845, -1749759171, -1456384581, 1718545634, 164298054, 699928885, 1037941074, 1982882465, 1818795259, 881634838, 1518813734, 1081534165, 209244380, 134488236, 1640719714, -1156513859, 2060675168, -1709410135, 2018598116, 786835035, 66305356, -2147256638, -1697335787, -1834554268, 2147066685, -1047945906, -643732630, 789772766, 769762093, -1384633417, -1250578523, -220031440, 419939285, -1297709456, -1789579901, 1408830753, -244678222, -1679162061, -2043207954, -385184810, -1471491436, 1088141374, 1159822281, 981668459, 891231728, 123124, 890058230, 1185616809, 692376641, -724874785, -1757330859, 790097922, 568524539, 1952864122, -268052200, -1143682314, 24476247, 2004141661, 891828737, 132296904, -489149454, 892209664, 1708283132, 1212659575, 122162810, -2023591850, 203625826, -1807786116, 168417098, -1181838643, 767852672, -1463304317, -331030260, 2022236096, -387121756, 1331932719, -2056647129, -1531812353, -1327590000, 1762430530, 285172143, 696944861, -695646254, 468521648, -1006161062, 1884631304, -1189102016, -2061476288, -1263836725, 1399880213, -1132111383, 2128923016, -1687223805, -927353900, -498800332, 2123830244, 564893404, 207971100, -838860419, 239972147, 993996353, -544660981, -986853202, -1839029782, 724393123, 125520109, 2019577342, 515823904, 1137635951, -1263780594, -1934815989, -976817554, -291065076, -1599408896, -1666956017, 1862540968, 294116393, 1130190574, 25899071, 559632621, -788333900, -1491839314, 1531696881, -342938062, -1424382300, 1464944787, 1070383118, -31360542, -1321050994, 2079608377, 749678946, 1604962919, 1896884928, 122487966, -398403177, -1684736436, 1601678511, -1940543603, 1706874972, 1207743005, 923955440, 609907640, -736942096, 1383418256, 221524690, 1035215964, 370690032, 1129565372, -358357677, -727599792, -1340036142, -380408671, 501844507, 846071191, 1393256178, 178936656, 1538675785, 224599708};
            boolean existsInVisible = false;
            for (int i : visible) {
                if (val == i) existsInVisible = true;
            }
            if (!existsInVisible) okay = false;

            if (okay) newSpheres.add(sphere);
        }
        spheres = newSpheres;

        //ColorRGB[][] tennis = TexturedSphere.calculateTextureMap("textures/balls/tennis.png");
        //ColorRGB[][] basketball = TexturedSphere.calculateTextureMap("textures/balls/basketball.png");
        ColorRGB[][] basketball2 = TexturedSphere.calculateTextureMap("textures/balls/basketball2.png");
        ColorRGB[][] football = TexturedSphere.calculateTextureMap("textures/balls/football2.png");
        //ColorRGB[][] football2 = TexturedSphere.calculateTextureMap("textures/balls/football2.png");
        //ColorRGB[][] golf = TexturedSphere.calculateTextureMap("textures/balls/golf.png");
        //ColorRGB[][] golf2 = TexturedSphere.calculateTextureMap("textures/balls/golf2.png");
        //ColorRGB[][] volleyball = TexturedSphere.calculateTextureMap("textures/balls/volleyball.png");

        ColorRGB[][] earth = TexturedSphere.calculateTextureMap("textures/planets/2k_earth_daymap.png");
        ColorRGB[][] moon = TexturedSphere.calculateTextureMap("textures/planets/2k_moon.png");
        ColorRGB[][] venus = TexturedSphere.calculateTextureMap("textures/planets/2k_venus_surface.png");
        ColorRGB[][] jupiter1 = TexturedSphere.calculateTextureMap("textures/planets/2k_jupiter.png");
        ColorRGB[][] jupiter = TexturedSphere.calculateTextureMap("textures/planets/2k_jupiter.png");
        ColorRGB[][] mars = TexturedSphere.calculateTextureMap("textures/planets/2k_mars.png");
        ColorRGB[][] neptune = TexturedSphere.calculateTextureMap("textures/planets/2k_neptune.png");
        for (Sphere sphere : spheres) {
            TexturedSphere temp = null;
            switch (sphere.label) {
                case -100:
                    System.err.println("not labeled sphere");
                    break;
                case -1:
                    sphere.colour = gray;
                    //scene.addObject(sphere);
                    break;
                case 0:
                    temp = new TexturedSphere(sphere.position, sphere.radius, sphere.colour, earth, "8k_earth_daymap.png");
                    break;
                case 1:
                    temp = new TexturedSphere(sphere.position, sphere.radius, sphere.colour, jupiter1, "8k_jupiter.png");
                    break;
                case 2:
                    temp = new TexturedSphere(sphere.position, sphere.radius, sphere.colour, football, "football2.png");
                    break;
                case 3:
                    temp = new TexturedSphere(sphere.position, sphere.radius, sphere.colour, basketball2, "basketball2.png");
                    break;
                default:
                    System.err.println("not handled label");
            }
            if (temp != null) {
                //temp.rotateRandomly();
                scene.addObject(temp);
            }
        }

        ColorRGB[][] sun = TexturedSphere.calculateTextureMap("textures/planets/2k_sun.png");
        for (int i = 0; i < sun.length; i++) {
            for (int j = 0; j < sun[i].length; j++) {
                sun[i][j] = sun[i][j];
            }
        }
        TexturedSphere bigSun = new TexturedSphere(new Vector3(0.7, 0.3, 0.75), 0.58, white, sun, "8k_sun.png");
        scene.addObject(bigSun);

        scene.addPointLight(new PointLight(new Vector3(5, 3, 5), new ColorRGB(1, 0.9, 0.3), 250));
        scene.addPointLight(new PointLight(new Vector3(0, 0, 0), new ColorRGB(1, 0.7, 0.7), 20));
        scene.addPointLight(new PointLight(new Vector3(2, 2, 5), new ColorRGB(1, 0.8, 0.5), 40));

        ColorRGB[][] stars = TexturedSphere.calculateTextureMap("textures/planets/2k_stars_milky_way.png");
        for (int i = 0; i < stars.length; i++) {
            for (int j = 0; j < stars[i].length; j++) {
                stars[i][j] = stars[i][j].scale(10);
            }
        }
        InsideOutSphere background = new InsideOutSphere(new Vector3(0, 0, 800), 1000, white, stars, "8k_stars_milky.png");
        scene.addObject(background);

        System.out.println("Number of objects: " + scene.objects.size());
        return scene;
    }

    public static Scene create8() throws IOException {
        Scene scene = new Scene();

        scene.ambientLight = new ColorRGB(0.03);

        ArrayList<PointLight> pointLights = new ArrayList<>();
        //pointLights.add(new PointLight(new Vector3(1, -1, -2), white, 90));
        //pointLights.add(new PointLight(new Vector3(1, 3, 4), white, 10));
        //pointLights.add(new PointLight(new Vector3(2, 2, 5), white, 8));
        ////scene.addPointLight(new PointLight(new Vector3(-2, -2, 4), white, 30));
        for (PointLight pointLight : pointLights) scene.addPointLight(pointLight);


        double r1 = Math.sqrt(0.5);
        double r2 = Math.sqrt(0.5) + 1;
        double r3 = Math.sqrt(2) + 1;

        ArrayList<Sphere> bases = new ArrayList<>(Arrays.asList(
                new Sphere(new Vector3(r2, r2, r2), r3, white),
                new Sphere(new Vector3(r2, r2, -r2), r3, white),
                new Sphere(new Vector3(r2, -r2, r2), r3, white),
                new Sphere(new Vector3(r2, -r2, -r2), r3, white),
                new Sphere(new Vector3(-r2, r2, r2), r3, white),
                new Sphere(new Vector3(-r2, r2, -r2), r3, white),
                new Sphere(new Vector3(-r2, -r2, r2), r3, white),
                new Sphere(new Vector3(-r2, -r2, -r2), r3, white)
        ));

        int maxIterations = 3;
        int invertIteration = 1;
        ArrayList<Sphere> allInverted = new ArrayList<>();
        ArrayList<Sphere>[] iterations = new ArrayList[maxIterations + 1];

        iterations[0] = new ArrayList<>(Arrays.asList(
                new Sphere(new Vector3(0, 0, 0), r2, white).setLabel(0),
                new Sphere(new Vector3(1, 0, 0), r1, white).setLabel(0),
                new Sphere(new Vector3(-1, 0, 0), r1, white).setLabel(0),
                new Sphere(new Vector3(0, 1, 0), r1, white).setLabel(0),
                new Sphere(new Vector3(0, -1, 0), r1, white).setLabel(0),
                new Sphere(new Vector3(0, 0, 1), r1, white).setLabel(0),
                new Sphere(new Vector3(0, 0, -1), r1, white).setLabel(0)
        ));

        ArrayList<Sphere> spheres = new ArrayList<>(iterations[0]);
        for (int iteration = 1; iteration <= maxIterations; iteration++) {
            iterations[iteration] = new ArrayList<>();
            for (Sphere sphere : spheres) {
                for (Sphere base : bases) {
                    iterations[iteration].add(sphere.invert(base).setLabel(iteration));
                }
            }
            spheres.addAll(iterations[iteration]);

            if (iteration == invertIteration) {
                Sphere border = new Sphere(new Vector3(0, 0, 0), 2.5, white);
                for (Sphere sphere : spheres) {
                    allInverted.add(sphere.invert(border).setLabel(-1));
                }
            }
        }
        spheres.addAll(allInverted);

        ArrayList<Sphere> newSpheres = new ArrayList<>();
        for (Sphere sphere : spheres) {
            boolean unique = true;
            for (Sphere newSphere : newSpheres) {
                if (sphere.position.subtract(newSphere.position).magnitude() < 0.001 &&
                        Math.abs(sphere.radius - newSphere.radius) < 0.001) {
                    unique = false;
                }
            }
            if (unique) newSpheres.add(sphere);
        }
        spheres = newSpheres;

        newSpheres = new ArrayList<>();
        for (Sphere sphere : spheres) {
            boolean okay = true;
            if (sphere.radius < 0.065) okay = false;
            if (sphere.radius == r2 && sphere.position.magnitude() < 0.01) okay = false;
            if (sphere.position.magnitude() > r2 * 1.1) sphere.colour = gray;

            if (okay) newSpheres.add(sphere);
        }
        spheres = newSpheres;

        for (Sphere sphere : spheres) {
            sphere.position = Vector3.rotateVector(sphere.position, new Vector3(0, 1, 0), 0.25);
            sphere.position = Vector3.rotateVector(sphere.position, new Vector3(1, 0, 0), -0.4);
            //sphere.position = Vector3.rotateVector(sphere.position, new Vector3(0, 0, 1), -0.1);
            sphere.position = sphere.position.add(new Vector3(-1.3, -0.5, 9));
        }

        Sphere remove = new Sphere(new Vector3(0, 100000, 4), 4, white);
        newSpheres = new ArrayList<>();
        for (Sphere sphere : spheres) {
            boolean okay = true;

            if (sphere.position.magnitude() < sphere.radius) okay = false;
            if (sphere.position.subtract(remove.position).magnitude() < sphere.radius + remove.radius) okay = false;
            //if (sphere.position.z > 9.1) okay = false;
            int[] justVisible = new int[]{-278600875, -808391239, 420797800, 77070566, 1789875790, -572257562, -1881751354, 1109833632, 640763280, -284334934, -1507098224, 576854948, 2144973362};
            int val = Renderer.hashPos(sphere.position);
            for (int i : justVisible) {
                if (i == val) okay = false;
            }

            int[] visible = new int[]{597335448, 2143700931, -1652393083, -475534417, -989260030, 1564346152, 26804608, 1093479222, -816093376, -636318708, -1747360258, -2063626033, -1633757013, -355191583, 224472921, -485471958, -1267774383, 1225158983, -102569565, 1029173013, 1346453511, -1849896971, -1366257956, -2004657969, -1365242127, 1062381316, 919728777, 1017854707, -870307716, -175638053, 2051578389, -1986405383, -290855733, 423570263, -1195597611, 1620679927, -289849641, 1399137560, 1493620100, 477000424, 2117382845, -1749759171, -1456384581, 1718545634, 164298054, 699928885, 1037941074, 1982882465, 1818795259, 881634838, 1518813734, 1081534165, 209244380, 134488236, 1640719714, -1156513859, 2060675168, -1709410135, 2018598116, 786835035, 66305356, -2147256638, -1697335787, -1834554268, 2147066685, -1047945906, -643732630, 789772766, 769762093, -1384633417, -1250578523, -220031440, 419939285, -1297709456, -1789579901, 1408830753, -244678222, -1679162061, -2043207954, -385184810, -1471491436, 1088141374, 1159822281, 981668459, 891231728, 123124, 890058230, 1185616809, 692376641, -724874785, -1757330859, 790097922, 568524539, 1952864122, -268052200, -1143682314, 24476247, 2004141661, 891828737, 132296904, -489149454, 892209664, 1708283132, 1212659575, 122162810, -2023591850, 203625826, -1807786116, 168417098, -1181838643, 767852672, -1463304317, -331030260, 2022236096, -387121756, 1331932719, -2056647129, -1531812353, -1327590000, 1762430530, 285172143, 696944861, -695646254, 468521648, -1006161062, 1884631304, -1189102016, -2061476288, -1263836725, 1399880213, -1132111383, 2128923016, -1687223805, -927353900, -498800332, 2123830244, 564893404, 207971100, -838860419, 239972147, 993996353, -544660981, -986853202, -1839029782, 724393123, 125520109, 2019577342, 515823904, 1137635951, -1263780594, -1934815989, -976817554, -291065076, -1599408896, -1666956017, 1862540968, 294116393, 1130190574, 25899071, 559632621, -788333900, -1491839314, 1531696881, -342938062, -1424382300, 1464944787, 1070383118, -31360542, -1321050994, 2079608377, 749678946, 1604962919, 1896884928, 122487966, -398403177, -1684736436, 1601678511, -1940543603, 1706874972, 1207743005, 923955440, 609907640, -736942096, 1383418256, 221524690, 1035215964, 370690032, 1129565372, -358357677, -727599792, -1340036142, -380408671, 501844507, 846071191, 1393256178, 178936656, 1538675785, 224599708};
            boolean existsInVisible = false;
            for (int i : visible) {
                if (val == i) existsInVisible = true;
            }
            if (!existsInVisible) okay = false;

            if (okay) newSpheres.add(sphere);
        }
        spheres = newSpheres;

        //ColorRGB[][] tennis = TexturedSphere.calculateTextureMap("textures/balls/tennis.png");
        //ColorRGB[][] basketball = TexturedSphere.calculateTextureMap("textures/balls/basketball.png");
        ColorRGB[][] basketball2 = TexturedSphere.calculateTextureMap("textures/balls/basketball2.png");
        ColorRGB[][] football = TexturedSphere.calculateTextureMap("textures/balls/football.png");
        //ColorRGB[][] football2 = TexturedSphere.calculateTextureMap("textures/balls/football2.png");
        //ColorRGB[][] golf = TexturedSphere.calculateTextureMap("textures/balls/golf.png");
        //ColorRGB[][] golf2 = TexturedSphere.calculateTextureMap("textures/balls/golf2.png");
        //ColorRGB[][] volleyball = TexturedSphere.calculateTextureMap("textures/balls/volleyball.png");

        ColorRGB[][] earth = TexturedSphere.calculateTextureMap("textures/planets/2k_earth_daymap.png");
        ColorRGB[][] moon = TexturedSphere.calculateTextureMap("textures/planets/2k_moon.png");
        ColorRGB[][] venus = TexturedSphere.calculateTextureMap("textures/planets/2k_venus_surface.png");
        ColorRGB[][] jupiter1 = TexturedSphere.calculateTextureMap("textures/planets/1k_jupiter.png");
        ColorRGB[][] jupiter = TexturedSphere.calculateTextureMap("textures/planets/2k_jupiter.png");
        ColorRGB[][] mars = TexturedSphere.calculateTextureMap("textures/planets/2k_mars.png");
        ColorRGB[][] neptune = TexturedSphere.calculateTextureMap("textures/planets/2k_neptune.png");
        for (Sphere sphere : spheres) {
            TexturedSphere temp = null;
            switch (sphere.label) {
                case -100:
                    System.err.println("not labeled sphere");
                    break;
                case -1:
                    sphere.colour = gray;
                    //scene.addObject(sphere);
                    break;
                case 0:
                    temp = new TexturedSphere(sphere.position, sphere.radius, sphere.colour, earth, "2k_earth_daymap.png");
                    break;
                case 1:
                    temp = new TexturedSphere(sphere.position, sphere.radius, sphere.colour, jupiter1, "1k_jupiter.png");
                    break;
                case 2:
                    temp = new TexturedSphere(sphere.position, sphere.radius, sphere.colour, football, "football.png");
                    break;
                case 3:
                    temp = new TexturedSphere(sphere.position, sphere.radius, sphere.colour, basketball2, "basketball2.png");
                    break;
                default:
                    System.err.println("not handled label");
            }
            if (temp != null) {
                //temp.rotateRandomly();
                scene.addObject(temp);
            }
        }

        ColorRGB[][] sun = TexturedSphere.calculateTextureMap("textures/planets/2k_sun.png");
        for (int i = 0; i < sun.length; i++) {
            for (int j = 0; j < sun[i].length; j++) {
                sun[i][j] = sun[i][j];
            }
        }
        TexturedSphere bigSun = new TexturedSphere(new Vector3(0.7, 0.3, 0.75), 0.58, white, sun, "2k_sun.png");
        scene.addObject(bigSun);

        scene.addPointLight(new PointLight(new Vector3(5, 3, 5), new ColorRGB(1, 0.9, 0.3), 250));
        scene.addPointLight(new PointLight(new Vector3(0, 0, 0), new ColorRGB(1, 0.7, 0.7), 20));
        scene.addPointLight(new PointLight(new Vector3(2, 2, 5), new ColorRGB(1, 0.8, 0.5), 40));

        ColorRGB[][] stars = TexturedSphere.calculateTextureMap("textures/planets/2k_stars_milky_way.png");
        for (int i = 0; i < stars.length; i++) {
            for (int j = 0; j < stars[i].length; j++) {
                stars[i][j] = stars[i][j].scale(10);
            }
        }
        InsideOutSphere background = new InsideOutSphere(new Vector3(0, 0, 800), 1000, white, stars, "2k_stars_milky_way.png");
        scene.addObject(background);

        System.out.println("Number of objects: " + scene.objects.size());
        return scene;
    }

    public static Scene create7() {
        Scene scene = new Scene();
        scene.ambientLight = new ColorRGB(0.02);


        ArrayList<PointLight> pointLights = new ArrayList<>();
        pointLights.add(new PointLight(new Vector3(1, -1, -2), white, 90));
        pointLights.add(new PointLight(new Vector3(1, 3, 4), white, 100));
        pointLights.add(new PointLight(new Vector3(2, 2, 5), white, 80));
        //scene.addPointLight(new PointLight(new Vector3(-2, -2, 4), white, 30));


        scene.addObject(new Sphere(new Vector3(2, 0, 1), 1, orange));
        scene.addObject(new Sphere(new Vector3(-2, 0, 1), 1, red));
        scene.addObject(new Sphere(new Vector3(0, 2, 1), 1, green));
        scene.addObject(new Sphere(new Vector3(0, -2, 1), 1, blue));
        scene.addObject(new Plane(new Vector3(0, 0, -1), new Vector3(0, 0, 1), white));
        scene.addObject(new Plane(new Vector3(0, 0, 3), new Vector3(0, 0, -1), white));
        scene.addPointLight(new PointLight(new Vector3(-1, 1, 1.5), white, 10));

        System.out.println("Number of objects: " + scene.objects.size());
        return scene;
    }

    public static Scene create6() {
        Scene scene = new Scene();
        scene.ambientLight = new ColorRGB(0.02);


        ArrayList<PointLight> pointLights = new ArrayList<>();
        pointLights.add(new PointLight(new Vector3(1, -1, -2), white, 90));
        pointLights.add(new PointLight(new Vector3(1, 3, 4), white, 100));
        pointLights.add(new PointLight(new Vector3(2, 2, 5), white, 80));
        //scene.addPointLight(new PointLight(new Vector3(-2, -2, 4), white, 30));


        double r1 = Math.sqrt(0.5);
        double r2 = Math.sqrt(0.5) + 1;
        double r3 = Math.sqrt(2) + 1;

        ArrayList<Sphere> spheres = new ArrayList<>();
        spheres.add(new Sphere(new Vector3(0, 0, 0), r2, green));
        spheres.add(new Sphere(new Vector3(1, 0, 0), r1, green));
        spheres.add(new Sphere(new Vector3(-1, 0, 0), r1, green));
        spheres.add(new Sphere(new Vector3(0, 1, 0), r1, green));
        spheres.add(new Sphere(new Vector3(0, -1, 0), r1, green));
        spheres.add(new Sphere(new Vector3(0, 0, 1), r1, green));
        spheres.add(new Sphere(new Vector3(0, 0, -1), r1, green));

        ArrayList<Sphere> bases = new ArrayList<>();
        bases.add(new Sphere(new Vector3(r2, r2, r2), r3, white));
        bases.add(new Sphere(new Vector3(r2, r2, -r2), r3, white));
        bases.add(new Sphere(new Vector3(r2, -r2, r2), r3, white));
        bases.add(new Sphere(new Vector3(r2, -r2, -r2), r3, white));
        bases.add(new Sphere(new Vector3(-r2, r2, r2), r3, white));
        bases.add(new Sphere(new Vector3(-r2, r2, -r2), r3, white));
        bases.add(new Sphere(new Vector3(-r2, -r2, r2), r3, white));
        bases.add(new Sphere(new Vector3(-r2, -r2, -r2), r3, white));

        ArrayList<Sphere> newSpheres = new ArrayList<>();

        ArrayList<Sphere> allInverted = new ArrayList<>();
        ColorRGB[] colors = new ColorRGB[]{blue, purple, orange, yellow};
        for (int iteration = 0; iteration < 2; iteration++) {
            ColorRGB colour = colors[iteration];
            newSpheres = new ArrayList<>();
            for (Sphere sphere : spheres) {
                for (Sphere base : bases) {
                    Sphere inverted = sphere.invert(base);
                    inverted.colour = colour;
                    newSpheres.add(inverted);
                }
            }
            spheres.addAll(newSpheres);

            if (iteration == 0) {
                Sphere border = new Sphere(new Vector3(0, 0, 0), 2.5, white);
                for (Sphere sphere : spheres) {
                    Sphere inverted = sphere.invert(border);
                    allInverted.add(inverted);
                }
            }
        }
        spheres.addAll(allInverted);

        newSpheres = new ArrayList<>();
        for (Sphere sphere : spheres) {
            boolean unique = true;
            for (Sphere newSphere : newSpheres) {
                if (sphere.position.subtract(newSphere.position).magnitude() < 0.001 && Math.abs(sphere.radius - newSphere.radius) < 0.001) {
                    unique = false;
                    break;
                }
            }
            if (unique) {
                newSpheres.add(sphere);
            }
        }
        spheres = newSpheres;

        Sphere remove = new Sphere(new Vector3(1, 5 + 1000, -2), 4, white);

        newSpheres = new ArrayList<>();
        for (Sphere sphere : spheres) {
            boolean okay = true;
            if (sphere.radius < 0.07) {
                okay = false;
            }
            if (sphere.radius > 0.7) {
                //okay = false;
            }
            if (sphere.radius == r2 && sphere.position.magnitude() < 0.01) {
                okay = false;
            }
            if (sphere.position.subtract(remove.position).magnitude() < sphere.radius + remove.radius) {
                okay = false;
            }
            if (sphere.position.magnitude() > 2) {
                sphere.colour = gray;
            }
            if (okay) {
                newSpheres.add(sphere);
            }
        }
        spheres = newSpheres;

        for (Sphere sphere : spheres) {
            sphere.position = Vector3.rotateVector(sphere.position, new Vector3(0, 1, 0), 0.15);
            sphere.position = Vector3.rotateVector(sphere.position, new Vector3(1, 0, 0), -0.5);
            sphere.position = sphere.position.scale(1);
            sphere.position = sphere.position.add(new Vector3(0, 0, 10));
        }
        for (PointLight pointLight : pointLights) {
            //pointLight.position = Vector3.rotateVector(pointLight.position, new Vector3(0, 1, 0), 0.1);
            //pointLight.position = Vector3.rotateVector(pointLight.position, new Vector3(1, 0, 0), -0.5);
            //pointLight.position = pointLight.position.add(new Vector3(0, 0, 10));
        }

        remove = new Sphere(new Vector3(0, 0, 4), 4, white);

        newSpheres = new ArrayList<>();
        for (Sphere sphere : spheres) {
            boolean okay = true;

            if (sphere.position.magnitude() < sphere.radius) {
                okay = false;
            }
            if (sphere.position.subtract(remove.position).magnitude() < sphere.radius + remove.radius) {
                okay = false;
            }
            if (okay) {
                newSpheres.add(sphere);
            }
        }
        spheres = newSpheres;

        for (PointLight pointLight : pointLights) {
            scene.addPointLight(pointLight);
        }
        ColorRGB[][] sun = TexturedSphere.calculateTextureMap("textures/planets/2k_sun.png");
        for (int i = 0; i < sun.length; i++) {
            for (int j = 0; j < sun[i].length; j++) {
                sun[i][j] = sun[i][j].scale(3);
            }
        }
        ColorRGB[][] earth = TexturedSphere.calculateTextureMap("textures/planets/2k_earth_daymap.png");
        for (int i = 0; i < earth.length; i++) {
            for (int j = 0; j < earth[i].length; j++) {
                earth[i][j] = earth[i][j].scale(3);
            }
        }
        for (Sphere sphere : spheres) {
            scene.addObject(sphere);
            /*if (sphere.radius > r1) {
                scene.addObject(new TexturedSphere(sphere.position, sphere.radius, sphere.colour, earth));
            } else {
                scene.addObject(new TexturedSphere(sphere.position, sphere.radius, sphere.colour, sun));
            }*/
        }
        //scene.addObject(new Plane(new Vector3(0, 0, 20), new Vector3(0, 0, -1), white));
        System.out.println("Number of objects: " + scene.objects.size());
        return scene;
    }

    public static Scene create5() {
        Scene scene = new Scene();
        scene.ambientLight = new ColorRGB(0.02);
        scene.addPointLight(new PointLight(new Vector3(1, -3, 2), white, 90));
        scene.addPointLight(new PointLight(new Vector3(1, 3, 4), white, 100));
        scene.addPointLight(new PointLight(new Vector3(0, 1, 8), white, 3));
        //scene.addPointLight(new PointLight(new Vector3(-2, -2, 4), white, 30));
        //scene.addObject(new Sphere(new Vector3(0.5, -0.5, 4), 0.5, white));
        ArrayList<Sphere> spheres = new ArrayList<>();

        for (double alpha = 0; alpha < 2 * Math.PI; alpha += 2 * Math.PI / 6) {
            for (double beta = 0; beta < Math.PI; beta += Math.PI / 32) {
                Vector3 position = new Vector3(1.2, 0, 0);
                position = Vector3.rotateVector(position, new Vector3(0, 0, 1), beta - Math.PI / 2);
                position = Vector3.rotateVector(position, new Vector3(0, 1, 0), alpha - (beta));
                Sphere sphere = new Sphere(position, 0.1, blue);
                sphere.SPHERE_REFLECTIVITY = 0.8;
                spheres.add(sphere);
            }
        }

        for (double alpha = 0; alpha < 2 * Math.PI; alpha += 2 * Math.PI / 6) {
            for (double beta = 0; beta < Math.PI; beta += Math.PI / 32) {
                Vector3 position = new Vector3(0.9, 0, 0);
                position = Vector3.rotateVector(position, new Vector3(0, 0, 1), beta - Math.PI / 2);
                position = Vector3.rotateVector(position, new Vector3(0, 1, 0), alpha - (beta) + Math.PI / 6);
                Sphere sphere = new Sphere(position, 0.1, red);
                sphere.SPHERE_REFLECTIVITY = 0.8;
                spheres.add(sphere);
            }
        }
        Sphere planet = new Sphere(new Vector3(0, 0, 0), 1, green);
        planet.SPHERE_REFLECTIVITY = 0.8;
        //spheres.add(planet);


        for (Sphere sphere : spheres) {
            //sphere.position = Vector3.rotateVector(sphere.position, new Vector3(0, 1, 0), 0.3);
            sphere.position = Vector3.rotateVector(sphere.position, new Vector3(1, 0, 0), -0.75);
            sphere.position = sphere.position.scale(1);
            sphere.position = sphere.position.add(new Vector3(0, 0, 8));
        }

        for (Sphere sphere : spheres) {
            scene.addObject(sphere);
        }
        System.out.println("Number of objects: " + scene.objects.size());
        return scene;
    }

    public static Scene create4() {
        Scene scene = new Scene();
        scene.ambientLight = new ColorRGB(0.02);
        scene.addPointLight(new PointLight(new Vector3(1, -3, 2), white, 90));
        scene.addPointLight(new PointLight(new Vector3(1, 3, 4), white, 100));
        scene.addPointLight(new PointLight(new Vector3(0, 1, 8), white, 3));
        //scene.addPointLight(new PointLight(new Vector3(-2, -2, 4), white, 30));
        //scene.addObject(new Sphere(new Vector3(0.5, -0.5, 4), 0.5, white));
        ArrayList<Sphere> spheres = new ArrayList<>();
        ArrayList<Sphere> bases = new ArrayList<>();


        double r1 = Math.sqrt(0.5);
        double r2 = Math.sqrt(0.5) + 1;
        double r3 = Math.sqrt(2) + 1;
        double r4 = r2 * Math.sqrt(3) - r3;

        spheres.add(new Sphere(new Vector3(0, 0, 0), r2, green));
        spheres.add(new Sphere(new Vector3(1, 0, 0), r1, green));
        spheres.add(new Sphere(new Vector3(-1, 0, 0), r1, green));
        spheres.add(new Sphere(new Vector3(0, 1, 0), r1, green));
        spheres.add(new Sphere(new Vector3(0, -1, 0), r1, green));
        spheres.add(new Sphere(new Vector3(0, 0, 1), r1, green));
        spheres.add(new Sphere(new Vector3(0, 0, -1), r1, green));

        if (false) {
            bases.add(new Sphere(new Vector3(0, 0, 0), r1, white));
            bases.add(new Sphere(new Vector3(0, r2, r2), r2, white));
            bases.add(new Sphere(new Vector3(0, r2, -r2), r2, white));
            bases.add(new Sphere(new Vector3(0, -r2, r2), r2, white));
            bases.add(new Sphere(new Vector3(0, -r2, -r2), r2, white));
            bases.add(new Sphere(new Vector3(r2, 0, r2), r2, white));
            bases.add(new Sphere(new Vector3(r2, 0, -r2), r2, white));
            bases.add(new Sphere(new Vector3(-r2, 0, r2), r2, white));
            bases.add(new Sphere(new Vector3(-r2, 0, -r2), r2, white));
            bases.add(new Sphere(new Vector3(r2, r2, 0), r2, white));
            bases.add(new Sphere(new Vector3(r2, -r2, 0), r2, white));
            bases.add(new Sphere(new Vector3(-r2, r2, 0), r2, white));
            bases.add(new Sphere(new Vector3(-r2, -r2, 0), r2, white));
        } else {
            bases.add(new Sphere(new Vector3(0, 0, 0), r4, white));
            bases.add(new Sphere(new Vector3(r2, r2, r2), r3, white));
            bases.add(new Sphere(new Vector3(r2, r2, -r2), r3, white));
            bases.add(new Sphere(new Vector3(r2, -r2, r2), r3, white));
            bases.add(new Sphere(new Vector3(r2, -r2, -r2), r3, white));
            bases.add(new Sphere(new Vector3(-r2, r2, r2), r3, white));
            bases.add(new Sphere(new Vector3(-r2, r2, -r2), r3, white));
            bases.add(new Sphere(new Vector3(-r2, -r2, r2), r3, white));
            bases.add(new Sphere(new Vector3(-r2, -r2, -r2), r3, white));
        }


        ColorRGB[] colors = new ColorRGB[]{blue, purple, orange, yellow};
        for (int iteration = 0; iteration < 2; iteration++) {
            ColorRGB colour = colors[iteration];
            ArrayList<Sphere> newSpheres = new ArrayList<>();
            for (Sphere sphere : spheres) {
                for (Sphere base : bases) {
                    Sphere inverted = sphere.invert(base);
                    /*if (inverted.position.x == 0 && inverted.position.z == 0 && inverted.position.y > 0) {
                        System.out.format("Pos: %f, %f, %f\n", inverted.position.x, inverted.position.y, inverted.position.z);
                        System.out.format("sphere, base: %d, %d\n", i, j);
                    }*/
                    inverted.colour = colour;
                    newSpheres.add(inverted);
                }
            }
            spheres.addAll(newSpheres);
        }

        ArrayList<Sphere> newSpheres = new ArrayList<>();
        for (Sphere sphere : spheres) {
            boolean unique = true;
            for (Sphere newSphere : newSpheres) {
                if (sphere.position.subtract(newSphere.position).magnitude() < 0.001 && Math.abs(sphere.radius - newSphere.radius) < 0.001) {
                    unique = false;
                    break;
                }
            }
            if (unique) {
                newSpheres.add(sphere);
            }
        }
        spheres = newSpheres;

        Sphere remove = new Sphere(new Vector3(0, 3.5, 0), 2, white);
        //spheres.add(remove);

        newSpheres = new ArrayList<>();
        for (Sphere sphere : spheres) {
            boolean okay = true;
            if (sphere.radius < 0.07) {
                okay = false;
            }
            if (sphere.radius > 0.7) {
                //okay = false;
            }
            if (sphere.radius > 1) {
                okay = false;
            }
            if (sphere.position.subtract(remove.position).magnitude() < sphere.radius + remove.radius) {
                okay = false;
            }
            if (okay) {
                newSpheres.add(sphere);
            }
        }
        spheres = newSpheres;


        //spheres.addAll(bases);
        for (Sphere sphere : spheres) {
            sphere.position = Vector3.rotateVector(sphere.position, new Vector3(0, 1, 0), 0.3);
            sphere.position = Vector3.rotateVector(sphere.position, new Vector3(1, 0, 0), -0.6);
            sphere.position = sphere.position.scale(1);
            sphere.position = sphere.position.add(new Vector3(0, 0, 8));
        }


        newSpheres = new ArrayList<>();
        for (Sphere sphere : spheres) {
            boolean okay = true;

            if (sphere.position.magnitude() < sphere.radius) {
                okay = false;
            }
            if (okay) {
                newSpheres.add(sphere);
            }
        }
        spheres = newSpheres;

        for (Sphere sphere : spheres) {
            scene.addObject(sphere);
        }
        System.out.println("Number of objects: " + scene.objects.size());
        return scene;
    }

    public static Scene create3() {
        Scene scene = new Scene();
        scene.ambientLight = new ColorRGB(0.02);
        scene.addPointLight(new PointLight(new Vector3(1, -3, 2), white, 90));
        scene.addPointLight(new PointLight(new Vector3(1, 3, 4), white, 200));
        //scene.addPointLight(new PointLight(new Vector3(-2, -2, 4), white, 30));
        //scene.addObject(new Sphere(new Vector3(0.5, -0.5, 4), 0.5, white));
        ArrayList<Sphere> spheres = new ArrayList<>();
        ArrayList<Sphere> spheres1 = new ArrayList<>();

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    //spheres.add(new Sphere(new Vector3(x, y, z), (x + y + z + 4) / 10.0, orange));
                }
            }
        }

        double c = 0.1;
        double r = 0.5;
        spheres1.add(new Sphere(new Vector3((1 + c), (1 + 0), (1 + 0)), r, white));
        spheres1.add(new Sphere(new Vector3((1 + 0), (1 + c), (1 + 0)), r, red));
        spheres1.add(new Sphere(new Vector3((1 + 0), (1 + 0), (1 + c)), r, blue));

        spheres1.add(new Sphere(new Vector3((1 + c), (1 + 0), -(1 + 0)), r, white));
        spheres1.add(new Sphere(new Vector3((1 + 0), (1 + c), -(1 + 0)), r, red));
        spheres1.add(new Sphere(new Vector3((1 + 0), (1 + 0), -(1 + c)), r, blue));

        spheres1.add(new Sphere(new Vector3((1 + c), -(1 + 0), (1 + 0)), r, white));
        spheres1.add(new Sphere(new Vector3((1 + 0), -(1 + c), (1 + 0)), r, red));
        spheres1.add(new Sphere(new Vector3((1 + 0), -(1 + 0), (1 + c)), r, blue));

        spheres1.add(new Sphere(new Vector3((1 + c), -(1 + 0), -(1 + 0)), r, white));
        spheres1.add(new Sphere(new Vector3((1 + 0), -(1 + c), -(1 + 0)), r, red));
        spheres1.add(new Sphere(new Vector3((1 + 0), -(1 + 0), -(1 + c)), r, blue));

        spheres.add(new Sphere(new Vector3(-(1 + c), (1 + 0), (1 + 0)), r, white));
        spheres.add(new Sphere(new Vector3(-(1 + 0), (1 + c), (1 + 0)), r, red));
        spheres.add(new Sphere(new Vector3(-(1 + 0), (1 + 0), (1 + c)), r, blue));

        spheres.add(new Sphere(new Vector3(-(1 + c), (1 + 0), -(1 + 0)), r, white));
        spheres.add(new Sphere(new Vector3(-(1 + 0), (1 + c), -(1 + 0)), r, red));
        spheres.add(new Sphere(new Vector3(-(1 + 0), (1 + 0), -(1 + c)), r, blue));

        spheres.add(new Sphere(new Vector3(-(1 + c), -(1 + 0), (1 + 0)), r, white));
        spheres.add(new Sphere(new Vector3(-(1 + 0), -(1 + c), (1 + 0)), r, red));
        spheres.add(new Sphere(new Vector3(-(1 + 0), -(1 + 0), (1 + c)), r, blue));

        spheres.add(new Sphere(new Vector3(-(1 + c), -(1 + 0), -(1 + 0)), r, white));
        spheres.add(new Sphere(new Vector3(-(1 + 0), -(1 + c), -(1 + 0)), r, red));
        spheres.add(new Sphere(new Vector3(-(1 + 0), -(1 + 0), -(1 + c)), r, blue));


        spheres1.add(new Sphere(new Vector3((1 + c), (1 + 0), 0 * (1 + 0)), r, green));
        spheres1.add(new Sphere(new Vector3((1 + 0), (1 + c), 0 * (1 + 0)), r, yellow));

        spheres1.add(new Sphere(new Vector3((1 + c), -(1 + 0), 0 * (1 + 0)), r, green));
        spheres1.add(new Sphere(new Vector3((1 + 0), -(1 + c), 0 * (1 + 0)), r, yellow));

        spheres.add(new Sphere(new Vector3(-(1 + c), (1 + 0), 0 * (1 + 0)), r, green));
        spheres.add(new Sphere(new Vector3(-(1 + 0), (1 + c), 0 * (1 + 0)), r, yellow));

        spheres.add(new Sphere(new Vector3(-(1 + c), -(1 + 0), 0 * (1 + 0)), r, green));
        spheres.add(new Sphere(new Vector3(-(1 + 0), -(1 + c), 0 * (1 + 0)), r, yellow));


        spheres1.add(new Sphere(new Vector3((1 + c), 0 * (1 + 0), (1 + 0)), r, green));
        spheres1.add(new Sphere(new Vector3((1 + 0), 0 * (1 + 0), (1 + c)), r, yellow));

        spheres1.add(new Sphere(new Vector3((1 + c), 0 * (1 + 0), -(1 + 0)), r, green));
        spheres1.add(new Sphere(new Vector3((1 + 0), 0 * (1 + 0), -(1 + c)), r, yellow));

        spheres.add(new Sphere(new Vector3(-(1 + c), 0 * (1 + 0), (1 + 0)), r, green));
        spheres.add(new Sphere(new Vector3(-(1 + 0), 0 * (1 + 0), (1 + c)), r, yellow));

        spheres.add(new Sphere(new Vector3(-(1 + c), 0 * (1 + 0), -(1 + 0)), r, green));
        spheres.add(new Sphere(new Vector3(-(1 + 0), 0 * (1 + 0), -(1 + c)), r, yellow));


        spheres.add(new Sphere(new Vector3(0 * (1 + 0), (1 + c), (1 + 0)), r, green));
        spheres.add(new Sphere(new Vector3(0 * (1 + 0), (1 + 0), (1 + c)), r, yellow));

        spheres.add(new Sphere(new Vector3(0 * (1 + 0), (1 + c), -(1 + 0)), r, green));
        spheres.add(new Sphere(new Vector3(0 * (1 + 0), (1 + 0), -(1 + c)), r, yellow));

        spheres.add(new Sphere(new Vector3(0 * (1 + 0), -(1 + c), (1 + 0)), r, green));
        spheres.add(new Sphere(new Vector3(0 * (1 + 0), -(1 + 0), (1 + c)), r, yellow));

        spheres.add(new Sphere(new Vector3(0 * (1 + 0), -(1 + c), -(1 + 0)), r, green));
        spheres.add(new Sphere(new Vector3(0 * (1 + 0), -(1 + 0), -(1 + c)), r, yellow));


        spheres1.add(new Sphere(new Vector3((1 + c), 0, 0), r, red));
        spheres.add(new Sphere(new Vector3(-(1 + c), 0, 0), r, red));
        spheres.add(new Sphere(new Vector3(0, (1 + c), 0), r, red));
        spheres.add(new Sphere(new Vector3(0, -(1 + c), 0), r, red));
        spheres.add(new Sphere(new Vector3(0, 0, (1 + c)), r, red));
        spheres.add(new Sphere(new Vector3(0, 0, -(1 + c)), r, red));


        ArrayList<Sphere> spheres2 = new ArrayList<>();
        double c_ring = 4;
        double r_ring = 0.21;
        for (double alpha = 0; alpha < 2 * Math.PI; alpha += 2 * Math.PI / 60) {
            spheres2.add(new Sphere(
                    new Vector3(c_ring * Math.cos(alpha), 0, c_ring * Math.sin(alpha)
                    ), r_ring, green));
        }
        c_ring = 4.38;
        r_ring = 0.17;
        for (double alpha = 0; alpha < 2 * Math.PI; alpha += 2 * Math.PI / 80) {
            spheres2.add(new Sphere(
                    new Vector3(c_ring * Math.cos(alpha), 0, c_ring * Math.sin(alpha)
                    ), r_ring, orange));
        }


        for (Sphere sphere1 : spheres1) {
            sphere1.position = Vector3.rotateVector(sphere1.position, new Vector3(1, 0, 0), 0.7);
        }
        spheres.addAll(spheres1);

        for (Sphere sphere2 : spheres2) {
            sphere2.position = Vector3.rotateVector(sphere2.position, new Vector3(0, 0, 1), -0.3);
            sphere2.position = Vector3.rotateVector(sphere2.position, new Vector3(0, 1, 0), -0.1);
        }
        spheres.addAll(spheres2);


        Sphere base = new Sphere(new Vector3(3.2, 0, 0), 2, white);
        spheres.add(base);

        for (Sphere sphere : spheres) {
            sphere.position = Vector3.rotateVector(sphere.position, new Vector3(0, 1, 0), 0.35);
            sphere.position = Vector3.rotateVector(sphere.position, new Vector3(1, 0, 0), -0.4);
            sphere.position = sphere.position.scale(1);
            sphere.position = sphere.position.add(new Vector3(0, 0, 14));
        }
        spheres.remove(base);
        for (Sphere sphere : spheres) {
            System.out.println(sphere.invert(base).position.x);
            System.out.println(sphere.invert(base).position.y);
            System.out.println(sphere.invert(base).position.z);
            System.out.println("");
            scene.addObject(sphere.invert(base));
        }


        for (Sphere sphere : spheres) {
            scene.addObject(sphere);
        }
        System.out.println("Number of objects: " + scene.objects.size());
        return scene;
    }

    public static Scene create2() {
        Scene scene = new Scene();
        scene.ambientLight = new ColorRGB(0.02);
        scene.addPointLight(new PointLight(new Vector3(1, -3, 2), white, 90));
        scene.addPointLight(new PointLight(new Vector3(1, 3, 4), white, 200));
        //scene.addPointLight(new PointLight(new Vector3(-2, -2, 4), white, 30));
        //scene.addObject(new Sphere(new Vector3(0.5, -0.5, 4), 0.5, white));
        ArrayList<Sphere> spheres = new ArrayList<>();
        ArrayList<Sphere> spheres1 = new ArrayList<>();

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    //spheres.add(new Sphere(new Vector3(x, y, z), (x + y + z + 4) / 10.0, orange));
                }
            }
        }

        double c = 0.1;
        double r = 0.5;
        spheres1.add(new Sphere(new Vector3((1 + c), (1 + 0), (1 + 0)), r, white));
        spheres1.add(new Sphere(new Vector3((1 + 0), (1 + c), (1 + 0)), r, red));
        spheres1.add(new Sphere(new Vector3((1 + 0), (1 + 0), (1 + c)), r, blue));

        spheres1.add(new Sphere(new Vector3((1 + c), (1 + 0), -(1 + 0)), r, white));
        spheres1.add(new Sphere(new Vector3((1 + 0), (1 + c), -(1 + 0)), r, red));
        spheres1.add(new Sphere(new Vector3((1 + 0), (1 + 0), -(1 + c)), r, blue));

        spheres1.add(new Sphere(new Vector3((1 + c), -(1 + 0), (1 + 0)), r, white));
        spheres1.add(new Sphere(new Vector3((1 + 0), -(1 + c), (1 + 0)), r, red));
        spheres1.add(new Sphere(new Vector3((1 + 0), -(1 + 0), (1 + c)), r, blue));

        spheres1.add(new Sphere(new Vector3((1 + c), -(1 + 0), -(1 + 0)), r, white));
        spheres1.add(new Sphere(new Vector3((1 + 0), -(1 + c), -(1 + 0)), r, red));
        spheres1.add(new Sphere(new Vector3((1 + 0), -(1 + 0), -(1 + c)), r, blue));

        spheres.add(new Sphere(new Vector3(-(1 + c), (1 + 0), (1 + 0)), r, white));
        spheres.add(new Sphere(new Vector3(-(1 + 0), (1 + c), (1 + 0)), r, red));
        spheres.add(new Sphere(new Vector3(-(1 + 0), (1 + 0), (1 + c)), r, blue));

        spheres.add(new Sphere(new Vector3(-(1 + c), (1 + 0), -(1 + 0)), r, white));
        spheres.add(new Sphere(new Vector3(-(1 + 0), (1 + c), -(1 + 0)), r, red));
        spheres.add(new Sphere(new Vector3(-(1 + 0), (1 + 0), -(1 + c)), r, blue));

        spheres.add(new Sphere(new Vector3(-(1 + c), -(1 + 0), (1 + 0)), r, white));
        spheres.add(new Sphere(new Vector3(-(1 + 0), -(1 + c), (1 + 0)), r, red));
        spheres.add(new Sphere(new Vector3(-(1 + 0), -(1 + 0), (1 + c)), r, blue));

        spheres.add(new Sphere(new Vector3(-(1 + c), -(1 + 0), -(1 + 0)), r, white));
        spheres.add(new Sphere(new Vector3(-(1 + 0), -(1 + c), -(1 + 0)), r, red));
        spheres.add(new Sphere(new Vector3(-(1 + 0), -(1 + 0), -(1 + c)), r, blue));


        spheres1.add(new Sphere(new Vector3((1 + c), (1 + 0), 0 * (1 + 0)), r, green));
        spheres1.add(new Sphere(new Vector3((1 + 0), (1 + c), 0 * (1 + 0)), r, yellow));

        spheres1.add(new Sphere(new Vector3((1 + c), -(1 + 0), 0 * (1 + 0)), r, green));
        spheres1.add(new Sphere(new Vector3((1 + 0), -(1 + c), 0 * (1 + 0)), r, yellow));

        spheres.add(new Sphere(new Vector3(-(1 + c), (1 + 0), 0 * (1 + 0)), r, green));
        spheres.add(new Sphere(new Vector3(-(1 + 0), (1 + c), 0 * (1 + 0)), r, yellow));

        spheres.add(new Sphere(new Vector3(-(1 + c), -(1 + 0), 0 * (1 + 0)), r, green));
        spheres.add(new Sphere(new Vector3(-(1 + 0), -(1 + c), 0 * (1 + 0)), r, yellow));


        spheres1.add(new Sphere(new Vector3((1 + c), 0 * (1 + 0), (1 + 0)), r, green));
        spheres1.add(new Sphere(new Vector3((1 + 0), 0 * (1 + 0), (1 + c)), r, yellow));

        spheres1.add(new Sphere(new Vector3((1 + c), 0 * (1 + 0), -(1 + 0)), r, green));
        spheres1.add(new Sphere(new Vector3((1 + 0), 0 * (1 + 0), -(1 + c)), r, yellow));

        spheres.add(new Sphere(new Vector3(-(1 + c), 0 * (1 + 0), (1 + 0)), r, green));
        spheres.add(new Sphere(new Vector3(-(1 + 0), 0 * (1 + 0), (1 + c)), r, yellow));

        spheres.add(new Sphere(new Vector3(-(1 + c), 0 * (1 + 0), -(1 + 0)), r, green));
        spheres.add(new Sphere(new Vector3(-(1 + 0), 0 * (1 + 0), -(1 + c)), r, yellow));


        spheres.add(new Sphere(new Vector3(0 * (1 + 0), (1 + c), (1 + 0)), r, green));
        spheres.add(new Sphere(new Vector3(0 * (1 + 0), (1 + 0), (1 + c)), r, yellow));

        spheres.add(new Sphere(new Vector3(0 * (1 + 0), (1 + c), -(1 + 0)), r, green));
        spheres.add(new Sphere(new Vector3(0 * (1 + 0), (1 + 0), -(1 + c)), r, yellow));

        spheres.add(new Sphere(new Vector3(0 * (1 + 0), -(1 + c), (1 + 0)), r, green));
        spheres.add(new Sphere(new Vector3(0 * (1 + 0), -(1 + 0), (1 + c)), r, yellow));

        spheres.add(new Sphere(new Vector3(0 * (1 + 0), -(1 + c), -(1 + 0)), r, green));
        spheres.add(new Sphere(new Vector3(0 * (1 + 0), -(1 + 0), -(1 + c)), r, yellow));


        spheres1.add(new Sphere(new Vector3((1 + c), 0, 0), r, red));
        spheres.add(new Sphere(new Vector3(-(1 + c), 0, 0), r, red));
        spheres.add(new Sphere(new Vector3(0, (1 + c), 0), r, red));
        spheres.add(new Sphere(new Vector3(0, -(1 + c), 0), r, red));
        spheres.add(new Sphere(new Vector3(0, 0, (1 + c)), r, red));
        spheres.add(new Sphere(new Vector3(0, 0, -(1 + c)), r, red));


        ArrayList<Sphere> spheres2 = new ArrayList<>();
        double c_ring = 4;
        double r_ring = 0.21;
        for (double alpha = 0; alpha < 2 * Math.PI; alpha += 2 * Math.PI / 60) {
            spheres2.add(new Sphere(
                    new Vector3(c_ring * Math.cos(alpha), 0, c_ring * Math.sin(alpha)
                    ), r_ring, green));
        }
        c_ring = 4.38;
        r_ring = 0.17;
        for (double alpha = 0; alpha < 2 * Math.PI; alpha += 2 * Math.PI / 80) {
            spheres2.add(new Sphere(
                    new Vector3(c_ring * Math.cos(alpha), 0, c_ring * Math.sin(alpha)
                    ), r_ring, orange));
        }


        for (Sphere sphere1 : spheres1) {
            sphere1.position = Vector3.rotateVector(sphere1.position, new Vector3(1, 0, 0), 0.7);
        }
        spheres.addAll(spheres1);

        for (Sphere sphere2 : spheres2) {
            sphere2.position = Vector3.rotateVector(sphere2.position, new Vector3(0, 0, 1), -0.3);
            sphere2.position = Vector3.rotateVector(sphere2.position, new Vector3(0, 1, 0), -0.1);
        }
        spheres.addAll(spheres2);


        for (Sphere sphere : spheres) {
            sphere.position = Vector3.rotateVector(sphere.position, new Vector3(0, 1, 0), 0.35);
            sphere.position = Vector3.rotateVector(sphere.position, new Vector3(1, 0, 0), -0.4);
            sphere.position = sphere.position.scale(1);
            sphere.position = sphere.position.add(new Vector3(0, 0, 14));
        }
        for (Sphere sphere : spheres) {
            scene.addObject(sphere);
        }
        System.out.println("Number of objects: " + scene.objects.size());
        return scene;
    }

    public static Scene create() {
        Scene scene = new Scene();
        scene.ambientLight = new ColorRGB(0.02);
        scene.addPointLight(new PointLight(new Vector3(1, -3, 2), new ColorRGB(1, 0, 0), 40));
        scene.addPointLight(new PointLight(new Vector3(1, 3, 4), new ColorRGB(0, 1, 0), 80));
        scene.addPointLight(new PointLight(new Vector3(-2, -2, 4), new ColorRGB(0, 0, 1), 30));
        //scene.addObject(new Sphere(new Vector3(0.5, -0.5, 4), 0.5, white));
        ArrayList<Sphere> spheres = fractal(new Vector3(-0.5, -0.5, 6), 1, 4);
        for (Sphere sphere : spheres) {
            sphere.position =
                    Vector3.rotateVector(sphere.position, new Vector3(0, 0, 1), 0.5);
        }
        for (Sphere sphere : spheres) {
            scene.addObject(sphere);
        }
        return scene;
    }

    public static ArrayList<Sphere> fractal(Vector3 v, double r, int iterationCount) {
        ArrayList<Sphere> result = new ArrayList<>();
        if (iterationCount == 0) {
            return result;
        }
        Sphere sphere = new Sphere(v, r, white);
        result.add(sphere);

        double c1 = 1.5;
        double c2 = 0.55;
        result.addAll(fractal(v.add(new Vector3(0, 0, c1 * r)), c2 * r, iterationCount - 1));
        result.addAll(fractal(v.add(new Vector3(0, 0, -c1 * r)), c2 * r, iterationCount - 1));
        result.addAll(fractal(v.add(new Vector3(0, c1 * r, 0)), c2 * r, iterationCount - 1));
        result.addAll(fractal(v.add(new Vector3(0, -c1 * r, 0)), c2 * r, iterationCount - 1));
        result.addAll(fractal(v.add(new Vector3(c1 * r, 0, 0)), c2 * r, iterationCount - 1));
        result.addAll(fractal(v.add(new Vector3(-c1 * r, 0, 0)), c2 * r, iterationCount - 1));

        return result;
    }
}
