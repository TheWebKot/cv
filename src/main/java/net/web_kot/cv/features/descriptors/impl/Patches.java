package net.web_kot.cv.features.descriptors.impl;

import lombok.experimental.UtilityClass;
import net.web_kot.cv.features.corners.PointOfInterest;
import net.web_kot.cv.features.descriptors.Descriptor;
import net.web_kot.cv.mat.EdgeWrapMode;
import net.web_kot.cv.mat.Mat;
import net.web_kot.cv.mat.Vector;
import net.web_kot.cv.processors.convolution.impl.gradient.Gradient;
import net.web_kot.cv.processors.convolution.impl.gradient.GradientMatrices;

import java.util.List;

import static net.web_kot.cv.features.descriptors.DescriptorsCommon.*;

@UtilityClass
public class Patches {

    private final int DEFAULT_GRID_SIZE = 5;
    private final int DEFAULT_BLOCK_SIZE = 5;

    public List<Descriptor> calculate(Mat image, Iterable<PointOfInterest> points) {
        return calculate(image, points, DEFAULT_GRID_SIZE, DEFAULT_BLOCK_SIZE);
    }

    public List<Descriptor> calculate(Mat image, Iterable<PointOfInterest> points, int gridSize, int blockSize) {
        Mat gradient = Gradient.apply(image, GradientMatrices.SOBEL);

        return map(points, gridMapper(gridSize, blockSize, (xRange, yRange) -> {
            double value = 0;
            for(int x = xRange.fromInclusive(); x < xRange.toExclusive(); x++)
                for(int y = yRange.fromInclusive(); y < yRange.toExclusive(); y++)
                    value += gradient.get(x, y, EdgeWrapMode.DEFAULT);

            return Vector.from(new double[] { value / (xRange.getSize() * yRange.getSize()) });
        }));
    }

}
