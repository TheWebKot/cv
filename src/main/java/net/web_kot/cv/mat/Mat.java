package net.web_kot.cv.mat;

import lombok.*;
import net.web_kot.cv.utils.MathUtils;

@AllArgsConstructor
public class Mat {

    @Getter
    protected final int width, height;
    @Getter
    protected final double[] buffer;

    public Mat(int width, int height) {
        this(width, height, new double[width * height]);
    }

    private Mat(Mat source) {
        this(source.getWidth(), source.getHeight(), source.buffer.clone());
    }

    public void set(int x, int y, double value) {
        buffer[getIndex(x, y)] = value;
    }

    public double get(int x, int y) {
        return buffer[getIndex(x, y)];
    }

    public double get(int x, int y, EdgeWrapMode mode) {
        switch(mode) {
            case BLACK:
                if(isCoordinatesOutOfBounds(x, y)) return 0;
                break;
            case COPY:
            case DEFAULT:
                x = MathUtils.normalizeNumber(0, x, getWidth());
                y = MathUtils.normalizeNumber(0, y, getHeight());
                break;
            case MIRROR:
                if(x < 0 || x >= getWidth()) x = x - (x % getWidth()) * 2 - 1;
                if(y < 0 || y >= getHeight()) y = y - (y % getHeight()) * 2 - 1;
                break;
            case WRAP:
                x = (x + getWidth()) % getWidth();
                y = (y + getHeight()) % getHeight();
                break;
            default:
                throw new IllegalArgumentException("Unknown edge wrap mode " + mode);
        }
        return get(x, y);
    }

    protected int getIndex(int x, int y) {
        if(isCoordinatesOutOfBounds(x, y)) throw new IllegalArgumentException("Coordinates out of bounds");
        return y * getWidth() + x;
    }

    protected boolean isCoordinatesOutOfBounds(int x, int y) {
        return x < 0 || x >= getWidth() || y < 0 || y >= getHeight();
    }

    public Mat copy() {
        return new Mat(this);
    }

    public Mat withSameSize() {
        return new Mat(this.getWidth(), this.getHeight());
    }

    public static Mat vector(double[] vector) {
        return new Mat(vector.length, 1, vector.clone());
    }

    public static Mat matrix(double[][] matrix) {
        Mat result = new Mat(matrix[0].length, matrix.length);
        for(int i = 0; i < matrix.length; i++)
            System.arraycopy(matrix[i], 0, result.buffer, i * result.width, result.width);
        return result;
    }

}
