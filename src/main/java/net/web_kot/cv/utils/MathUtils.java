package net.web_kot.cv.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MathUtils {

    public int normalizeNumber(int minInclusive, int value, int maxExclusive) {
        return Math.max(minInclusive, Math.min(value, maxExclusive - 1));
    }

    public double sqr(double a) {
        return a * a;
    }

    public double log2(double a) {
        return Math.log(a) / Math.log(2);
    }

}
