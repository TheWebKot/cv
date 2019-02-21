package net.web_kot.cv.processors.convolution.impl.gradient;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.web_kot.cv.mat.Mat;
import net.web_kot.cv.processors.convolution.SeparableKernel;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GradientMatrices {

    private static final Mat SOBEL_VECTOR_A = Mat.vector(new double[] { -1, 0, 1 });
    private static final Mat SOBEL_VECTOR_B = Mat.vector(new double[] { 1, 2, 1 });

    public static final GradientMatrices SOBEL = GradientMatrices.of(
            SeparableKernel.of(SOBEL_VECTOR_A, SOBEL_VECTOR_B),
            SeparableKernel.of(SOBEL_VECTOR_B, SOBEL_VECTOR_A)
    );

    @Getter
    private final SeparableKernel kernelX, kernelY;

    public static GradientMatrices of(SeparableKernel kernelX, SeparableKernel kernelY) {
        return new GradientMatrices(kernelX, kernelY);
    }

}
