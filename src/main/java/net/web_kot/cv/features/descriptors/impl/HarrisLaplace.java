package net.web_kot.cv.features.descriptors.impl;

import net.web_kot.cv.features.corners.Harris;
import net.web_kot.cv.features.corners.NonMaximumSuppression;
import net.web_kot.cv.features.corners.PointOfInterest;
import net.web_kot.cv.features.descriptors.Descriptor;
import net.web_kot.cv.mat.EdgeWrapMode;
import net.web_kot.cv.mat.Mat;
import net.web_kot.cv.processors.common.Normalization;
import net.web_kot.cv.scale.Pyramid;
import net.web_kot.cv.scale.ScaledMat;
import net.web_kot.cv.utils.MatUtils;
import net.web_kot.cv.utils.MathUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class HarrisLaplace {

    private static final int OCTAVE_SIZE = 4;
    private static final double PYRAMID_SIGMA = 1;

    private static final int HARRIS_WINDOW_SIZE = 3;
    private static final double HARRIS_THRESHOLD = 0.1;

    private static final double DOG_THRESHOLD = 0.01;

    public static List<Descriptor> calculate(Mat image) {
        return calculate(image, 50);
    }

    public static List<Descriptor> calculate(Mat image, int maxPoints) {
        Pyramid pyramid = Pyramid.build(image, OCTAVE_SIZE, 0.5, PYRAMID_SIGMA, true);

        double maxRadius = NonMaximumSuppression.maxRadius(image);
        List<PointOfInterest> points = NonMaximumSuppression.filter(findPoints(pyramid, image), maxPoints, maxRadius);

        HashMap<ScaledMat, LinkedList<PointOfInterest>> groups = new HashMap<>();
        for(PointOfInterest point : points) {
            ScaledMat target = pyramid.get(point.getOctave(), point.getLayer());

            LinkedList<PointOfInterest> group = groups.computeIfAbsent(target, k -> new LinkedList<>());
            group.add(point);
        }

        ArrayList<Descriptor> descriptors = new ArrayList<>();
        for(Map.Entry<ScaledMat, LinkedList<PointOfInterest>> group : groups.entrySet())
            descriptors.addAll(RotationInvariant.calculate(group.getKey().withoutScaling(), group.getValue()));

        return descriptors;
    }

    private static List<PointOfInterest> findPoints(Pyramid pyramid, Mat image) {
        int layersNum = pyramid.getOctaveSize();

        ArrayList<PointOfInterest> points = new ArrayList<>();
        for(int octave = -1; octave < pyramid.getOctavesCount(); octave++) {
            for(int layer = 1; layer <= layersNum; layer++) {
                if(octave == -1) layer = layersNum; // Only last image on -1 octave

                Mat layerMat = pyramid.get(octave, layer - 1).withoutScaling();

                Mat response = Harris.findRaw(layerMat, HARRIS_WINDOW_SIZE, EdgeWrapMode.DEFAULT);
                response = Normalization.apply(response);

                Mat dilate = MatUtils.dilate(response);

                Mat prev = pyramid.getDoG(octave, layer - 1);
                Mat current = pyramid.getDoG(octave, layer);
                Mat next = pyramid.getDoG(octave, layer + 1);

                for(int x = 1; x < layerMat.getWidth() - 1; x++)
                    for(int y = 1; y < layerMat.getHeight() - 1; y++) {
                        double value = response.get(x, y);
                        if(value < HARRIS_THRESHOLD) continue;
                        if(Math.abs(value - dilate.get(x, y)) > 1e-6) continue;

                        if(current.get(x, y) < DOG_THRESHOLD) continue;
                        if(prev.get(x, y) >= current.get(x, y) || current.get(x, y) <= next.get(x, y)) continue;

                        double k = Math.pow(2, octave);

                        int pointX = (int)Math.round(x * k + k / 2);
                        int pointY = (int)Math.round(y * k + k / 2);

                        points.add(new PointOfInterest(pointX, pointY, value).setOctave(octave).setLayer(layer));
                    }
            }
        }

        return points;
    }

}
