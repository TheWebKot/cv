package net.web_kot.cv.lab3;

import net.web_kot.cv.features.edges.Canny;
import net.web_kot.cv.mat.Mat;
import net.web_kot.cv.utils.IOUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;

import static net.web_kot.cv.features.edges.Canny.*;

public class TestCanny {

    private static final File src = new File("test/building.jpg");

    public static void main(String[] args) throws Exception {
        Mat image = IOUtils.readGreyscaleFromFile(src);

        Mat m1 = image.withSameSize(), m2 = image.withSameSize(), m3 = image.withSameSize();
        Mat edges = Canny.apply(image, m1, m2, m3, DEFAULT_MIN, DEFAULT_MAX, DEFAULT_TRACKING_RADIUS);

        FileUtils.copyFile(src, new File("test/canny/0.jpg"));

        IOUtils.writeToJpegFile(m1, new File("test/canny/1.jpg"));
        IOUtils.writeToJpegFile(m2, new File("test/canny/2.jpg"));
        IOUtils.writeToJpegFile(m3, new File("test/canny/3.jpg"));

        IOUtils.writeToJpegFile(edges, new File("test/canny/5.jpg"));
    }

}
