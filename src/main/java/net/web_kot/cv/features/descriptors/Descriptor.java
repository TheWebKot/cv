package net.web_kot.cv.features.descriptors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.web_kot.cv.features.corners.PointOfInterest;
import net.web_kot.cv.mat.Vector;

@AllArgsConstructor(staticName = "of")
public class Descriptor {

    @Getter
    private final PointOfInterest point;
    @Getter
    private final Vector vector;

    @Override
    public String toString() {
        return vector.toString();
    }

}
