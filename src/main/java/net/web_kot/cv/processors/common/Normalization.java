package net.web_kot.cv.processors.common;

import net.web_kot.cv.mat.Mat;

public class Normalization {

    public static Mat apply(Mat source) {
        Mat target = source.withSameSize();
        apply(source, target);
        return target;
    }

    public static void apply(Mat source, Mat target) {
        apply(source, target, 0D, 1D);
    }

    public static void apply(Mat source, Mat target, double newMin, double newMax) {
        apply(source.getBuffer(), target.getBuffer(), newMin, newMax);
    }

    private static void apply(double[] source, double[] target, double newMin, double newMax) {
        double min = source[0], max = source[0];
        for(double value : source) {
            if(Double.isNaN(value)) continue;
            min = Math.min(min, value);
            max = Math.max(max, value);
        }

        for(int i = 0; i < source.length; i++)
            target[i] = (source[i] - min) * (newMax - newMin) / (max - min) + newMin;
    }

}
