package net.web_kot.cv.scale;

import lombok.Getter;
import net.web_kot.cv.mat.Mat;

public class ScaledMat extends Mat {

    @Getter
    private final int octave, index;
    @Getter
    private final double sigma, effectiveSigma;

    public ScaledMat(Mat mat, int octave, int index, double sigma, double effectiveSigma) {
        super(mat.getWidth(), mat.getHeight(), mat.getBuffer());

        this.octave = octave;
        this.index = index;

        this.sigma = sigma;
        this.effectiveSigma = effectiveSigma;
    }

    public Mat withoutScaling() {
        return new Mat(width, height, buffer);
    }

    @Override
    public int getWidth() {
        return width * (1 << octave);
    }

    @Override
    public int getHeight() {
        return height * (1 << octave);
    }

    @Override
    protected int getIndex(int x, int y) {
        if(isCoordinatesOutOfBounds(x, y)) throw new IllegalArgumentException("Coordinates out of bounds");

        x /= 1 << octave;
        y /= 1 << octave;

        return y * width + x;
    }

    public int backConvertCoordinate(int c) {
        return c * (1 << octave);
    }

    @Override
    public double[] getBuffer() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(int x, int y, double value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Mat copy() {
        throw new UnsupportedOperationException();
    }

    public String getDescription() {
        return String.format("{%d.%d, sigma=%.3f, effSigma=%.3f}", octave, index, sigma, effectiveSigma);
    }

}
