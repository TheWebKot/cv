package net.web_kot.cv.lab1;

import net.web_kot.cv.image.EdgeWrapMode;
import net.web_kot.cv.image.GreyscaleImage;
import net.web_kot.cv.utils.IOUtils;

import java.io.File;

public class TestEdgeWrapping {

    private static final int EDGE = 100;
    
    public static void main(String[] args) {
        GreyscaleImage image = IOUtils.readGreyscaleFromFile(new File("test/Fry.jpg"));
        
        for(EdgeWrapMode mode : EdgeWrapMode.values()) {
            GreyscaleImage result = new GreyscaleImage(image.getWidth() + EDGE * 2, image.getHeight() + EDGE * 2);
            for(int x = 0; x < result.getWidth(); x++)
                for(int y = 0; y < result.getHeight(); y++)
                    result.setPixel(x, y, image.getPixel(x - EDGE, y - EDGE, mode));
            
            File output = new File("test/edgeWrapping/" + mode + ".png");
            IOUtils.writeToPngFile(result, output);
        }
    }
    
}