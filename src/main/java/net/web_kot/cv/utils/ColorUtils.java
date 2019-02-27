package net.web_kot.cv.utils;

import java.awt.*;

public class ColorUtils {

    private static final double SPECTRUM_OFFSET = 180D / 255;

    public static double rgbToGreyscale(int red, int green, int blue) {
        return (0.229 * red + 0.587 * green + 0.114 * blue) / 255D;
    }

    public static double rgbToGreyscale(int rgb) {
        Color color = new Color(rgb);
        return rgbToGreyscale(color.getRed(), color.getGreen(), color.getBlue());
    }

    public static int greyscaleToRgb(double greyscale) {
        int value = (int)Math.round(greyscale * 255);
        return value << 16 | value << 8 | value;
    }

    public static Color getSpectrum(double value) {
        value = 1 - value * SPECTRUM_OFFSET + SPECTRUM_OFFSET;
        return Color.getHSBColor((float)value, 1F, 1F);
    }

}
