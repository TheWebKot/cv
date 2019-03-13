package net.web_kot.cv.features.blobs;

import net.web_kot.cv.mat.EdgeWrapMode;
import net.web_kot.cv.mat.Mat;
import net.web_kot.cv.scale.Pyramid;
import net.web_kot.cv.scale.ScaledMat;
import net.web_kot.cv.utils.PointUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class BlobsDetector {

    private static final int OCTAVE_SIZE = 8;

    public static ArrayList<Blob> find(Mat image) {
        Pyramid pyramid = Pyramid.build(image, OCTAVE_SIZE, 0.8, 1.6);

        ArrayList<Blob> blobs = new ArrayList<>();
        for(int i = 0; i < pyramid.getOctavesCount(); i++) {
            List<ScaledMat> octave = pyramid.getOctave(i);
            int width = octave.get(0).withoutScaling().getWidth(), height = octave.get(0).withoutScaling().getHeight();

            List<Pair<Mat, Double>> DoG = buildDoG(pyramid, i);
            for(int j = 1; j < DoG.size() - 1; j++) {
                Mat current = DoG.get(j).getKey();

                for(int x = 0; x < width; x++)
                    for(int y = 0; y < height; y++) {
                        double value = current.get(x, y);

                        boolean localMaximum = true, localMinimum = true;
                        for(PointUtils.Delta d : PointUtils.neighborhood3D()) {
                            double check = DoG.get(j + d.z()).getKey().get(x + d.x(), y + d.y(), EdgeWrapMode.DEFAULT);

                            localMaximum &= check < value;
                            localMinimum &= check > value;
                        }

                        if(localMaximum || localMinimum)
                            blobs.add(new Blob(x << i, y << i, DoG.get(j).getValue() * Math.sqrt(2)));
                    }
            }
        }

        return blobs;
    }

    public static List<Pair<Mat, Double>> buildDoG(Pyramid pyramid, int octaveIndex) {
        List<ScaledMat> octave = pyramid.getOctave(octaveIndex);
        int width = octave.get(0).withoutScaling().getWidth(), height = octave.get(0).withoutScaling().getHeight();

        ArrayList<Pair<Mat, Double>> DoG = new ArrayList<>(octave.size() - 1);
        for(int j = 0; j < octave.size() - 1; j++) {
            Mat mat = octave.get(j).withoutScaling(), next = octave.get(j + 1).withoutScaling();
            Mat difference = mat.withSameSize();

            for(int x = 0; x < width; x++)
                for(int y = 0; y < height; y++)
                    difference.set(x, y, next.get(x, y) - mat.get(x, y));

            DoG.add(Pair.of(difference, octave.get(j).getEffectiveSigma()));
        }

        return DoG;
    }

}
