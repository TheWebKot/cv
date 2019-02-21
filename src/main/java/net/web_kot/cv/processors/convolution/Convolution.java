package net.web_kot.cv.processors.convolution;

import lombok.experimental.UtilityClass;
import net.web_kot.cv.mat.EdgeWrapMode;
import net.web_kot.cv.mat.Mat;

@UtilityClass
public class Convolution {

    public Mat apply(Mat source, Mat kernel) {
        return apply(source, kernel, EdgeWrapMode.DEFAULT);
    }

    public Mat apply(Mat source, Mat kernel, EdgeWrapMode mode) {
        Mat target = source.withSameSize();
        apply(source, target, kernel, mode);
        return target;
    }

    public void apply(Mat source, Mat target, Mat kernel) {
        apply(source, target, kernel, EdgeWrapMode.DEFAULT);
    }

    public void apply(Mat source, Mat target, Mat kernel, EdgeWrapMode mode) {
        int k = kernel.getWidth() / 2, h = kernel.getHeight() / 2;

        for(int x = 0; x < source.getWidth(); x++)
            for(int y = 0; y < source.getHeight(); y++) {
                double value = 0;

                for(int dx = -k; dx <= k; dx++)
                    for(int dy = -h; dy <= h; dy++)
                        value += source.get(x - dx, y - dy, mode) * kernel.get(dx + k, dy + h);

                target.set(x, y, value);
            }
    }

    public Mat apply(Mat source, SeparableKernel kernel) {
        return apply(source, kernel, EdgeWrapMode.DEFAULT);
    }

    public Mat apply(Mat source, SeparableKernel kernel, EdgeWrapMode mode) {
        Mat target = source.withSameSize();
        apply(source, target, kernel, mode);
        return target;
    }

    public void apply(Mat source, Mat target, SeparableKernel kernel) {
        apply(source, target, kernel, EdgeWrapMode.DEFAULT);
    }

    public void apply(Mat source, Mat target, SeparableKernel kernel, EdgeWrapMode mode) {
        int k = kernel.getXVector().getWidth() / 2, h = kernel.getYVector().getWidth() / 2;

        Mat tmp = source.copy();
        for(int x = 0; x < source.getWidth(); x++)
            for(int y = 0; y < source.getHeight(); y++) {
                double value = 0;

                for(int dx = -k; dx <= k; dx++)
                    value += source.get(x - dx, y, mode) * kernel.getXVector().get(dx + k, 0);

                tmp.set(x, y, value);
            }

        for(int x = 0; x < source.getWidth(); x++)
            for(int y = 0; y < source.getHeight(); y++) {
                double value = 0;

                for(int dy = -h; dy <= h; dy++)
                    value += tmp.get(x, y - dy, mode) * kernel.getYVector().get(dy + h, 0);

                target.set(x, y, value);
            }
    }

}
