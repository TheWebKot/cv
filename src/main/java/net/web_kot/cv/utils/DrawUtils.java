package net.web_kot.cv.utils;

import net.web_kot.cv.features.corners.PointOfInterest;
import net.web_kot.cv.features.descriptors.Descriptor;
import net.web_kot.cv.mat.Mat;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class DrawUtils {

    private static final int CORNER_CIRCLE_RADIUS = 3;
    private static final int POINT_CROSS_SIZE = 3;

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
        for(Descriptor d : descriptors) {
            PointOfInterest p = d.getPoint();

            Color color = ColorUtils.getSpectrum(p.getValue());
            g.setColor(color);

            g.setStroke(new BasicStroke(1));
            g.drawLine(p.getX() - POINT_CROSS_SIZE, p.getY(), p.getX() + POINT_CROSS_SIZE, p.getY());
            g.drawLine(p.getX(), p.getY() - POINT_CROSS_SIZE, p.getX(), p.getY() + POINT_CROSS_SIZE);

            g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 128));

            int radius = (int)Math.round((p.getOctave() == -1 ? 1 : 1 << p.getOctave()) * 1.5);
            g.setStroke(new BasicStroke(1));
            g.drawOval(p.getX() - radius, p.getY() - radius, radius * 2, radius * 2);

            double angle = d.getAngle();
            int rotatedX = (int)Math.round(radius * Math.cos(angle));
            int rotatedY = (int)Math.round(-radius * Math.sin(angle));

            g.drawLine(p.getX(), p.getY(), p.getX() + rotatedX, p.getY() + rotatedY);
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
        g.setColor(new Color(1, 0, 0, 0.5f));
        g.setStroke(new BasicStroke(2));
        g.drawLine(fromX, fromY, toX, toY);
    }

    public static void enableAntiAliasing(BufferedImage image) {
        ((Graphics2D)image.getGraphics()).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                                           RenderingHints.VALUE_ANTIALIAS_ON);
    }

}
