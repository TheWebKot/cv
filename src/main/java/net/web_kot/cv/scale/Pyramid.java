package net.web_kot.cv.scale;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.web_kot.cv.mat.Mat;
import net.web_kot.cv.processors.convolution.Convolution;
import net.web_kot.cv.processors.convolution.impl.Gauss;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.*;
import static net.web_kot.cv.utils.MathUtils.*;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Pyramid {

    private static final double MIN_SIZE = 32;

    @Getter
    private final int octavesCount, octaveSize;
    private final Map<Integer, List<ScaledMat>> images = new HashMap<>();

    public ScaledMat get(int octave, int index) {
        return images.get(octave).get(index);
    }

    public static Pyramid build(Mat source, int octaveSize, double sourceSigma, double sigma0) {
        int octavesCount = 1 + (int)min(
                floor(log2(source.getWidth() / MIN_SIZE)),
                floor(log2(source.getHeight() / MIN_SIZE))
        );
        return build(source, octavesCount, octaveSize, sourceSigma, sigma0);
    }

    public static Pyramid build(Mat source, int octavesCount, int octaveSize, double sourceSigma, double sigma0) {
        if(sourceSigma > sigma0)
            throw new IllegalArgumentException("Source sigma must be less than or equal to sigma0");

        Pyramid pyramid = new Pyramid(octavesCount, octaveSize);
        double k = pow(2, 1D / octaveSize);

        Mat image = gauss(source, sourceSigma, sigma0);
        for(int octave = 0; octave < octavesCount; octave++) {
            double sigma = sigma0;

            ArrayList<ScaledMat> list = new ArrayList<>(octaveSize + 1);
            list.add(new ScaledMat(image, octave, 0, sigma, sigma * (1 << octave)));

            for(int i = 0; i < octaveSize; i++) {
                double nextSigma = sigma * k;
                image = gauss(image, sigma, nextSigma);
                list.add(new ScaledMat(image, octave, i + 1, nextSigma, nextSigma * (1 << octave)));

                sigma = nextSigma;
            }

            pyramid.images.put(octave, list);
            if(octave != octavesCount - 1) image = downsample(image);
        }

        return pyramid;
    }

    private static Mat gauss(Mat mat, double current, double needed) {
        // current * current + σ * σ = needed * needed
        double sigma = sqrt(sqr(needed) - sqr(current));
        if(sigma < 1e-6) return mat.copy();
        return Convolution.apply(mat, Gauss.getKernel(sigma));
    }

    private static Mat downsample(Mat source) {
        Mat result = new Mat(source.getWidth() / 2, source.getHeight() / 2);

        for(int x = 0; x < result.getWidth(); x++)
            for(int y = 0; y < result.getHeight(); y++)
                result.set(x, y, source.get(x * 2, y * 2));

        return result;
    }

}
