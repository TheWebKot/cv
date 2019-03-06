package net.web_kot.cv.features.descriptors;

import net.web_kot.cv.features.corners.PointOfInterest;
import net.web_kot.cv.mat.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class DescriptorsCommon {

    public static List<Descriptor> map(Iterable<PointOfInterest> points, Function<PointOfInterest, Vector> mapper) {
        ArrayList<Descriptor> result = new ArrayList<>();
        for(PointOfInterest point : points) result.add(Descriptor.of(point, mapper.apply(point)));
        return result;
    }

}
