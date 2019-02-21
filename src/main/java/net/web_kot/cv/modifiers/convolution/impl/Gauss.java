package net.web_kot.cv.modifiers.convolution.impl;

import net.web_kot.cv.datastructure.Kernel;
import net.web_kot.cv.modifiers.convolution.AbstractConvolution;
import net.web_kot.cv.utils.MathUtils;

public class Gauss extends AbstractConvolution {
    
    private static final double sigma = 10;
    
    @Override
    public Kernel getKernel() {
        int size = (int)Math.ceil(3 * sigma);
        double[][] kernel = new double[size * 2 + 1][size * 2 + 1];
        
        double prefix = 1D / (2 * Math.PI * MathUtils.sqr(sigma));
        for(int x = -size; x <= size; x++)
            for(int y = -size; y <= size; y++) {
                double value = Math.exp(-(MathUtils.sqr(x) + MathUtils.sqr(y)) / (2 * MathUtils.sqr(sigma)));
                kernel[x + size][y + size] = prefix * value;
            }
        
        return Kernel.of(kernel);
    }
    
}
