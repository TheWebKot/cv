package net.web_kot.cv.features.descriptors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.web_kot.cv.features.corners.PointOfInterest;
import net.web_kot.cv.mat.Vector;

@RequiredArgsConstructor(staticName = "of")
public class Descriptor {

    @Getter
    private final PointOfInterest point;
    @Getter
    private final Vector vector;

    @Getter @Setter @Accessors(chain = true)
    private double angle;

    @Override
    public String toString() {
        return vector.toString();
    }

}
