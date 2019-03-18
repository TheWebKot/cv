package net.web_kot.cv.features.blobs;

import net.web_kot.cv.mat.EdgeWrapMode;
import net.web_kot.cv.mat.Mat;
import net.web_kot.cv.scale.Pyramid;
import net.web_kot.cv.scale.ScaledMat;
import net.web_kot.cv.utils.PointUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BlobsDetector {

    private static final int OCTAVE_SIZE = 8;

    public static List<Blob> find(Mat image) {
        return find(Pyramid.build(image, OCTAVE_SIZE, 0.8, 1.6, true));
    }

    public static List<Blob> find(Pyramid pyramid) {
        List<RawBlob> raw = findRaw(pyramid, 0);
        return raw.stream()
                  .map(b -> {
                      int delta = ScaledMat.modify(1, b.getOctave()) / 2;

                      int px = ScaledMat.modify(b.getX(), b.getOctave()) + delta;
                      int py = ScaledMat.modify(b.getY(), b.getOctave()) + delta;

                      double p = Math.pow(2, b.getOctave() + b.getLayer() * 1D / pyramid.getOctaveSize());
                      return new Blob(b, px, py, pyramid.getSigma0() * p * Math.sqrt(2));
                  })
                  .collect(Collectors.toList());
    }

    public static List<RawBlob> findRaw(Pyramid pyramid, int imageBorder) {
        int layersNum = pyramid.getOctaveSize();

        ArrayList<RawBlob> blobs = new ArrayList<>();
        for(int octave = -1; octave < pyramid.getOctavesCount(); octave++) {
            for(int layer = 1; layer <= layersNum; layer++) {
                if(octave == -1) layer = layersNum;

                Mat current = pyramid.getDoG(octave, layer);

                for(int x = imageBorder; x < current.getWidth() - imageBorder; x++)
                    for(int y = imageBorder; y < current.getHeight() - imageBorder; y++) {
                        double value = current.get(x, y);

                        boolean localMaximum = true, localMinimum = true;
                        for(PointUtils.Delta d : PointUtils.neighborhood3D()) {
                            Mat mat = pyramid.getDoG(octave, layer + d.z());
                            double check = mat.get(x + d.x(), y + d.y(), EdgeWrapMode.DEFAULT);

                            localMaximum &= check < value;
                            localMinimum &= check > value;
                        }

                        if(localMaximum || localMinimum) blobs.add(new RawBlob(x, y, octave, layer, value));
                    }
            }
        }

        return blobs;
    }

}
