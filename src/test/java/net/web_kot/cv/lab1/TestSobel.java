package net.web_kot.cv.lab1;

import net.web_kot.cv.image.GreyscaleImage;
import net.web_kot.cv.modifiers.common.Normalization;
import net.web_kot.cv.modifiers.convolution.impl.SobelOperator;
import net.web_kot.cv.utils.IOUtils;

import java.io.File;

public class TestSobel {
    
    public static void main(String[] args) {
        GreyscaleImage image = IOUtils.readGreyscaleFromFile(new File("test/wall.jpg"));
        
        GreyscaleImage result = image.modifier(SobelOperator.class).setTmpFilesPath(new File("test/sobel/")).apply()
                                     .modifier(Normalization.class).apply();
        
        File output = new File("test/sobel/result.png");
        IOUtils.writeToPngFile(result, output);
    }
    
}
