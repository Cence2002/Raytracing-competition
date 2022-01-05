package uk.ac.cam.cl.gfxintro.bh525.tick1star;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PipedInputStream;

public class BumpySphere extends Sphere {

    private float BUMP_FACTOR = 2;
    private float[][] heightMap;
    private int bumpMapHeight;
    private int bumpMapWidth;

    public BumpySphere(Vector3 position, double radius, ColorRGB colour, String bumpMapImg) {
        super(position, radius, colour);
        try {
            BufferedImage inputImg = ImageIO.read(new File(bumpMapImg));
            bumpMapHeight = inputImg.getHeight();
            bumpMapWidth = inputImg.getWidth();
            heightMap = new float[bumpMapHeight][bumpMapWidth];
            for (int row = 0; row < bumpMapHeight; row++) {
                for (int col = 0; col < bumpMapWidth; col++) {
                    float height = (float) (inputImg.getRGB(col, row) & 0xFF) / 0xFF;
                    heightMap[row][col] = BUMP_FACTOR * height;
                }
            }
        } catch (IOException e) {
            System.err.println("Error creating bump map");
            e.printStackTrace();
        }
    }

    // Get normal to surface at position
    @Override
    public Vector3 getNormalAt(Vector3 position) {
        Vector3 normal = super.getNormalAt(position);
        double alpha = (Math.atan2(normal.z, -normal.x) + 2 * Math.PI) % (2 * Math.PI);
        double beta = Math.asin(normal.y) + Math.PI / 2;
        double alphaScaled = 1 - (alpha) / (2 * Math.PI);
        double betaScaled = 1 - (beta) / (Math.PI);
        int u = (int) (bumpMapWidth * alphaScaled);
        int v = (int) (bumpMapHeight * betaScaled);
        if (u >= bumpMapWidth - 1) u = bumpMapWidth - 2;
        if (v >= bumpMapHeight - 1) v = bumpMapHeight - 2;
        Vector3 P_u = new Vector3(0, 1, 0).cross(normal).normalised();
        Vector3 P_v = P_u.cross(normal).scale(-1).normalised();
        double B_u = heightMap[v + 1][u] - heightMap[v][u];
        double B_v = heightMap[v][u + 1] - heightMap[v][u];

        //TODO: return the normal modified by the bump map
        return normal.add(P_u.scale(B_u)).add(P_v.scale(B_v)).normalised();
    }

}
