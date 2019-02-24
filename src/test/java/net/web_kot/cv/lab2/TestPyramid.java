package net.web_kot.cv.lab2;

import net.web_kot.cv.mat.Mat;
import net.web_kot.cv.scale.Pyramid;
import net.web_kot.cv.scale.ScaledMat;
import net.web_kot.cv.utils.IOUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Locale;

public class TestPyramid {

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.US);
        FileUtils.deleteDirectory(new File("test/pyramid/build/"));

        Mat image = IOUtils.readGreyscaleFromFile(new File("test/cat.jpg"));
        Pyramid pyramid = Pyramid.build(image, 3, 0D, 2D, true);

        for(int i = -1; i < pyramid.getOctavesCount(); i++)
            for(int j = 0; j <= pyramid.getOctaveSize(); j++) {
                ScaledMat scaled = pyramid.get(i, j);

                String fileName = "test/pyramid/build/" + scaled.getDescription() + ".jpeg";
                IOUtils.writeToJpegFile(scaled.withoutScaling(), new File(fileName));
            }
    }

}
