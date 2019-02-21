package net.web_kot.cv.mat;

import lombok.*;
import net.web_kot.cv.utils.MathUtils;

import static net.web_kot.cv.utils.ColorUtils.*;

import java.awt.image.BufferedImage;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Mat {

    @Getter
    private final int width, height;
    @Getter
    private final double[] buffer;

    public Mat(int width, int height) {
        this(width, height, new double[width * height]);
    }

    private Mat(Mat source) {
        this(source.getWidth(), source.getHeight(), source.buffer.clone());
    }

    public void set(int x, int y, double value) {
        buffer[getIndex(x, y)] = value;
    }

    public double get(int x, int y) {
        return buffer[getIndex(x, y)];
    }

    public double get(int x, int y, EdgeWrapMode mode) {
        switch(mode) {
            case BLACK:
                if(isCoordinatesOutOfBounds(x, y)) return 0;
                break;
            case COPY:
            case DEFAULT:
                x = MathUtils.normalizeNumber(0, x, width);
                y = MathUtils.normalizeNumber(0, y, height);
                break;
            case MIRROR:
                if(x < 0 || x >= width) x = x - (x % width) * 2 - 1;
                if(y < 0 || y >= height) y = y - (y % height) * 2 - 1;
                break;
            case WRAP:
                x = (x + width) % width;
                y = (y + height) % height;
                break;
            default:
                throw new IllegalArgumentException("Unknown edge wrap mode " + mode);
        }
        return get(x, y);
    }

    public static Mat fromBufferedImage(BufferedImage image) {
        Mat mat = new Mat(image.getWidth(), image.getHeight());
        for(int x = 0; x < mat.getWidth(); x++)
            for(int y = 0; y < mat.getHeight(); y++)
                mat.set(x, y, rgbToGreyscale(image.getRGB(x, y)));

        return mat;
    }

    public BufferedImage toBufferedImage() {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for(int x = 0; x < width; x++)
            for(int y = 0; y < height; y++)
                image.setRGB(x, y, greyscaleToRgb(get(x, y)));

        return image;
    }

    private int getIndex(int x, int y) {
        if(isCoordinatesOutOfBounds(x, y)) throw new IllegalArgumentException("Coordinates out of bounds");
        return y * width + x;
    }

    private boolean isCoordinatesOutOfBounds(int x, int y) {
        return x < 0 || x >= width || y < 0 || y >= height;
    }

    public Mat copy() {
        return new Mat(this);
    }

    public Mat withSameSize() {
        return new Mat(this.width, this.height);
    }

    public static Mat vector(double[] vector) {
        return new Mat(vector.length, 1, vector.clone());
    }

    public static Mat matrix(double[][] matrix) {
        Mat result = new Mat(matrix[0].length, matrix.length);
        for(int i = 0; i < matrix.length; i++)
            System.arraycopy(matrix[i], 0, result.buffer, i * result.width, result.width);
        return result;
    }

}
