package net.web_kot.cv.lab3;

import net.web_kot.cv.features.corners.Moravec;
import net.web_kot.cv.mat.Mat;
import net.web_kot.cv.utils.DrawUtils;
import net.web_kot.cv.utils.IOUtils;

import java.awt.image.BufferedImage;
import java.io.File;

public class TestMoravec {

    public static void main(String[] args) {
        Mat image = IOUtils.readGreyscaleFromFile(new File("test/blocks.gif"));

        Mat corners = Moravec.find(image, 2, 0.1);

        BufferedImage result = IOUtils.toBufferedImage(image);
        DrawUtils.drawCorners(result, corners);
        IOUtils.writeToJpegFile(result, new File("test/corners/moravec.jpeg"));
    }

}
