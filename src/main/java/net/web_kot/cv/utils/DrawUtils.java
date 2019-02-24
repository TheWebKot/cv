package net.web_kot.cv.utils;

import lombok.experimental.UtilityClass;
import net.web_kot.cv.mat.Mat;

import java.awt.*;
import java.awt.image.BufferedImage;

@UtilityClass
public class DrawUtils {

    public void drawCorners(BufferedImage image, Mat corners) {
        for(int x = 0; x < corners.getWidth(); x++)
            for(int y = 0; y < corners.getHeight(); y++)
                if(Math.abs(corners.get(x, y)) > 1e-6) drawCircle(image, x, y, 2, Color.RED);
    }

    public void drawCircle(BufferedImage image, int x, int y, int radius, Color color) {
        Graphics2D g = (Graphics2D)image.getGraphics();
        g.setColor(color);
        g.drawOval(x - radius, y - radius, radius * 2, radius * 2);
    }

}
