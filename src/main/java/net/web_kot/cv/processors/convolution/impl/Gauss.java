package net.web_kot.cv.processors.convolution.impl;

import lombok.experimental.UtilityClass;
import net.web_kot.cv.mat.Mat;
import net.web_kot.cv.processors.convolution.SeparableKernel;
import net.web_kot.cv.utils.MathUtils;

@UtilityClass
public class Gauss {

    public SeparableKernel getKernel(double sigma) {
        return getKernel((int)Math.ceil(3 * sigma), sigma);
    }

    public SeparableKernel getKernel(int k, double sigma) {
        double[] vector = new double[k * 2 + 1];

        double prefix = 1D / (Math.sqrt(2 * Math.PI) * sigma);
        for(int x = 0; x <= k; x++)
            vector[-x + k] = vector[x + k] = prefix * Math.exp(-MathUtils.sqr(x) / (2 * MathUtils.sqr(sigma)));

        Mat mat = Mat.vector(vector);
        return SeparableKernel.of(mat, mat);
    }

}
