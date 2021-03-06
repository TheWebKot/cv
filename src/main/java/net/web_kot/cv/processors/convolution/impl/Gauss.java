package net.web_kot.cv.processors.convolution.impl;

import net.web_kot.cv.mat.Mat;
import net.web_kot.cv.mat.Vector;
import net.web_kot.cv.processors.convolution.SeparableKernel;
import net.web_kot.cv.utils.MathUtils;

public class Gauss {

    public static SeparableKernel getKernel(double sigma) {
        return getKernel((int)Math.ceil(3 * sigma), sigma);
    }

    public static SeparableKernel getKernel(int k, double sigma) {
        double[] vector = new double[k * 2 + 1];

        double prefix = 1D / (Math.sqrt(2 * Math.PI) * sigma);
        for(int x = 0; x <= k; x++)
            vector[-x + k] = vector[x + k] = prefix * Math.exp(-MathUtils.sqr(x) / (2 * MathUtils.sqr(sigma)));

        double sum = 0;
        for(int x = -k; x <= k; x++) sum += vector[x + k];
        for(int x = -k; x <= k; x++) vector[x + k] /= sum;

        Mat mat = Vector.from(vector);
        return SeparableKernel.of(mat, mat);
    }

    public static Mat getFullKernel(double sigma) {
        int size = (int)Math.ceil(3 * sigma);
        double[][] kernel = new double[size * 2 + 1][size * 2 + 1];

        double prefix = 1D / (2 * Math.PI * MathUtils.sqr(sigma));
        for(int x = -size; x <= size; x++)
            for(int y = -size; y <= size; y++) {
                double value = Math.exp(-(MathUtils.sqr(x) + MathUtils.sqr(y)) / (2 * MathUtils.sqr(sigma)));
                kernel[x + size][y + size] = prefix * value;
            }

        return Mat.from(kernel);
    }

}
