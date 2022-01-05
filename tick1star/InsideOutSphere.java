package uk.ac.cam.cl.gfxintro.bh525.tick1star;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class InsideOutSphere extends Sphere {

    private ColorRGB[][] textureMap;
    private int bumpMapHeight;
    private int bumpMapWidth;
    public double randomAngle = 1.3;
    public Vector3 randomAxis = new Vector3(0, 1, 0);
    String texture;

    public InsideOutSphere(Vector3 position, double radius, ColorRGB colour, String bumpMapImg) {
        super(position, radius, colour);
        this.texture = bumpMapImg;
        try {
            BufferedImage inputImg = ImageIO.read(new File(bumpMapImg));
            bumpMapHeight = inputImg.getHeight();
            bumpMapWidth = inputImg.getWidth();
            textureMap = new ColorRGB[bumpMapHeight][bumpMapWidth];
            for (int row = 0; row < bumpMapHeight; row++) {
                for (int col = 0; col < bumpMapWidth; col++) {
                    int color = inputImg.getRGB(col, row);
                    int blue = color & 0xff;
                    int green = (color & 0xff00) >> 8;
                    int red = (color & 0xff0000) >> 16;
                    double scale = 256;
                    textureMap[row][col] = new ColorRGB((double) red / scale, (double) green / scale, (double) blue / scale);
                }
            }
        } catch (IOException e) {
            System.err.println("Error creating bump map");
            e.printStackTrace();
        }
    }

    public static ColorRGB[][] calculateTextureMap(String bumpMapImg) {
        try {
            BufferedImage inputImg = ImageIO.read(new File(bumpMapImg));
            int height = inputImg.getHeight();
            int width = inputImg.getWidth();
            ColorRGB[][] map = new ColorRGB[height][width];
            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    int color = inputImg.getRGB(col, row);
                    int blue = color & 0xff;
                    int green = (color & 0xff00) >> 8;
                    int red = (color & 0xff0000) >> 16;
                    double scale = 256;
                    map[row][col] = new ColorRGB((double) red / scale, (double) green / scale, (double) blue / scale);
                }
            }
            return map;
        } catch (IOException e) {
            System.err.println("Error creating bump map");
            e.printStackTrace();
        }
        return null;
    }

    public InsideOutSphere(Vector3 position, double radius, ColorRGB colour, ColorRGB[][] map, String texture) {
        super(position, radius, colour);
        this.texture = texture;
        //BufferedImage inputImg = ImageIO.read(new File(bumpMapImg));
        bumpMapHeight = map.length;
        bumpMapWidth = map[0].length;
        textureMap = map;
    }

    public InsideOutSphere(Sphere sphere) {
        super(sphere.position, sphere.radius, sphere.colour);
        //BufferedImage inputImg = ImageIO.read(new File(bumpMapImg));
        bumpMapHeight = 1;
        bumpMapWidth = 1;
        textureMap = new ColorRGB[][]{{sphere.colour}};
    }

    public void rotateRandomly() {
        randomAngle = 2 * Math.PI * Math.random();
        randomAxis = Vector3.randomInsideUnitSphere().normalised();
    }

    // Get normal to surface at position
    public ColorRGB getColour(Vector3 position) {
        Vector3 normal = Vector3.rotateVector(super.getNormalAt(position), randomAxis, randomAngle).normalised();
        //normal = super.getNormalAt(position);
        double alpha = (Math.atan2(normal.z, -normal.x) + 2 * Math.PI) % (2 * Math.PI);
        double beta = Math.asin(normal.y) + Math.PI / 2;
        double alphaScaled = 1 - (alpha) / (2 * Math.PI);
        double betaScaled = 1 - (beta) / (Math.PI);
        int u = (int) (bumpMapWidth * alphaScaled);
        int v = (int) (bumpMapHeight * betaScaled);
        return textureMap[v][u];
    }

    @Override
    public Vector3 getNormalAt(Vector3 position) {
        return position.subtract(this.position).normalised().scale(-1);
    }

    @Override
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
            double s = (-b + d) / (2 * a);
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

    public String toXML() {
        return String.format("<textUred-sphere x=\"%f\" y=\"%f\" z=\"%f\" radius=\"%f\" colour=\"#FFFFFF\" kd=\"0.8\" kS=\"1.2\" alphaS=\"50\" texture-map=\"%s\"/>\n", position.x, position.y, position.z, radius, texture);
    }
}
