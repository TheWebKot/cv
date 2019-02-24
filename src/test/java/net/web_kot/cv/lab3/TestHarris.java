package net.web_kot.cv.lab3;

import net.web_kot.cv.features.corners.Harris;
import net.web_kot.cv.mat.Mat;
import net.web_kot.cv.utils.DrawUtils;
import net.web_kot.cv.utils.IOUtils;

import java.awt.image.BufferedImage;
import java.io.File;

public class TestHarris {

    public static void main(String[] args) {
        //Mat image = IOUtils.readGreyscaleFromFile(new File("test/blocks.gif"));
        Mat image = IOUtils.readGreyscaleFromFile(new File("test/grid.png"));

        Mat corners = Harris.find(image, 2, 0.1);

        BufferedImage result = IOUtils.toBufferedImage(image);
        DrawUtils.drawCorners(result, corners);
        IOUtils.writeToJpegFile(result, new File("test/corners/harris.jpeg"));
    }

}
