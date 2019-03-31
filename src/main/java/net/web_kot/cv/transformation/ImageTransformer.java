package net.web_kot.cv.transformation;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.image.BufferedImage;
import java.util.List;

public class ImageTransformer {

    public static BufferedImage apply(BufferedImage first, BufferedImage second, Transformation transformation) {
        int minX = 0, maxX = first.getWidth();
        int minY = 0, maxY = first.getHeight();

        List<Pair<Integer, Integer>> corners = ImmutableList.of(
                Pair.of(0, 0), Pair.of(second.getWidth(), 0), Pair.of(0, second.getHeight()),
                Pair.of(second.getWidth(), second.getHeight())
        );

        for(Pair<Integer, Integer> corner : corners) {
            Pair<Double, Double> transformed = apply(transformation.getReverseMatrix(),
                                                     corner.getLeft(), corner.getRight());

            minX = Math.min(minX, (int)Math.floor(transformed.getLeft()));
            maxX = Math.max(maxX, (int)Math.ceil(transformed.getLeft()));

            minY = Math.min(minY, (int)Math.floor(transformed.getRight()));
            maxY = Math.max(maxY, (int)Math.ceil(transformed.getRight()));
        }

        int width = maxX - minX, height = maxY - minY;
        int dx = -minX, dy = -minY;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        image.getGraphics().drawImage(first, dx, dy, null);

        for(int y = 0; y < image.getHeight(); y++) {
            for(int x = 0; x < image.getWidth(); x++) {
                Pair<Double, Double> to = apply(transformation.getMatrix(), x - dx, y - dy);
                int nX = (int)Math.round(to.getLeft());
                int nY = (int)Math.round(to.getRight());

                if(nX < 0 || nY < 0 || nX >= second.getWidth() || nY >= second.getHeight()) continue;
                image.setRGB(x, y, getRGB(second, nX, nY));
            }
        }

        return image;
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
        if(x < 0 || y < 0 || x >= image.getWidth() || y >= image.getHeight()) return 0;
        return image.getRGB(x, y);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public static Pair<Double, Double> apply(double[][] h, double x, double y) {
        double newX = (h[0][0] * x + h[0][1] * y + h[0][2]) / (h[2][0] * x + h[2][1] * y + h[2][2]);
        double newY = (h[1][0] * x + h[1][1] * y + h[1][2]) / (h[2][0] * x + h[2][1] * y + h[2][2]);
        return Pair.of(newX, newY);
    }

}
