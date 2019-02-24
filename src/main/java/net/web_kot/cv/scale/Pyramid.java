package net.web_kot.cv.scale;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
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
    @Getter @Accessors(fluent = true)
    private final boolean hasMinusOne;
    private final Map<Integer, List<ScaledMat>> octaves = new HashMap<>();

    public ScaledMat get(int octave, int index) {
        return octaves.get(octave).get(index);
    }

    public static Pyramid build(Mat source, int octaveSize, double sourceSigma, double sigma0) {
        return build(source, octaveSize, sourceSigma, sigma0, false);
    }

    public static Pyramid build(Mat source, int octaveSize, double sourceSigma, double sigma0, boolean minusOne) {
        int octavesCount = 1 + (int)min(
                floor(log2(source.getWidth() / MIN_SIZE)),
                floor(log2(source.getHeight() / MIN_SIZE))
        );
        return build(source, octavesCount, octaveSize, sourceSigma, sigma0, minusOne);
    }

    public static Pyramid build(Mat source, int size, int octaveSize, double sigma1, double sigma0, boolean minusOne) {
        if(minusOne && sigma1 > sigma0 / 2)
            throw new IllegalArgumentException("sigma1 must be <= sigma0/2 when generating -1 octave");
        if(sigma1 > sigma0)
            throw new IllegalArgumentException("sigma1 must be <= sigma0");

        Pyramid pyramid = new Pyramid(size, octaveSize, minusOne);
        double k = pow(2, 1D / octaveSize);

        Mat image = gauss(source, sigma1, sigma0);
        for(int octave = 0; octave < size; octave++) {
            List<ScaledMat> list = buildOctave(octave, octaveSize, sigma0, k, image);
            pyramid.octaves.put(octave, list);

            if(octave != size - 1) image = downsample(list.get(octaveSize).withoutScaling());
        }

        if(minusOne) {
            image = gauss(source, sigma1, sigma0 / 2);
            pyramid.octaves.put(-1, buildOctave(-1, octaveSize, sigma0, k, upscale(image)));
        }

        return pyramid;
    }

    private static List<ScaledMat> buildOctave(int index, int octaveSize, double sigma0, double k, Mat image) {
        double sigma = sigma0;

        ArrayList<ScaledMat> list = new ArrayList<>(octaveSize + 1);
        list.add(new ScaledMat(image, index, 0, sigma, sigma * pow(2, index)));

        for(int i = 0; i < octaveSize; i++) {
            double nextSigma = sigma * k;
            image = gauss(image, sigma, nextSigma);
            list.add(new ScaledMat(image, index, i + 1, nextSigma, nextSigma * pow(2, index)));

            sigma = nextSigma;
        }

        return list;
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

    private static Mat upscale(Mat source) {
        Mat result = new Mat(source.getWidth() * 2, source.getHeight() * 2);

        for(int x = 0; x < result.getWidth(); x++)
            for(int y = 0; y < result.getHeight(); y++)
                result.set(x, y, source.get(x / 2, y / 2));

        return result;
    }

}
