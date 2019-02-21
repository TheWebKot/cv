package net.web_kot.cv.lab1;

import net.web_kot.cv.mat.Mat;
import net.web_kot.cv.processors.common.Normalization;
import net.web_kot.cv.processors.convolution.impl.gradient.Gradient;
import net.web_kot.cv.processors.convolution.impl.gradient.GradientMatrices;
import net.web_kot.cv.utils.IOUtils;

import java.io.File;

public class TestSobel {

    public static void main(String[] args) {
        Mat image = IOUtils.readGreyscaleFromFile(new File("test/wall.jpg"));

        Mat dx = image.withSameSize(), dy = image.withSameSize();
        image = Gradient.apply(image, dx, dy, GradientMatrices.SOBEL);

        IOUtils.writeToPngFile(Normalization.apply(dx), new File("test/sobel/dx.png"));
        IOUtils.writeToPngFile(Normalization.apply(dy), new File("test/sobel/dy.png"));
        IOUtils.writeToPngFile(Normalization.apply(image), new File("test/sobel/result.png"));
    }

}
