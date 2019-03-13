package net.web_kot.cv.scale;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.web_kot.cv.mat.Mat;
import net.web_kot.cv.processors.convolution.Convolution;
import net.web_kot.cv.processors.convolution.impl.Gauss;
import net.web_kot.cv.utils.MatUtils;
import net.web_kot.cv.utils.MathUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.*;
import static net.web_kot.cv.utils.MathUtils.*;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Pyramid {

    private static final double MIN_SIZE = 32;
    private static final int ADDITIONAL_LEVELS_PER_OCTAVE = 3;

    @Getter
    private final double sigma0;
    @Getter
    private final int octavesCount, octaveSize;
    @Getter @Accessors(fluent = true)
    private final boolean hasMinusOne;

    private final Map<Integer, List<ScaledMat>> octaves = new HashMap<>();
    private final Map<Integer, List<Mat>> DoG = new HashMap<>();

    public List<ScaledMat> getOctave(int octave) {
        if(octave == -1 && !hasMinusOne)
            throw new IllegalArgumentException("Pyramid build without -1 octave");
        if(octave < -1 || octave >= octavesCount)
            throw new IllegalArgumentException("Octave index out of bounds");
        return octaves.get(octave);
    }

    public ScaledMat get(int octave, int index) {
        List<ScaledMat> list = getOctave(octave);

        if(index < 0 || index >= octaveSize + ADDITIONAL_LEVELS_PER_OCTAVE)
            throw new IllegalArgumentException("Octave element index out of bounds");

        return list.get(index);
    }

    public Mat getDoG(int octave, int index) {
        return DoG.get(octave).get(index);
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

    public static Pyramid build(Mat source, int size, int layers, double sigma1, double sigma0, boolean minusOne) {
        if(minusOne && sigma1 > sigma0 / 2)
            throw new IllegalArgumentException("sigma1 must be <= sigma0/2 when generating -1 octave");
        if(sigma1 > sigma0)
            throw new IllegalArgumentException("sigma1 must be <= sigma0");

        Pyramid pyramid = new Pyramid(sigma0, size, layers, minusOne);
        double k = pow(2, 1D / layers);

        if(minusOne) {
            Mat image = Convolution.apply(source, Gauss.getKernel(sigma1)); // gauss(source, sigma1, sigma0 / 2);
            buildOctave(pyramid, -1, layers + ADDITIONAL_LEVELS_PER_OCTAVE, sigma0, k, upscale(image));
        }

        Mat image = gauss(source, sigma1 / Math.pow(2, minusOne ? -1 : 0), sigma0);
        for(int octave = 0; octave < size; octave++) {
            buildOctave(pyramid, octave, layers + ADDITIONAL_LEVELS_PER_OCTAVE, sigma0, k, image);
            List<ScaledMat> result = pyramid.octaves.get(octave);

            if(octave != size - 1) image = downsample(result.get(layers).withoutScaling());
        }

        return pyramid;
    }

    private static void buildOctave(Pyramid pyramid, int octave, int layers, double sigma0, double k, Mat image) {
        ArrayList<ScaledMat> list = new ArrayList<>(layers);
        ArrayList<Mat> dogList = new ArrayList<>(layers);

        double sigmaPrev = sigma0;
        list.add(new ScaledMat(image, octave, 0, sigmaPrev, sigmaPrev * pow(2, octave)));

        for(int i = 1; i < layers; i++) {
            double sigmaCurrent = sigmaPrev * k;

            Mat prevImage = list.get(list.size() - 1).withoutScaling();
            Mat currentImage = gauss(prevImage, sigmaPrev, sigmaCurrent);

            list.add(new ScaledMat(currentImage, octave, i + 1, sigmaCurrent, sigmaCurrent * pow(2, octave)));
            dogList.add(MatUtils.absDiff(currentImage, prevImage));

            sigmaPrev = sigmaCurrent;
        }

        pyramid.octaves.put(octave, list);
        pyramid.DoG.put(octave, dogList);
    }

    public ScaledMat L(double sigma) {
        int index = max(0, nearestGeometricProgressionElement(sigma0 / 2, pow(2, 1D / octaveSize), sigma));

        int octave = index / octaveSize - 1;
        int subIndex = index % octaveSize;

        if(octave >= octavesCount) return get(octavesCount - 1, octaveSize);
        if(octave == -1 && !hasMinusOne) return get(0, 0);
        return get(octave, subIndex);
    }

    private static Mat gauss(Mat mat, double current, double needed) {
        // current * current + σ * σ = needed * needed
        double sigma = sqrt(sqr(needed) - sqr(current));
        if(sigma < 1e-6) return mat.copy();
        return Convolution.apply(mat, Gauss.getKernel(sigma));
    }

    public static Mat downsample(Mat source) {
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
