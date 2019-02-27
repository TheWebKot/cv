package net.web_kot.cv.mat;

import net.web_kot.cv.utils.MathUtils;

import java.util.Arrays;

public class Vector extends Mat {

    public Vector(int width) {
        super(width, 1);
    }

    public Vector(int width, double[] buffer) {
        super(width, 1, buffer);
    }

    public double get(int index) {
        return get(index, 0);
    }

    public void set(int index, double value) {
        set(index, 0, value);
    }

    public int getSize() {
        return buffer.length;
    }

    public double getLength() {
        double sum = 0;
        for(double d : buffer) sum += MathUtils.sqr(d);
        return Math.sqrt(sum);
    }

    public Vector normalize() {
        double length = getLength();
        for(int i = 0; i < buffer.length; i++) buffer[i] = buffer[i] / length;

        return this;
    }

    public double distanceTo(Vector other) {
        if(other.buffer.length != buffer.length) throw new IllegalArgumentException("Vectors must have save size");

        double sum = 0;
        for(int i = 0; i < buffer.length; i++) sum += MathUtils.sqr(buffer[i] - other.buffer[i]);
        return Math.sqrt(sum);
    }

    public static Vector from(double[] vector) {
        return new Vector(vector.length, vector.clone());
    }

    @Override
    public String toString() {
        return Arrays.toString(buffer);
    }

}
