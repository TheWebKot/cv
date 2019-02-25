package net.web_kot.cv.features.corners;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.web_kot.cv.utils.MathUtils;

@AllArgsConstructor
public class PointOfInterest {

    @Getter
    private final int x, y;
    @Getter
    private final double value;

    public double squaredDistanceTo(PointOfInterest other) {
        return MathUtils.sqr(x - other.x) + MathUtils.sqr(y - other.y);
    }

}
