package net.web_kot.cv.lab1;

import net.web_kot.cv.mat.Mat;
import net.web_kot.cv.processors.convolution.Convolution;
import net.web_kot.cv.processors.convolution.impl.Gauss;
import net.web_kot.cv.utils.IOUtils;

import java.io.File;

public class TestGauss {

    public static void main(String[] args) {
        Mat image = IOUtils.readGreyscaleFromFile(new File("test/Lenna.jpg"));

        image = Convolution.apply(image, Gauss.getKernel(4));

        IOUtils.writeToPngFile(image, new File("test/gauss/result.jpg"));
    }

}
