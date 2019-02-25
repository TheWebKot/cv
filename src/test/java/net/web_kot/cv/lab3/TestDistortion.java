package net.web_kot.cv.lab3;

import net.web_kot.cv.features.corners.Harris;
import net.web_kot.cv.features.corners.NonMaximumSuppression;
import net.web_kot.cv.features.corners.PointOfInterest;
import net.web_kot.cv.mat.Mat;
import net.web_kot.cv.utils.DrawUtils;
import net.web_kot.cv.utils.IOUtils;
import org.apache.commons.io.FileUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

public class TestDistortion {

    private static final String[] IMAGES = { "test/building.jpg", "test/building2.jpg" };

    public static void main(String[] args) throws Exception {
        FileUtils.deleteDirectory(new File("test/corners/compare/"));

        for(String entry : IMAGES) {
            String name = IOUtils.getFileName(entry);
            Mat image = IOUtils.readGreyscaleFromFile(new File(entry));

            Mat harris = Harris.find(image, 3, 0.1);
            List<PointOfInterest> points = NonMaximumSuppression.filter(harris, 128);

            BufferedImage result = IOUtils.toBufferedImage(image);
            DrawUtils.drawCorners(result, points);
            IOUtils.writeToJpegFile(result, new File("test/corners/compare/" + name + ".jpeg"));
        }
    }

}
