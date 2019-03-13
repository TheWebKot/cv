package net.web_kot.cv.features.corners;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.web_kot.cv.utils.MathUtils;

@RequiredArgsConstructor
public class PointOfInterest {

    @Getter
    private final int x, y;
    @Getter
    private final double value;

    @Getter @Setter @Accessors(chain = true)
    private int octave, layer;

    public double squaredDistanceTo(PointOfInterest other) {
        return MathUtils.sqr(x - other.x) + MathUtils.sqr(y - other.y);
    }

}
