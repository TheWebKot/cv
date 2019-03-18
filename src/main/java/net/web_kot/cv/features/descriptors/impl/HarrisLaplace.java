package net.web_kot.cv.features.descriptors.impl;

import net.web_kot.cv.features.blobs.Blob;
import net.web_kot.cv.features.blobs.BlobsDetector;
import net.web_kot.cv.features.corners.Harris;
import net.web_kot.cv.features.corners.PointOfInterest;
import net.web_kot.cv.mat.Mat;
import net.web_kot.cv.scale.Pyramid;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class HarrisLaplace {

    private static final int HARRIS_WINDOW_SIZE = 3;
    private static final double HARRIS_THRESHOLD = 0.02;

    private static final double DOG_THRESHOLD = 0.03;

    public static List<PointOfInterest> findKeyPoints(Pyramid pyramid) {
        HashMap<Pair<Integer, Integer>, Harris> cache = new HashMap<>();

        ArrayList<PointOfInterest> points = new ArrayList<>();
        for(Blob b : BlobsDetector.find(pyramid)) {
            if(Math.abs(b.getRaw().getValue()) < DOG_THRESHOLD) continue;

            Harris detector = cache.computeIfAbsent(Pair.of(b.getRaw().getOctave(), b.getRaw().getLayer()), p ->
                    Harris.detector(pyramid.get(p.getLeft(), p.getRight()).withoutScaling(), HARRIS_WINDOW_SIZE)
            );

            double value = detector.calc(b.getRaw().getX(), b.getRaw().getY());
            if(value >= HARRIS_THRESHOLD) {
                int octave = b.getRaw().getOctave(), layer = b.getRaw().getLayer();
                double size = pyramid.getSigma0() * Math.pow(2, octave + layer / pyramid.getOctaveSize() + 1);

                PointOfInterest point = new PointOfInterest(b.getX(), b.getY(), value);
                points.add(point.setLayer(octave).setLayer(layer).setSize(size));
            }
        }

        return points;
    }

}
