package net.web_kot.cv.utils;

import net.web_kot.cv.mat.EdgeWrapMode;
import net.web_kot.cv.mat.Mat;

public class MatUtils {

    public static Mat multiply(Mat matrix, double value) {
        Mat result = matrix.copy();

        double[] buffer = result.getBuffer();
        for(int i = 0; i < buffer.length; i++) buffer[i] *= value;

        return result;
    }

    public static Mat multiply(Mat a, Mat b) {
        if(a.getWidth() != b.getHeight()) throw new IllegalArgumentException("a width != b height");
        Mat result = new Mat(b.getWidth(), a.getHeight());

        for(int x = 0; x < result.getWidth(); x++)
            for(int y = 0; y < result.getHeight(); y++) {
                double value = 0;
                for(int i = 0; i < a.getWidth(); i++) value += a.get(i, y) * b.get(x, i);

                result.set(x, y, value);
            }

        return result;
    }

    public static Mat dilate(Mat image) {
        Mat result = image.withSameSize();
        for(int x = 0; x < result.getWidth(); x++)
            for(int y = 0; y < result.getHeight(); y++) {
                double max = 0;
                for(int dx = -1; dx <= 1; dx++)
                    for(int dy = -1; dy <= 1; dy++)
                        max = Math.max(max, image.get(x + dx, y + dy, EdgeWrapMode.BLACK));

                result.set(x, y, max);
            }

        return result;
    }

    public static Mat diff(Mat a, Mat b) {
        Mat result = a.withSameSize();

        double[] buffer = result.getBuffer(), bufferA = a.getBuffer(), bufferB = b.getBuffer();
        for(int i = 0; i < buffer.length; i++) buffer[i] = bufferA[i] - bufferB[i];

        return result;
    }

}
