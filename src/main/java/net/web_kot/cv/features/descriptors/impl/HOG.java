package net.web_kot.cv.features.descriptors.impl;

import net.web_kot.cv.features.corners.PointOfInterest;
import net.web_kot.cv.features.descriptors.Descriptor;
import net.web_kot.cv.mat.EdgeWrapMode;
import net.web_kot.cv.mat.Mat;
import net.web_kot.cv.mat.Vector;
import net.web_kot.cv.processors.convolution.impl.gradient.Gradient;
import net.web_kot.cv.processors.convolution.impl.gradient.GradientMatrices;
import net.web_kot.cv.scale.ScaledMat;

import java.util.List;

import static net.web_kot.cv.features.descriptors.DescriptorsCommon.*;

public class HOG {

    private static final int DEFAULT_BINS_COUNT = 8;

    private static final int DEFAULT_GRID_SIZE = 4;
    private static final int DEFAULT_BLOCK_SIZE = 4;

    public static List<Descriptor> calculate(Mat image, Iterable<PointOfInterest> points) {
        return calculate(image, points, DEFAULT_GRID_SIZE, DEFAULT_BINS_COUNT);
    }

    public static List<Descriptor> calculate(Mat image, Iterable<PointOfInterest> points,
                                             int gridSize, int binsCount) {
        double sigma = DEFAULT_GRID_SIZE * DEFAULT_BLOCK_SIZE / 2D / 3D;

        Mat dx = image.withSameSize(), dy = image.withSameSize(), gradient = image.withSameSize();
        Gradient.apply(image, gradient, dx, dy, GradientMatrices.SOBEL);

        return map(points, point ->
                toVector(
                        calculateHistograms(gradient, dx, dy, sigma, gridSize, binsCount, point, 0, false),
                        gridSize, binsCount
                )
        );
    }

    protected static double[][][] calculateHistograms(Mat gradient, Mat dx, Mat dy, double sigma,
                                                      int gridSize, int binsCount,
                                                      PointOfInterest center, double angle, boolean triLinear) {
        double expScale = -1 / (2 * sigma * sigma);
        int radius = (int)Math.round(3 * sigma);

        return calculateHistograms(gradient, dx, dy, expScale, gridSize, radius, binsCount, center, angle, triLinear);
    }

    protected static double[][][] calculateHistograms(Mat gradient, Mat dx, Mat dy, double expScale,
                                                      int gridSize, int radius, int binsCount,
                                                      PointOfInterest center, double angle, boolean triLinear) {
        int centerX = ScaledMat.modify(center.getX(), -center.getOctave());
        int centerY = ScaledMat.modify(center.getY(), -center.getOctave());

        double[][][] bins = new double[gridSize][gridSize][binsCount];
        double step = 2 * Math.PI / binsCount;

        double cos = Math.cos(-angle), sin = Math.sin(-angle);
        double fullSize = radius * 2 + 1, blockSize = fullSize / gridSize;

        EdgeWrapMode mode = EdgeWrapMode.DEFAULT;
        for(int u = -radius; u <= radius; u++)
            for(int v = -radius; v <= radius; v++) {
                int x = centerX + u, y = centerY + v;

                double theta = Math.atan2(dy.get(x, y, mode), dx.get(x, y, mode));
                if(theta < 0) theta += Math.PI * 2;

                double value = gradient.get(x, y, mode);

                // Rotation
                double rotatedU = u * cos + v * sin;
                double rotatedV = v * cos - u * sin;

                double rotatedTheta = theta + angle;
                if(rotatedTheta > Math.PI * 2) rotatedTheta -= Math.PI * 2;

                double column = (rotatedU + fullSize / 2) / blockSize;
                double row = (rotatedV + fullSize / 2) / blockSize;

                if(column < 0 || column >= gridSize || row < 0 || row >= gridSize) continue;

                // Distance-based multiplier
                value *= Math.exp((rotatedU * rotatedU + rotatedV * rotatedV) * expScale);

                // Bins distribution
                int leftBin = Math.min((int)Math.floor(rotatedTheta / step), binsCount - 1);
                int rightBin = (leftBin + 1) % binsCount;

                double ratio = (rotatedTheta % step) / step;

                if(!triLinear) {
                    distribute(bins, (int)row, (int)column, 1, leftBin, rightBin, ratio, value);
                } else {
                    int lowestR = (int)row;
                    if(row - lowestR <= 0.5) lowestR--;

                    int lowestC = (int)column;
                    if(column - lowestC <= 0.5) lowestC--;

                    double rc = 1 - Math.abs(row - (lowestR + 0.5));
                    double cc = 1 - Math.abs(column - (lowestC + 0.5));

                    distribute(bins, lowestR,     lowestC,     rc * cc,             leftBin, rightBin, ratio, value);
                    distribute(bins, lowestR,     lowestC + 1, rc * (1 - cc),       leftBin, rightBin, ratio, value);
                    distribute(bins, lowestR + 1, lowestC,     (1 - rc) * cc,       leftBin, rightBin, ratio, value);
                    distribute(bins, lowestR + 1, lowestC + 1, (1 - rc) * (1 - cc), leftBin, rightBin, ratio, value);
                }
            }

        return bins;
    }

    private static void distribute(double[][][] bins, int row, int column, double coefficient,
                                  int leftBin, int rightBin, double ratio, double value) {
        if(row < 0 || row >= bins.length || column < 0 || column >= bins[row].length) return;

        bins[row][column][leftBin] += coefficient * value * (1 - ratio);
        bins[row][column][rightBin] += coefficient * value * ratio;
    }

    protected static Vector toVector(double[][][] bins, int gridSize, int binsCount) {
        Vector result = new Vector(gridSize * gridSize * binsCount);

        for(int i = 0; i < gridSize; i++)
            for(int j = 0; j < gridSize; j++)
                for(int k = 0; k < binsCount; k++)
                    result.set(i * gridSize * binsCount + j * binsCount + k, bins[i][j][k]);

        return result.normalize();
    }

}
