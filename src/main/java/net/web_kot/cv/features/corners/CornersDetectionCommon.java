package net.web_kot.cv.features.corners;

import net.web_kot.cv.mat.EdgeWrapMode;
import net.web_kot.cv.mat.Mat;
import net.web_kot.cv.processors.common.Normalization;

import java.util.ArrayList;
import java.util.List;

public class CornersDetectionCommon {

    public static Mat normalizeAndFilter(Mat mat, double threshold) {
        return filter(Normalization.apply(mat), threshold);
    }

    public static Mat filter(Mat mat, double threshold) {
        Mat result = mat.withSameSize();

        for(int x = 0; x < mat.getWidth(); x++)
            for(int y = 0; y < mat.getHeight(); y++) {
                if(mat.get(x, y) <= threshold || hasLargerNeighbour(mat, x, y)) continue;
                result.set(x, y, mat.get(x, y));
            }

        return result;
    }

    private static boolean hasLargerNeighbour(Mat mat, int x, int y) {
        for(int dx = -1; dx <= 1; dx++)
            for(int dy = -1; dy <= 1; dy++) {
                if(dx == 0 && dy == 0) continue;
                if(mat.get(x + dx, y + dy, EdgeWrapMode.BLACK) - mat.get(x, y) > 1e-6) return true;
            }
        return false;
    }

    public static List<PointOfInterest> matToPoints(Mat mat) {
        List<PointOfInterest> list = new ArrayList<>();
        for(int x = 0; x < mat.getWidth(); x++)
            for(int y = 0; y < mat.getHeight(); y++)
                if(mat.get(x, y) > 1e-6) list.add(new PointOfInterest(x, y, mat.get(x, y)));
        return list;
    }

}
