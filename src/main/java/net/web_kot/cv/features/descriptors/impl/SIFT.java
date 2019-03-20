package net.web_kot.cv.features.descriptors.impl;

import net.web_kot.cv.features.blobs.BlobsDetector;
import net.web_kot.cv.features.blobs.RawBlob;
import net.web_kot.cv.features.corners.NonMaximumSuppression;
import net.web_kot.cv.features.corners.PointOfInterest;
import net.web_kot.cv.features.descriptors.Descriptor;
import net.web_kot.cv.mat.Mat;
import net.web_kot.cv.processors.common.Normalization;
import net.web_kot.cv.scale.Pyramid;
import net.web_kot.cv.scale.ScaledMat;
import net.web_kot.cv.utils.MathUtils;

import java.util.*;
import java.util.function.Function;

public class SIFT {

    private static final int OCTAVE_LAYERS = 3;
    private static final double PYRAMID_SIGMA = 1.6;

    private static final int MAX_CLARIFICATION_STEPS = 5;
    private static final int IMAGE_BORDER = 3;

    private static final double CONTRAST_THRESHOLD = 0.04;
    private static final double EDGE_THRESHOLD = 10;

    private static final int BINS_COUNT = 8;
    private static final int GRID_SIZE = 4;

    private static final double VECTOR_THRESHOLD = 0.2;

    public static List<Descriptor> calculate(Mat image) {
        return calculate(image, true);
    }

    public static List<Descriptor> calculate(Mat image, boolean triLinear) {
        return calculate(SIFT::findKeyPoints, image, 100, triLinear);
    }

    public static List<Descriptor> calculate(Function<Pyramid, List<PointOfInterest>> keyPointsFunction,
                                             Mat image, int maxPoints, boolean triLinear) {
        Pyramid pyramid = Pyramid.build(image, OCTAVE_LAYERS, 0.5, PYRAMID_SIGMA, true);
        return calculate(image, pyramid, keyPointsFunction.apply(pyramid), maxPoints, triLinear);
    }

    private static List<Descriptor> calculate(Mat image, Pyramid pyramid, List<PointOfInterest> points,
                                              int maxPoints, boolean triLinear) {
        Normalization.apply(points);
        points = NonMaximumSuppression.filter(points, maxPoints, NonMaximumSuppression.maxRadius(image));

        HashMap<ScaledMat, List<PointOfInterest>> groups = new HashMap<>();
        for(PointOfInterest p : points) {
            ScaledMat layer = pyramid.getOctave(p.getOctave()).get(0);
            List<PointOfInterest> group = groups.computeIfAbsent(layer, l -> new LinkedList<>());
            group.add(p);
        }

        List<Descriptor> descriptors = new ArrayList<>();
        for(Map.Entry<ScaledMat, List<PointOfInterest>> group : groups.entrySet())
            descriptors.addAll(RotationInvariant.calculate(group.getKey().withoutScaling(), group.getValue(),
                                                           GRID_SIZE, BINS_COUNT, triLinear));

        for(Descriptor d : descriptors) {
            double[] v = d.getVector().getBuffer();
            for(int i = 0; i < v.length; i++)
                if(v[i] > VECTOR_THRESHOLD) v[i] = VECTOR_THRESHOLD;

            d.getVector().normalize();
        }

        return descriptors;
    }

    public static List<PointOfInterest> findKeyPoints(Pyramid pyramid) {
        ArrayList<PointOfInterest> points = new ArrayList<>();
        for(RawBlob b : BlobsDetector.findRaw(pyramid, IMAGE_BORDER)) {
            PointOfInterest p = pointClarification(pyramid, b.getOctave(), b.getLayer(), b.getX(), b.getY());
            if(p != null) points.add(p);
        }

        return points;
    }

    private static PointOfInterest pointClarification(Pyramid pyramid, int octave, int layer, int x, int y) {
        double[] dD = null, X = null;
        double dxx = 0, dyy = 0, dxy = 0;

        int step;
        for(step = 0; step < MAX_CLARIFICATION_STEPS; step++) {
            Mat img = pyramid.getDoG(octave, layer);
            Mat prev = pyramid.getDoG(octave, layer - 1), next = pyramid.getDoG(octave, layer + 1);

            dD = new double[] {
                    (img.get(x + 1, y) - img.get(x - 1, y)) / 2,
                    (img.get(x, y + 1) - img.get(x, y - 1)) / 2,
                    (next.get(x, y) - prev.get(x, y)) / 2
            };

            double v2 = img.get(x, y) * 2;

            dxx = img.get(x + 1, y) + img.get(x - 1, y) - v2;
            dyy = img.get(x, y + 1) + img.get(x, y - 1) - v2;
            double dss = next.get(x, y) + prev.get(x, y) - v2;

            dxy = (img.get(x + 1, y + 1) - img.get(x - 1, y + 1) - img.get(x + 1, y - 1) + img.get(x - 1, y - 1)) / 4;
            double dxs = (next.get(x + 1, y) - next.get(x - 1, y) - prev.get(x + 1, y) + prev.get(x - 1, y)) / 4;
            double dys = (next.get(x, y + 1) - next.get(x, y - 1) - prev.get(x, y + 1) + prev.get(x, y - 1)) / 4;

            double[][] d2D = {
                    { dxx, dxy, dxs },
                    { dxy, dyy, dys },
                    { dxs, dys, dss },
            };

            try {
                X = MathUtils.solveLU(d2D, dD);
                for(int i = 0; i < X.length; i++) X[i] *= -1;
            } catch(Exception e) {
                return null; // singular matrix
            }

            if(Math.abs(X[0]) < 0.5 && Math.abs(X[1]) < 0.5 && Math.abs(X[2]) < 0.5) break;

            x += (int)Math.round(X[0]);
            y += (int)Math.round(X[1]);
            layer += (int)Math.round(X[2]);

            if(layer < 1 || layer > pyramid.getOctaveSize() ||
                    x < IMAGE_BORDER || x >= img.getWidth() - IMAGE_BORDER ||
                    y < IMAGE_BORDER || y >= img.getHeight() - IMAGE_BORDER)
                return null;
        }

        if(step >= MAX_CLARIFICATION_STEPS) return null;

        double contr;
        {
            Mat img = pyramid.getDoG(octave, layer);

            contr = img.get(x, y) + MathUtils.dot(dD, X) * 0.5;
            if(Math.abs(contr) * pyramid.getOctaveSize() < CONTRAST_THRESHOLD) return null;

            double tr = dxx + dyy;
            double det = dxx * dyy - dxy * dxy;

            if(det <= 0 || MathUtils.sqr(tr) * EDGE_THRESHOLD >= MathUtils.sqr(EDGE_THRESHOLD + 1) * det) return null;
        }

        double size = pyramid.getSigma0() * Math.pow(2, octave + (layer + X[2]) / pyramid.getOctaveSize() + 1);

        PointOfInterest point = new PointOfInterest(coord(x + X[0], octave), coord(y + X[1], octave), contr);
        return point.setOctave(octave).setLayer(layer).setSize(size);
    }

    private static int coord(double value, int octave) {
        if(octave == -1) return (int)Math.round(value / 2);
        return (int)Math.round(value * (1 << octave));
    }

}
