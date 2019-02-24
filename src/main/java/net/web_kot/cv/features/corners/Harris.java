package net.web_kot.cv.features.corners;

import lombok.experimental.UtilityClass;
import net.web_kot.cv.mat.EdgeWrapMode;
import net.web_kot.cv.mat.Mat;
import net.web_kot.cv.processors.convolution.impl.Gauss;
import net.web_kot.cv.processors.convolution.impl.gradient.Gradient;
import net.web_kot.cv.processors.convolution.impl.gradient.GradientMatrices;
import net.web_kot.cv.utils.MathUtils;

@UtilityClass
public class Harris {

    public Mat find(Mat image, int k, double threshold) {
        return find(image, k, threshold, EdgeWrapMode.DEFAULT);
    }

    public Mat find(Mat image, int k, double threshold, EdgeWrapMode mode) {
        Mat gauss = Gauss.getFullKernel(k / 3D);
        int gaussK = gauss.getWidth() / 2;

        Mat dx = image.withSameSize(), dy = image.withSameSize();
        Gradient.derivatives(image, dx, dy, GradientMatrices.SOBEL);

        Mat lambdas = image.withSameSize();
        for(int x = 0; x < image.getWidth(); x++)
            for(int y = 0; y < image.getHeight(); y++) {
                double A = 0, B = 0, C = 0;

                for(int u = -k; u <= k; u++)
                    for(int v = -k; v <= k; v++) {
                        double multiplier = gauss.get(u + gaussK, v + gaussK);

                        A += multiplier * MathUtils.sqr(dx.get(x + u, y + v, mode));
                        B += multiplier * dx.get(x + u, y + v, mode) * dy.get(x + u, y + v, mode);
                        C += multiplier * MathUtils.sqr(dy.get(x + u, y + v, mode));
                    }

                lambdas.set(x, y, calcLambdaMin(A, B, C));
            }

        return CornerDetectionCommon.normalizeAndFilter(lambdas, threshold);
    }

    private double calcLambdaMin(double A, double B, double C) {
        double a = 1, b = -(A + C), c = A * C - B * B;
        double D = b * b - 4 * a * c;

        if(D < 0 && D > -1e-6) D = 0;
        if(D < 0) return 0;

        double lambda1 = (-b + Math.sqrt(D)) / (2 * a);
        double lambda2 = (-b - Math.sqrt(D)) / (2 * a);

        return Math.min(lambda1, lambda2);
    }

}
