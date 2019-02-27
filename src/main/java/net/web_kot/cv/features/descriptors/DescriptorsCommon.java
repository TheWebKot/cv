package net.web_kot.cv.features.descriptors;

import lombok.experimental.UtilityClass;
import net.web_kot.cv.features.corners.PointOfInterest;
import net.web_kot.cv.mat.Vector;
import net.web_kot.cv.utils.Range;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

@UtilityClass
public class DescriptorsCommon {

    public List<Descriptor> map(Iterable<PointOfInterest> points, Function<PointOfInterest, Vector> mapper) {
        ArrayList<Descriptor> result = new ArrayList<>();
        for(PointOfInterest point : points) result.add(Descriptor.of(point, mapper.apply(point)));
        return result;
    }

    public Function<PointOfInterest, Vector> gridMapper(int gridSize, int blockSize,
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

}
