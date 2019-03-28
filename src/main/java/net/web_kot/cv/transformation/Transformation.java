package net.web_kot.cv.transformation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.web_kot.cv.features.descriptors.Descriptor;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class Transformation {

    private final List<Pair<Descriptor, Descriptor>> matches;
    private final double[][] homography;

}
