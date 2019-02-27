package net.web_kot.cv.features.corners;

import lombok.experimental.UtilityClass;
import net.web_kot.cv.mat.Mat;
import net.web_kot.cv.utils.MathUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@UtilityClass
public class NonMaximumSuppression {

    private static final double C_ROBUST = 0.9;

    public List<PointOfInterest> filter(Mat mat, int minAmount) {
        double maxRadius = Math.sqrt(MathUtils.sqr(mat.getWidth()) + MathUtils.sqr(mat.getHeight()));
        return filter(CornersDetectionCommon.matToPoints(mat), minAmount, maxRadius);
    }

    public List<PointOfInterest> filter(List<PointOfInterest> pointsList, int minAmount, double maxRadius) {
        PointOfInterest[] points = pointsList.toArray(new PointOfInterest[0]);
        Arrays.sort(points, Comparator.comparingDouble(p -> -p.getValue()));

        boolean[] used = new boolean[points.length];

        double left = 0, right = maxRadius;
        while(right - left > 1) {
            double middle = (left + right) / 2;
            double radiusSquared = MathUtils.sqr(middle);

            if(countWithRadius(points, radiusSquared, used) < minAmount)
                right = middle;
            else
                left = middle;
        }

        countWithRadius(points, MathUtils.sqr(left), used);
        return IntStream.range(0, points.length).filter(i -> !used[i]).mapToObj(i -> points[i])
                        .collect(Collectors.toList());
    }

    private int countWithRadius(PointOfInterest[] points, double radiusSquared, boolean[] used) {
        Arrays.fill(used, false);

        for(int i = 0; i < points.length; i++) {
            if(used[i]) continue;

            for(int j = 0; j < points.length; j++) {
                if(i == j || points[i].squaredDistanceTo(points[j]) > radiusSquared) continue;
                used[j] |= points[j].getValue() - points[i].getValue() < 1e-6;
            }
        }

        int count = 0;
        for(int i = 0; i < points.length; i++)
            if(!used[i]) count++;

        return count;
    }

    public List<PointOfInterest> filter2(Mat mat, int amount) {
        return filter2(CornersDetectionCommon.matToPoints(mat), amount);
    }

    public List<PointOfInterest> filter2(List<PointOfInterest> points, int amount) {
        ArrayList<Pair<PointOfInterest, Double>> radiuses = new ArrayList<>(points.size());

        for(PointOfInterest point : points) {
            double r = Double.MAX_VALUE;

            for(PointOfInterest other : points)
                if(point.getValue() < C_ROBUST * other.getValue())
                    r = Math.min(r, point.squaredDistanceTo(other));

            radiuses.add(Pair.of(point, r));
        }

        radiuses.sort(Comparator.comparingDouble((ToDoubleFunction<Pair<?, Double>>) Pair::getRight).reversed());
        return radiuses.stream().limit(amount).map(Pair::getLeft).collect(Collectors.toList());
    }

}
