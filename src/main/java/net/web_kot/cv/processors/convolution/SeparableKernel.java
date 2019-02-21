package net.web_kot.cv.processors.convolution;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.web_kot.cv.mat.Mat;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SeparableKernel {

    @Getter
    private final Mat xVector, yVector;

    public static SeparableKernel of(Mat vectorX, Mat vectorY) {
        return new SeparableKernel(vectorX, vectorY);
    }

}
