package net.web_kot.cv.features.blobs;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class RawBlob {

    @Getter
    private int x, y;

    @Getter
    private int octave, layer;

    @Getter
    private double value;

}
