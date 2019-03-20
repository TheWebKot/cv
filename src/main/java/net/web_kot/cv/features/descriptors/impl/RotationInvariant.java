package net.web_kot.cv.features.descriptors.impl;

import net.web_kot.cv.features.corners.PointOfInterest;
import net.web_kot.cv.features.descriptors.Descriptor;
import net.web_kot.cv.mat.Mat;
import net.web_kot.cv.processors.convolution.impl.gradient.Gradient;
import net.web_kot.cv.processors.convolution.impl.gradient.GradientMatrices;

import java.util.*;

import static net.web_kot.cv.features.descriptors.impl.HOG.calculateHistograms;
import static net.web_kot.cv.features.descriptors.impl.HOG.toVector;

public class RotationInvariant {

    private static final int DEFAULT_BINS_COUNT = 8;
    private static final int DEFAULT_GRID_SIZE = 4;

    private static final int FULL_BINS_COUNT = 36;
    private static final double NEXT_PEAK_THRESHOLD = 0.8;

    public static List<Descriptor> calculate(Mat image, Iterable<PointOfInterest> points) {
        return calculate(image, points, DEFAULT_GRID_SIZE, DEFAULT_BINS_COUNT, false);
    }

    public static List<Descriptor> calculate(Mat image, Iterable<PointOfInterest> points,
                                             int gridSize, int binsCount, boolean triLinear) {
        double fullStep = 2 * Math.PI / FULL_BINS_COUNT;

        Mat dx = image.withSameSize(), dy = image.withSameSize(), g = image.withSameSize();
        Gradient.apply(image, g, dx, dy, GradientMatrices.SOBEL);

        ArrayList<Descriptor> descriptors = new ArrayList<>();
        for(PointOfInterest point : points) {
            double scale = 1.6;
            if(point.getSize() != null) scale = point.getSize() / Math.pow(2, point.getOctave() + 1);

            double[] full = calculateHistograms(g, dx, dy, scale * 1.5, 1, FULL_BINS_COUNT, point, 0, false)[0][0];

            // Peaks
            int peak = findMaximum(full, -1);
            int secondPeak = findMaximum(full, peak);

            ArrayList<Double> angles = new ArrayList<>();
            angles.add(interpolatePeak(full, peak, fullStep));

            if(full[secondPeak] / full[peak] > NEXT_PEAK_THRESHOLD)
                angles.add(interpolatePeak(full, secondPeak, fullStep));

            // Rotate and find key points descriptors
            for(Double angle : angles)
                descriptors.add(Descriptor.of(point, toVector(
                        calculateHistograms(g, dx, dy, scale * 3, gridSize, binsCount, point, angle, triLinear),
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

    private static double interpolatePeak(double[] hist, int index, double fullStep) {
        int left = index == 0 ? hist.length - 1 : index - 1;
        int right = index == hist.length - 1 ? 0 : index + 1;

        // ??? // Should be index + delta
        double result = index - 0.5 * (hist[left] - hist[right]) / (hist[left] - 2 * hist[index] + hist[right]);

        if(result < 0) result += hist.length;
        if(result >= hist.length) result -= hist.length;

        return 2 * Math.PI - result * fullStep;
    }

}
