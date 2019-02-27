package net.web_kot.cv.processors.convolution.impl.gradient;

import net.web_kot.cv.mat.EdgeWrapMode;
import net.web_kot.cv.mat.Mat;
import net.web_kot.cv.processors.convolution.Convolution;
import net.web_kot.cv.utils.MathUtils;

public class Gradient {

    public static void derivatives(Mat source, Mat dx, Mat dy, GradientMatrices matrices) {
        derivatives(source, dx, dy, matrices, EdgeWrapMode.DEFAULT);
    }

    public static void derivatives(Mat source, Mat dx, Mat dy, GradientMatrices matrices, EdgeWrapMode mode) {
        Convolution.apply(source, dx, matrices.getKernelX(), mode);
        Convolution.apply(source, dy, matrices.getKernelY(), mode);
    }

    public static Mat apply(Mat source, GradientMatrices matrices) {
        Mat target = source.withSameSize();
        apply(source, target, matrices);
        return target;
    }

    public static Mat apply(Mat source, Mat dx, Mat dy, GradientMatrices matrices) {
        Mat target = source.withSameSize();
        apply(source, target, dx, dy, matrices);
        return target;
    }

    public static void apply(Mat source, Mat target, GradientMatrices matrices) {
        apply(source, target, matrices, EdgeWrapMode.DEFAULT);
    }

    public static void apply(Mat source, Mat target, GradientMatrices matrices, EdgeWrapMode mode) {
        Mat dx = source.withSameSize(), dy = source.withSameSize();
        apply(source, target, dx, dy, matrices, mode);
    }

    public static void apply(Mat source, Mat target, Mat dx, Mat dy, GradientMatrices matrices) {
        apply(source, target, dx, dy, matrices, EdgeWrapMode.DEFAULT);
    }

    public static void apply(Mat source, Mat target, Mat dx, Mat dy, GradientMatrices matrices, EdgeWrapMode mode) {
        derivatives(source, dx, dy, matrices, mode);

        for(int x = 0; x < source.getWidth(); x++)
            for(int y = 0; y < source.getHeight(); y++)
                target.set(x, y, Math.sqrt(MathUtils.sqr(dx.get(x, y)) + MathUtils.sqr(dy.get(x, y))));
    }

}
