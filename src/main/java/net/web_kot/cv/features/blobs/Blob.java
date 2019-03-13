package net.web_kot.cv.features.blobs;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Blob {

    @Getter
    private int x, y;

    @Getter
    private double radius;

}
