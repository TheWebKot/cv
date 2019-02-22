package net.web_kot.cv.utils;

import lombok.experimental.UtilityClass;

import java.awt.*;

@UtilityClass
public class ColorUtils {

    public double rgbToGreyscale(int red, int green, int blue) {
        return (0.229 * red + 0.587 * green + 0.114 * blue) / 255D;
    }

    public double rgbToGreyscale(int rgb) {
        Color color = new Color(rgb);
        return rgbToGreyscale(color.getRed(), color.getGreen(), color.getBlue());
    }

    public int greyscaleToRgb(double greyscale) {
        int value = (int)Math.round(greyscale * 255);
        return value << 16 | value << 8 | value;
    }

}
