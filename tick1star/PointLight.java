package uk.ac.cam.cl.gfxintro.bh525.tick1star;

public class PointLight {

    // Point light parameters
    public Vector3 position;
    private ColorRGB colour;
    private double intensity;

    public PointLight(Vector3 position, ColorRGB colour, double intensity) {
        this.position = position;
        this.colour = colour;
        this.intensity = intensity;
    }

    public Vector3 getPosition() {
        return position;
    }

    public ColorRGB getColour() {
        return colour;
    }

    public double getIntensity() {
        return intensity;
    }

    // Get colour of light at a certain distance away
    public ColorRGB getIlluminationAt(double distance) {
        return colour.scale(intensity / (Math.PI * 4 * Math.pow(distance, 2)));
    }

    public String toXML() {
        return String.format("<point-light x=\"%f\" y=\"%f\" z=\"%f\" colour=\"#FFFFFF\" intensity=\"80\"/>\n", position.x, position.y, position.z);
    }
}
