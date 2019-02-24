package net.web_kot.cv.lab2;

import net.web_kot.cv.mat.Mat;
import net.web_kot.cv.scale.Pyramid;
import net.web_kot.cv.scale.ScaledMat;
import net.web_kot.cv.utils.IOUtils;

import java.io.File;
import java.util.Locale;
import java.util.Random;

public class TestPyramidLFunction {

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        Mat image = IOUtils.readGreyscaleFromFile(new File("test/cat.jpg"));
        Pyramid pyramid = Pyramid.build(image, 3, 0D, 2D, true);

        Random random = new Random(16);

        for(int i = 0; i < 30; i++) {
            double sigma = random.nextDouble() * 80;
            ScaledMat mat = pyramid.L(sigma);

            System.out.println(String.format("%6.3f --> %6.3f ( %d / %d )",
                                             sigma, mat.getEffectiveSigma(), mat.getOctave(), mat.getIndex()));
        }
    }

}
