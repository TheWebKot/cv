package net.web_kot.cv.features.corners;

import lombok.experimental.UtilityClass;
import net.web_kot.cv.mat.EdgeWrapMode;
import net.web_kot.cv.mat.Mat;
import net.web_kot.cv.processors.convolution.Convolution;
import net.web_kot.cv.processors.convolution.impl.Gauss;
import net.web_kot.cv.utils.MathUtils;

import java.util.Arrays;

@UtilityClass
public class Moravec {

    private static final int[] DX = { -1,  0,  1, -1,  1, -1,  0,  1 };
    private static final int[] DY = { -1, -1, -1,  0,  0,  1,  1,  1 };

    public Mat find(Mat image, int k, double threshold) {
        return find(image, k, threshold, EdgeWrapMode.DEFAULT);
    }

    public Mat find(Mat image, int k, double threshold, EdgeWrapMode mode) {
        Mat s = image.withSameSize();
        Arrays.fill(s.getBuffer(), Double.POSITIVE_INFINITY);

        image = Convolution.apply(image, Gauss.getKernel(0.6));
        for(int x = 0; x < image.getWidth(); x++)
            for(int y = 0; y < image.getHeight(); y++)
                for(int i = 0; i < DX.length; i++) {
                    double sum = 0;
                    for(int u = -k; u <= k; u++)
                        for(int v = -k; v <= k; v++) {
                            double d = image.get(x + u, y + v, mode) - image.get(x + u + DX[i], y + v + DY[i], mode);
                            sum += MathUtils.sqr(d);
                        }

                    s.set(x, y, Math.min(s.get(x, y), sum));
                }

        return CornersDetectionCommon.normalizeAndFilter(s, threshold);
    }

}
