package net.web_kot.cv.features.descriptors.impl;

import net.web_kot.cv.features.corners.PointOfInterest;
import net.web_kot.cv.features.descriptors.Descriptor;
import net.web_kot.cv.mat.Mat;
import net.web_kot.cv.processors.convolution.impl.Gauss;
import net.web_kot.cv.processors.convolution.impl.gradient.Gradient;
import net.web_kot.cv.processors.convolution.impl.gradient.GradientMatrices;

import java.util.*;

import static net.web_kot.cv.features.descriptors.impl.HOG.calculateHistograms;
import static net.web_kot.cv.features.descriptors.impl.HOG.toVector;

public class RotationInvariant {

    private static final int DEFAULT_BINS_COUNT = 8;

    private static final int DEFAULT_GRID_SIZE = 4;
    private static final int DEFAULT_BLOCK_SIZE = 4;

    private static final int FULL_BINS_COUNT = 36;
    private static final double NEXT_PEAK_THRESHOLD = 0.8;

    public static List<Descriptor> calculate(Mat image, Iterable<PointOfInterest> points) {
        return calculate(image, points, DEFAULT_GRID_SIZE, DEFAULT_BLOCK_SIZE, DEFAULT_BINS_COUNT);
    }

    public static List<Descriptor> calculate(Mat image, Iterable<PointOfInterest> points,
                                             int gridSize, int blockSize, int binsCount) {
        double fullStep = 2 * Math.PI / FULL_BINS_COUNT;

        Mat dx = image.withSameSize(), dy = image.withSameSize(), gradient = image.withSameSize();
        Gradient.apply(image, gradient, dx, dy, GradientMatrices.SOBEL);

        ArrayList<Descriptor> descriptors = new ArrayList<>();
        for(PointOfInterest point : points) {
            double sigma = gridSize * blockSize / 3D, sigma2 = sigma;
            if(point.getSize() != null) {
                double scale = point.getSize() / Math.pow(2, point.getOctave() + 1);

                sigma = 1.5 * scale;
                sigma2 = gridSize * blockSize / 3D * scale;
            }

            // Calculate histograms for full patch with FULL_BINS_COUNT bins
            double[] full = calculateHistograms(gradient, dx, dy, Gauss.getFullKernel(sigma),
                                                1, (int)Math.round(sigma * 3) * 2 + 1, FULL_BINS_COUNT, point, 0)[0][0];

            // Peaks
            int peak = findMaximum(full, -1);
            int secondPeak = findMaximum(full, peak);

            ArrayList<Double> angles = new ArrayList<>();
            angles.add(2 * Math.PI - peak * fullStep);

            if(full[secondPeak] / full[peak] > NEXT_PEAK_THRESHOLD) angles.add(secondPeak * fullStep);

            // Rotate and findKeyPoints descriptor
            Mat gauss2 = Gauss.getFullKernel(sigma2);
            for(Double angle : angles)
                descriptors.add(Descriptor.of(point, toVector(
                        calculateHistograms(gradient, dx, dy, gauss2, gridSize, blockSize, binsCount, point, angle),
                        gridSize, binsCount
                )).setAngle(angle));
        }

        return descriptors;
    }

    private static int findMaximum(double[] array, int exclude) {
        int index = -1;
        for(int i = 0; i < array.length; i++)
            if(i != exclude && (index == -1 || array[i] > array[index]))
                index = i;

        return index;
    }

}
