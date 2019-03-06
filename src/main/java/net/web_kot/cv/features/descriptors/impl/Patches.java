package net.web_kot.cv.features.descriptors.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.web_kot.cv.features.corners.PointOfInterest;
import net.web_kot.cv.features.descriptors.Descriptor;
import net.web_kot.cv.mat.EdgeWrapMode;
import net.web_kot.cv.mat.Mat;
import net.web_kot.cv.mat.Vector;
import net.web_kot.cv.processors.convolution.impl.gradient.Gradient;
import net.web_kot.cv.processors.convolution.impl.gradient.GradientMatrices;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static net.web_kot.cv.features.descriptors.DescriptorsCommon.*;

public class Patches {

    private static final int DEFAULT_GRID_SIZE = 5;
    private static final int DEFAULT_BLOCK_SIZE = 5;

    public static List<Descriptor> calculate(Mat image, Iterable<PointOfInterest> points) {
        return calculate(image, points, DEFAULT_GRID_SIZE, DEFAULT_BLOCK_SIZE);
    }

    public static List<Descriptor> calculate(Mat image, Iterable<PointOfInterest> points, int gridSize, int blockSize) {
        Mat gradient = Gradient.apply(image, GradientMatrices.SOBEL);

        return map(points, gridMapper(gridSize, blockSize, (xRange, yRange) -> {
            double value = 0;
            for(int x = xRange.fromInclusive(); x < xRange.toExclusive(); x++)
                for(int y = yRange.fromInclusive(); y < yRange.toExclusive(); y++)
                    value += gradient.get(x, y, EdgeWrapMode.DEFAULT);

            return Vector.from(new double[] { value / (xRange.getSize() * yRange.getSize()) });
        }));
    }

    private static Function<PointOfInterest, Vector> gridMapper(int gridSize, int blockSize,
                                                               BiFunction<Range, Range, Vector> blockMapper) {
        return (point) -> {
            int cornerX = point.getX() - gridSize * blockSize / 2;
            int cornerY = point.getY() - gridSize * blockSize / 2;

            int size = 0;
            List<Vector> vectors = new ArrayList<>(blockSize * blockSize);

            for(int i = 0; i < gridSize; i++)
                for(int j = 0; j < gridSize; j++) {
                    Range x = Range.of(cornerX + i * blockSize, cornerX + (i + 1) * blockSize);
                    Range y = Range.of(cornerY + j * blockSize, cornerY + (j + 1) * blockSize);

                    Vector vector = blockMapper.apply(x, y);
                    vectors.add(vector);
                    size += vector.getSize();
                }

            int offset = 0;
            Vector result = new Vector(size);
            for(Vector v : vectors) {
                for(int i = 0; i < v.getSize(); i++) result.set(offset + i, v.get(i));
                offset += v.getSize();
            }

            return result.normalize();
        };
    }

    @Accessors(fluent = true)
    @AllArgsConstructor(staticName = "of")
    private static class Range {

        @Getter
        private final int fromInclusive, toExclusive;

        public int getSize() {
            return toExclusive - fromInclusive;
        }

        @Override
        public String toString() {
            return "[" + fromInclusive + ", " + toExclusive + ")";
        }

    }

}
