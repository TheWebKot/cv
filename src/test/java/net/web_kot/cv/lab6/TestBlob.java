package net.web_kot.cv.lab6;

import net.web_kot.cv.features.blobs.Blob;
import net.web_kot.cv.features.blobs.BlobsDetector;
import net.web_kot.cv.mat.Mat;
import net.web_kot.cv.utils.DrawUtils;
import net.web_kot.cv.utils.IOUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

public class TestBlob {

    public static void main(String[] args) {
        Mat image = IOUtils.readGreyscaleFromFile(new File("test/daisies.jpg"));

        List<Blob> blobs = BlobsDetector.find(image);

        BufferedImage result = IOUtils.toBufferedImage(image);
        DrawUtils.enableAntiAliasing(result);

        for(Blob b : blobs)
            DrawUtils.drawCircle(result, b.getX(), b.getY(), (int)Math.round(b.getRadius()), Color.YELLOW);

        IOUtils.writeToJpegFile(result, new File("test/scale/blobs.jpeg"));
    }

}
