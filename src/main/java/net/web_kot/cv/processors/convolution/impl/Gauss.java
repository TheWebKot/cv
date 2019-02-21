package net.web_kot.cv.processors.convolution.impl;

import lombok.experimental.UtilityClass;
import net.web_kot.cv.mat.Mat;
import net.web_kot.cv.utils.MathUtils;

@UtilityClass
public class Gauss {

    public Mat getKernel(double sigma) {
        int size = (int)Math.ceil(3 * sigma);
        double[][] matrix = new double[size * 2 + 1][size * 2 + 1];

        double prefix = 1D / (2 * Math.PI * MathUtils.sqr(sigma));
        for(int x = -size; x <= size; x++)
            for(int y = -size; y <= size; y++) {
                double value = Math.exp(-(MathUtils.sqr(x) + MathUtils.sqr(y)) / (2 * MathUtils.sqr(sigma)));
                matrix[x + size][y + size] = prefix * value;
            }

        return Mat.matrix(matrix);
    }

}
