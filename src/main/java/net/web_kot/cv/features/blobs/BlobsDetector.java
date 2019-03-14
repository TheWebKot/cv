package net.web_kot.cv.features.blobs;

import net.web_kot.cv.mat.EdgeWrapMode;
import net.web_kot.cv.mat.Mat;
import net.web_kot.cv.scale.Pyramid;
import net.web_kot.cv.scale.ScaledMat;
import net.web_kot.cv.utils.PointUtils;

import java.util.ArrayList;

public class BlobsDetector {

    private static final int OCTAVE_SIZE = 8;

    public static ArrayList<Blob> find(Mat image) {
        Pyramid pyramid = Pyramid.build(image, OCTAVE_SIZE, 0.8, 1.6, true);
        int layersNum = pyramid.getOctaveSize();

        ArrayList<Blob> blobs = new ArrayList<>();
        for(int octave = -1; octave < pyramid.getOctavesCount(); octave++) {
            for(int layer = 1; layer <= layersNum; layer++) {
                if(octave == -1) layer = layersNum;

                Mat current = pyramid.getDoG(octave, layer);

                for(int x = 0; x < current.getWidth(); x++)
                    for(int y = 0; y < current.getHeight(); y++) {
                        double value = current.get(x, y);

                        boolean localMaximum = true, localMinimum = true;
                        for(PointUtils.Delta d : PointUtils.neighborhood3D()) {
                            Mat mat = pyramid.getDoG(octave, layer + d.z());
                            double check = mat.get(x + d.x(), y + d.y(), EdgeWrapMode.DEFAULT);

                            localMaximum &= check < value;
                            localMinimum &= check > value;
                        }

                        if(localMaximum || localMinimum) {
                            int delta = ScaledMat.modify(1, octave) / 2;

                            int px = ScaledMat.modify(x, octave) + delta;
                            int py = ScaledMat.modify(y, octave) + delta;

                            double sigma = pyramid.getSigma0() * Math.pow(2, octave + layer * 1D / layersNum);
                            blobs.add(new Blob(px, py, sigma * Math.sqrt(2)));
                        }
                    }
            }
        }

        return blobs;
    }

}
