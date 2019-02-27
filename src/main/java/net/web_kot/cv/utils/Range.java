package net.web_kot.cv.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@AllArgsConstructor(staticName = "of")
public class Range {

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
