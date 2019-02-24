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

    // x(n) = a * (q ^ n) | https://math.stackexchange.com/a/447670
    public int nearestGeometricProgressionElement(double a, double q, double value) {
        double y = (Math.log(value) - Math.log(a)) / Math.log(q);
        return (int)Math.round(y);
    }

}
