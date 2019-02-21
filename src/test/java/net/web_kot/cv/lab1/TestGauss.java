package net.web_kot.cv.lab1;

import net.web_kot.cv.image.GreyscaleImage;
import net.web_kot.cv.modifiers.common.Normalization;
import net.web_kot.cv.modifiers.convolution.impl.Gauss;
import net.web_kot.cv.utils.IOUtils;

import java.io.File;

public class TestGauss {

    public static void main(String[] args) {
        GreyscaleImage image = IOUtils.readGreyscaleFromFile(new File("test/Lenna.jpg"));

        GreyscaleImage result = image.modifier(Gauss.class).apply()
                                     .modifier(Normalization.class).apply();

        File output = new File("test/gauss/result.png");
        IOUtils.writeToPngFile(result, output);
    }
    
}
