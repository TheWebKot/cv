package net.web_kot.cv.utils;

import lombok.experimental.UtilityClass;
import net.web_kot.cv.features.corners.PointOfInterest;
import net.web_kot.cv.mat.Mat;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

@UtilityClass
public class DrawUtils {

    private final int CORNER_CIRCLE_RADIUS = 3;

    public void drawCorners(BufferedImage image, Mat corners) {
        for(int x = 0; x < corners.getWidth(); x++)
            for(int y = 0; y < corners.getHeight(); y++) {
                if(Math.abs(corners.get(x, y)) < 1e-6) continue;
                drawCircle(image, x, y, CORNER_CIRCLE_RADIUS, ColorUtils.getSpectrum(corners.get(x, y)));
            }
    }

    public void drawCorners(BufferedImage image, List<PointOfInterest> corners) {
        for(PointOfInterest p : corners)
            drawCircle(image, p.getX(), p.getY(), CORNER_CIRCLE_RADIUS, ColorUtils.getSpectrum(p.getValue()));
    }

    public void drawCircle(BufferedImage image, int x, int y, int radius, Color color) {
        Graphics2D g = (Graphics2D)image.getGraphics();
        g.setColor(color);
        g.fillOval(x - radius, y - radius, radius * 2, radius * 2);
    }

    public void drawImage(BufferedImage target, BufferedImage image, int x, int y) {
        Graphics2D g = (Graphics2D)target.getGraphics();
        g.drawImage(image, x, y, null);
    }

    public void drawLine(BufferedImage image, int fromX, int fromY, int toX, int toY) {
        Graphics2D g = (Graphics2D)image.getGraphics();
        g.setColor(new Color(1, 0, 0, 0.5f));
        g.setStroke(new BasicStroke(2));
        g.drawLine(fromX, fromY, toX, toY);
    }

}
