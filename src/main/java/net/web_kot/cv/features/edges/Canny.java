package net.web_kot.cv.features.edges;

import lombok.experimental.UtilityClass;
import net.web_kot.cv.mat.EdgeWrapMode;
import net.web_kot.cv.mat.Mat;
import net.web_kot.cv.processors.common.Normalization;
import net.web_kot.cv.processors.convolution.Convolution;
import net.web_kot.cv.processors.convolution.impl.Gauss;
import net.web_kot.cv.processors.convolution.impl.gradient.Gradient;
import net.web_kot.cv.processors.convolution.impl.gradient.GradientMatrices;

import java.util.function.Function;

@UtilityClass
public class Canny {

    public static final double DEFAULT_MIN = 0.05;
    public static final double DEFAULT_MAX = 0.25;
    public static final int DEFAULT_TRACKING_RADIUS = 2;

    private enum Type { SUPPRESSED, WEAK, STRONG }

    private final double PI_DIVIDE_4 = Math.PI / 4;

    private final int[] DX = {  1,  1,  0, -1, -1, -1,  0,  1 }; /* 3 2 1 / 4 X 0 / 5 6 7 */
    private final int[] DY = {  0, -1, -1, -1,  0,  1,  1,  1 };

    public Mat apply(Mat image) {
        return apply(image, DEFAULT_MIN, DEFAULT_MAX, DEFAULT_TRACKING_RADIUS);
    }

    public Mat apply(Mat image, double minThreshold, double maxThreshold, int trackingRadius) {
        return apply(image, image.withSameSize(), image.withSameSize(), image.withSameSize(),
                     minThreshold, maxThreshold, trackingRadius);
    }

    public Mat apply(Mat image, Mat gradient, Mat suppressed, Mat levels, double minT, double maxT, int radius) {
        // Step 1: gaussian filter
        image = Convolution.apply(image, Gauss.getKernel(1.4));

        // Step 2: gradient
        Mat dx = image.withSameSize(), dy = image.withSameSize();
        image = Gradient.apply(image, dx, dy, GradientMatrices.SOBEL);
        Normalization.apply(image, gradient);

        // Step 3: non-maximum suppression
        for(int x = 0; x < image.getWidth(); x++)
            for(int y = 0; y < image.getHeight(); y++) {
                double theta = Math.abs(Math.atan2(dy.get(x, y), dx.get(x, y)));
                suppressed.set(x, y, checkPixel(gradient, theta, x, y) ? gradient.get(x, y) : 0);
            }

        // Step 4: double threshold
        Type[][] matrix = new Type[image.getWidth()][image.getHeight()];
        for(int x = 0; x < image.getWidth(); x++)
            for(int y = 0; y < image.getHeight(); y++) {
                double value = suppressed.get(x, y);
                matrix[x][y] = value < minT ? Type.SUPPRESSED : (value < maxT ? Type.WEAK : Type.STRONG);
            }
        writeMatrixTo(matrix, levels, t -> t == Type.STRONG ? 1 : (t == Type.WEAK ? 0.3 : 0));

        // Step 5: edge tracking
        boolean[][] visited = new boolean[image.getWidth()][image.getHeight()];
        for(int x = 0; x < image.getWidth(); x++)
            for(int y = 0; y < image.getHeight(); y++)
                if(matrix[x][y] == Type.STRONG) dfs(matrix, visited, x, y, radius);

        return writeMatrix(matrix, t -> t == Type.STRONG ? 1D : 0);
    }

    private boolean checkPixel(Mat image, double theta, int x, int y) {
        if(theta >= Math.PI) theta = Math.PI - 1e-6;
        return checkPixelWithOffset(image, x, y, theta % PI_DIVIDE_4, (int)(theta / PI_DIVIDE_4));
    }

    private boolean checkPixelWithOffset(Mat image, int x, int y, double angle, int offset) {
        double a = interpolate(image, x, y, angle, offset, offset + 1);
        double b = interpolate(image, x, y, angle, offset + 4, (offset + 4 + 1) % 8);

        return image.get(x, y) > a && image.get(x, y) > b;
    }

    private double interpolate(Mat image, int x, int y, double angle, int u, int v) {
        double a = image.get(x + DX[u], y + DY[u], EdgeWrapMode.DEFAULT);
        double b = image.get(x + DX[v], y + DY[v], EdgeWrapMode.DEFAULT);

        return a * (angle / PI_DIVIDE_4) + b * (1 - angle / PI_DIVIDE_4);
    }

    private void dfs(Type[][] matrix, boolean[][] used, int x, int y, int radius) {
        if(used[x][y]) return;
        used[x][y] = true;

        matrix[x][y] = Type.STRONG;

        for(int dx = -radius; dx <= radius; dx++)
            for(int dy = -radius; dy <= radius; dy++) {
                int newX = x + dx, newY = y + dy;
                if(newX < 0 || newX >= matrix.length || newY < 0 || newY >= matrix[0].length) continue;

                if(matrix[newX][newY] != Type.SUPPRESSED) dfs(matrix, used, newX, newY, radius);
            }
    }

    private Mat writeMatrix(Type[][] matrix, Function<Type, Double> mapper) {
        Mat mat = new Mat(matrix.length, matrix[0].length);
        writeMatrixTo(matrix, mat, mapper);
        return mat;
    }

    private void writeMatrixTo(Type[][] matrix, Mat target, Function<Type, Double> mapper) {
        for(int x = 0; x < matrix.length; x++)
            for(int y = 0; y < matrix[0].length; y++)
                target.set(x, y, mapper.apply(matrix[x][y]));
    }

}
