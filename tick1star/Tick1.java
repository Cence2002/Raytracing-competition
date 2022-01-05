package uk.ac.cam.cl.gfxintro.bh525.tick1star;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Tick1 {
    // Default input and output files
    //public static final String DEFAULT_INPUT = "test1.xml";
    public static final String DEFAULT_INPUT = "scenes/scene6.xml";
    public static final String DEFAULT_OUTPUT = "output8.png";

    public static final int DEFAULT_BOUNCES = 5; // Default number of ray bounces

    // Height and width of the output image
    private static double resDiv = 8;
    private static final int WIDTH_PX = (int) (800 / resDiv);
    private static final int HEIGHT_PX = (int) (600 / resDiv);

    public static void usageError() { // Usage information
        System.err.println("USAGE: <tick2> [--input INPUT] [--output OUTPUT] [--bounces BOUNCES]");
        System.exit(-1);
    }

    public static void main(String[] args) throws IOException {
        // We should have an even number of arguments - each option and its value
        if (args.length % 2 != 0) {
            usageError();
        }

        // Parse the input and output filenames from the arguments
        String inputSceneFile = DEFAULT_INPUT, output = DEFAULT_OUTPUT;
        int bounces = DEFAULT_BOUNCES;
        for (int i = 0; i < args.length; i += 2) {
            switch (args[i]) {
                case "-i":
                case "--input":
                    inputSceneFile = args[i + 1];
                    break;
                case "-o":
                case "--output":
                    output = args[i + 1];
                    break;
                case "-b":
                case "--bounces":
                    bounces = Integer.parseInt(args[i + 1]);
                    break;
                default:
                    System.err.println("Unknown option: " + args[i]);
                    usageError();
            }
        }

        // Create the scene from the XML file
        System.out.printf("Loading scene '%s'\n", inputSceneFile);
        Scene scene = new SceneLoader(inputSceneFile).getScene();
        scene = Scene.create11();
        System.out.println(scene.toXML());

        // Create the image and colour the pixels
        Renderer renderer = new Renderer(WIDTH_PX, HEIGHT_PX, bounces);
        BufferedImage image = renderer.render(scene);

        System.out.format("Visible objects: %d\n", renderer.visibleObjects.size());
        //System.out.format("Visible objects: %d\n", renderer.visibleObjectHashes.size());
        /*for (Integer i : renderer.visibleObjectHashes.keySet()) {
            if (renderer.visibleObjectHashes.get(i) >= 1)
                System.out.print(i + ",");
        }*/

        // Save the image to disk
        File save = new File(output);
        ImageIO.write(image, "png", save);
    }
}
