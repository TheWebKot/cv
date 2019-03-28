package net.web_kot.cv.transformation;

import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageTransformer {

    public static final double SCALE_FACTOR = 4;
    public static final int BACKGROUND_RGB = Color.BLACK.getRGB();

    public static BufferedImage apply(BufferedImage first, BufferedImage second, double[][] matrix) {
        int width = (int)Math.round(first.getWidth() * SCALE_FACTOR);
        int height = (int)Math.round(first.getHeight() * SCALE_FACTOR);

        int dx = (width - first.getWidth()) / 2;
        int dy = (height - first.getHeight()) / 2;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        image.getGraphics().drawImage(first, dx, dy, null);

        int minX = dx, maxX = minX + first.getWidth();
        int minY = dy, maxY = minY + first.getHeight();

        for(int y = 0; y < image.getHeight(); y++) {
            for(int x = 0; x < image.getWidth(); x++) {
                Pair<Double, Double> to = apply(matrix, x - dx, y - dy);
                int nX = (int)Math.round(to.getLeft());
                int nY = (int)Math.round(to.getRight());

                if(nX < 0 || nY < 0 || nX >= second.getWidth() || nY >= second.getHeight()) continue;

                image.setRGB(x, y, getRGB(second, nX, nY));

                minX = Math.min(minX, x);
                maxX = Math.max(maxX, x);

                minY = Math.min(minY, y);
                maxY = Math.max(maxY, y);
            }
        }

        return image.getSubimage(minX, minY, maxX - minX, maxY - minY);
    }

    public static int getRGB(BufferedImage image, double x, double y) {
        int xF = (int)Math.floor(x), xC = (int)Math.ceil(x);
        int yF = (int)Math.floor(y), yC = (int)Math.ceil(y);

        double xDelta = 1 - (x - xF), yDelta = 1 - (y - yF);

        double result = getIntRGB(image, xF, yF) * xDelta * yDelta;
        result += getIntRGB(image, xC, yF) * (1 - xDelta) * yDelta;
        result += getIntRGB(image, xF, yC) * xDelta * (1 - yDelta);
        result += getIntRGB(image, xC, yC) * (1 - xDelta) * (1 - yDelta);

        return (int)Math.round(result);
    }

    public static int getIntRGB(BufferedImage image, int x, int y) {
        if(x < 0 || y < 0 || x >= image.getWidth() || y >= image.getHeight()) return BACKGROUND_RGB;
        return image.getRGB(x, y);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public static Pair<Double, Double> apply(double[][] h, double x, double y) {
        double newX = (h[0][0] * x + h[0][1] * y + h[0][2]) / (h[2][0] * x + h[2][1] * y + h[2][2]);
        double newY = (h[1][0] * x + h[1][1] * y + h[1][2]) / (h[2][0] * x + h[2][1] * y + h[2][2]);
        return Pair.of(newX, newY);
    }

}
