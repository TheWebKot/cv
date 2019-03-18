package net.web_kot.cv.features.descriptors.impl;

import net.web_kot.cv.features.corners.PointOfInterest;
import net.web_kot.cv.features.descriptors.Descriptor;
import net.web_kot.cv.mat.EdgeWrapMode;
import net.web_kot.cv.mat.Mat;
import net.web_kot.cv.mat.Vector;
import net.web_kot.cv.processors.convolution.impl.Gauss;
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
        return calculate(image, points, DEFAULT_GRID_SIZE, DEFAULT_BLOCK_SIZE, DEFAULT_BINS_COUNT);
    }

    public static List<Descriptor> calculate(Mat image, Iterable<PointOfInterest> points,
                                             int gridSize, int blockSize, int binsCount) {
        Mat gauss = Gauss.getFullKernel(DEFAULT_GRID_SIZE * DEFAULT_BLOCK_SIZE / 3D);

        Mat dx = image.withSameSize(), dy = image.withSameSize(), gradient = image.withSameSize();
        Gradient.apply(image, gradient, dx, dy, GradientMatrices.SOBEL);

        return map(points, point ->
                toVector(
                        calculateHistograms(gradient, dx, dy, gauss, gridSize, blockSize, binsCount, point, 0),
                        gridSize, binsCount
                )
        );
    }

    protected static double[][][] calculateHistograms(Mat gradient, Mat dx, Mat dy, Mat gauss,
                                                      int gridSize, int blockSize, int binsCount,
                                                      PointOfInterest center, double angle) {
        EdgeWrapMode mode = EdgeWrapMode.DEFAULT;
        int gaussK = gauss.getWidth() / 2;

        int centerX = ScaledMat.modify(center.getX(), -center.getOctave());
        int centerY = ScaledMat.modify(center.getY(), -center.getOctave());

        double[][][] bins = new double[gridSize][gridSize][binsCount];
        double step = 2 * Math.PI / binsCount;

        int from = gridSize * blockSize / 2, to = gridSize * blockSize - from;
        for(int u = -from; u < to; u++)
            for(int v = -from; v < to; v++) {
                int x = centerX + u, y = centerY + v;

                double theta = Math.atan2(dy.get(x, y, mode), dx.get(x, y, mode));
                if(theta < 0) theta += Math.PI * 2;

                double value = gradient.get(x, y, mode);

                // Rotation
                int rotatedU = (int)Math.round(u * Math.cos(-angle) + v * Math.sin(-angle));
                int rotatedV = (int)Math.round(v * Math.cos(-angle) - u * Math.sin(-angle));

                double rotatedTheta = theta + angle;
                if(rotatedTheta >= Math.PI * 2) rotatedTheta -= Math.PI * 2;

                // Grid cell location
                int column = (rotatedU + from) / blockSize;
                int row = (rotatedV + from) / blockSize;

                if(column < 0 || column >= gridSize || row < 0 || row >= gridSize) continue;

                // Distance-based multiplier
                value = value * gauss.get(rotatedU + gaussK, rotatedV + gaussK, EdgeWrapMode.BLACK);

                // Bins distribution
                int leftBin = Math.min((int)Math.floor(rotatedTheta / step), binsCount - 1);
                int rightBin = (leftBin + 1) % binsCount;

                double ratio = (rotatedTheta % step) / step;
                bins[row][column][leftBin] += value * (1 - ratio);
                bins[row][column][rightBin] += value * ratio;
            }

        return bins;
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
