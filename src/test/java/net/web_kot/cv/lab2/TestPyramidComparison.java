package net.web_kot.cv.lab2;

import net.web_kot.cv.mat.Mat;
import net.web_kot.cv.processors.convolution.Convolution;
import net.web_kot.cv.processors.convolution.impl.Gauss;
import net.web_kot.cv.scale.Pyramid;
import net.web_kot.cv.scale.ScaledMat;
import net.web_kot.cv.utils.IOUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Locale;

public class TestPyramidComparison {

    private static final int OCTAVE_SIZE = 3;
    private static final double SIGMA = 2;

    private static final double MAX_COMPARISON_SIGMA = 32;

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.US);
        FileUtils.deleteDirectory(new File("test/pyramid/compare/"));

        Mat image = IOUtils.readGreyscaleFromFile(new File("test/head.jpg"));
        Pyramid pyramid = Pyramid.build(image, OCTAVE_SIZE, 0, SIGMA, true);

        double sigma = SIGMA / 2, k = Math.pow(2, 1D / OCTAVE_SIZE);
        do {
            Mat mat = Convolution.apply(image, Gauss.getKernel(sigma));

            String fileName = String.format("test/pyramid/compare/%.3f.jpg", sigma);
            IOUtils.writeToJpegFile(mat, new File(fileName));

            sigma *= k;
        } while(!(sigma > MAX_COMPARISON_SIGMA));

        loop: for(int i = -1; i < pyramid.getOctavesCount(); i++)
            for(int j = 0; j <= pyramid.getOctaveSize(); j++) {
                if(i != -1 && j == 0) continue; // Skip first image in all octaves exclude -1

                ScaledMat scaled = pyramid.get(i, j);
                if(scaled.getEffectiveSigma() > MAX_COMPARISON_SIGMA) break loop;

                String fileName = String.format("test/pyramid/compare/%.3f-scale.jpg", scaled.getEffectiveSigma());
                IOUtils.writeToJpegFile(scaled, new File(fileName));
            }
    }

}
