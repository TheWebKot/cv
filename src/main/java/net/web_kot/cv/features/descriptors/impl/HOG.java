package net.web_kot.cv.features.descriptors.impl;

import net.web_kot.cv.features.corners.PointOfInterest;
import net.web_kot.cv.features.descriptors.Descriptor;
import net.web_kot.cv.mat.EdgeWrapMode;
import net.web_kot.cv.mat.Mat;
import net.web_kot.cv.mat.Vector;
import net.web_kot.cv.processors.convolution.impl.Gauss;
import net.web_kot.cv.processors.convolution.impl.gradient.Gradient;
import net.web_kot.cv.processors.convolution.impl.gradient.GradientMatrices;

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
        int gaussK = gauss.getWidth() / 2;

        Mat dx = image.withSameSize(), dy = image.withSameSize(), gradient = image.withSameSize();
        Gradient.apply(image, gradient, dx, dy, GradientMatrices.SOBEL);

        EdgeWrapMode mode = EdgeWrapMode.DEFAULT;
        return map(points, center -> gridMapper(gridSize, blockSize, (xRange, yRange) -> {
            double[] bins = new double[binsCount];
            double step = 2 * Math.PI / binsCount;

            for(int x = xRange.fromInclusive(); x < xRange.toExclusive(); x++)
                for(int y = yRange.fromInclusive(); y <= yRange.toExclusive(); y++) {
                    int deltaX = x - center.getX(), deltaY = y - center.getY();
                    double multiplier = gauss.get(deltaX + gaussK, deltaY + gaussK);

                    double theta = Math.atan2(dy.get(x, y, mode), dx.get(x, y, mode)) + Math.PI;
                    double value = gradient.get(x, y, mode) * multiplier;

                    int leftBin = Math.min((int)Math.floor(theta / step), binsCount - 1);
                    int rightBin = (leftBin + 1) % binsCount;

                    double ratio = (theta % step) / step;
                    bins[leftBin] += value * (1 - ratio);
                    bins[rightBin] += value * ratio;
                }

            return Vector.from(bins);
        }).apply(center));
    }

}
