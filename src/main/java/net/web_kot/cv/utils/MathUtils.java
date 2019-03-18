package net.web_kot.cv.utils;

import org.apache.commons.math3.linear.*;

public class MathUtils {

    public static int normalizeNumber(int minInclusive, int value, int maxExclusive) {
        return Math.max(minInclusive, Math.min(value, maxExclusive - 1));
    }

    public static double sqr(double a) {
        return a * a;
    }

    public static double log2(double a) {
        return Math.log(a) / Math.log(2);
    }

    // x(n) = a * (q ^ n) | https://math.stackexchange.com/a/447670
    public static int nearestGeometricProgressionElement(double a, double q, double value) {
        double y = (Math.log(value) - Math.log(a)) / Math.log(q);
        return (int)Math.round(y);
    }

    public static double[] solveLU(double[][] A, double[] B) {
        LUDecomposition decomposition = new LUDecomposition(new Array2DRowRealMatrix(A));
        RealVector vector = decomposition.getSolver().solve(new ArrayRealVector(B));
        return vector.toArray();
    }

    public static double dot(double[] a, double[] b) {
        double result = 0;
        for(int i = 0; i < a.length; i++) result += a[i] * b[i];
        return result;
    }

}
