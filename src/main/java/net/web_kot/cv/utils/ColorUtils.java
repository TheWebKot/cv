package net.web_kot.cv.utils;

import lombok.experimental.UtilityClass;

import java.awt.*;

@UtilityClass
public class ColorUtils {

    public double rgbToGreyscale(int rgb) {
        Color color = new Color(rgb);
        return (0.229 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue()) / 255D;
    }

    public int greyscaleToRgb(double greyscale) {
        int value = (int)Math.round(greyscale * 255);
        return new Color(value, value, value).getRGB();
    }

}
