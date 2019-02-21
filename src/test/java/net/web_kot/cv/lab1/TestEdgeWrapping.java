package net.web_kot.cv.lab1;

import net.web_kot.cv.mat.EdgeWrapMode;
import net.web_kot.cv.mat.Mat;
import net.web_kot.cv.utils.IOUtils;

import java.io.File;

public class TestEdgeWrapping {

    private static final int EDGE = 100;

    public static void main(String[] args) {
        Mat image = IOUtils.readGreyscaleFromFile(new File("test/Fry.jpg"));

        for(EdgeWrapMode mode : EdgeWrapMode.values()) {
            if(mode == EdgeWrapMode.DEFAULT) continue;

            Mat result = new Mat(image.getWidth() + EDGE * 2, image.getHeight() + EDGE * 2);
            for(int x = 0; x < result.getWidth(); x++)
                for(int y = 0; y < result.getHeight(); y++)
                    result.set(x, y, image.get(x - EDGE, y - EDGE, mode));

            IOUtils.writeToPngFile(result, new File("test/edgeWrapping/" + mode + ".png"));
        }
    }

}