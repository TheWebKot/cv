package net.web_kot.cv.utils;

import net.web_kot.cv.features.corners.PointOfInterest;
import net.web_kot.cv.features.descriptors.Descriptor;
import net.web_kot.cv.mat.Mat;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.List;

public class DrawUtils {

    private static final int CORNER_CIRCLE_RADIUS = 3;

    public static void drawCorners(BufferedImage image, Mat corners) {
        for(int x = 0; x < corners.getWidth(); x++)
            for(int y = 0; y < corners.getHeight(); y++) {
                if(Math.abs(corners.get(x, y)) < 1e-6) continue;
                fillCircle(image, x, y, CORNER_CIRCLE_RADIUS, ColorUtils.getSpectrum(corners.get(x, y)));
            }
    }

    public static void drawCorners(BufferedImage image, List<PointOfInterest> corners) {
        for(PointOfInterest p : corners)
            fillCircle(image, p.getX(), p.getY(), CORNER_CIRCLE_RADIUS, ColorUtils.getSpectrum(p.getValue()));
    }

    public static void drawDescriptors(BufferedImage image, List<Descriptor> descriptors) {
        Graphics2D g = (Graphics2D)image.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for(Descriptor d : descriptors) {
            PointOfInterest p = d.getPoint();

            Color color = ColorUtils.getSpectrum(p.getValue());
            g.setColor(color);

            double radius = p.getSize() == null ? 12 : p.getSize() / 2;

            g.setStroke(new BasicStroke(1.2f));
            g.draw(new Ellipse2D.Double(p.getX() - radius, p.getY() - radius, radius * 2, radius * 2));

            double rotatedX = radius * Math.cos(d.getAngle());
            double rotatedY = -radius * Math.sin(d.getAngle());

            g.draw(new Line2D.Double(p.getX(), p.getY(), p.getX() + rotatedX, p.getY() + rotatedY));
        }
    }

    public static void fillCircle(BufferedImage image, int x, int y, int radius, Color color) {
        Graphics2D g = (Graphics2D)image.getGraphics();
        g.setColor(color);
        g.fillOval(x - radius, y - radius, radius * 2, radius * 2);
    }

    public static void drawCircle(BufferedImage image, int x, int y, int radius, Color color) {
        Graphics2D g = (Graphics2D)image.getGraphics();
        g.setColor(color);
        g.setStroke(new BasicStroke(2F));
        g.drawOval(x - radius, y - radius, radius * 2, radius * 2);
    }

    public static void drawImage(BufferedImage target, BufferedImage image, int x, int y) {
        Graphics2D g = (Graphics2D)target.getGraphics();
        g.drawImage(image, x, y, null);
    }

    public static void drawLine(BufferedImage image, int fromX, int fromY, int toX, int toY) {
        Graphics2D g = (Graphics2D)image.getGraphics();

        Color c = ColorUtils.randomColor();
        g.setColor(c);
        //g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 128));

        g.setStroke(new BasicStroke(2));
        g.drawLine(fromX, fromY, toX, toY);
    }

}
