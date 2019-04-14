package net.web_kot.cv.lab9;

import com.google.common.collect.ImmutableList;
import net.web_kot.cv.lab4.TestDescriptors;
import net.web_kot.cv.mat.Mat;
import net.web_kot.cv.transformation.HoughTransform;
import net.web_kot.cv.utils.DrawUtils;
import net.web_kot.cv.utils.IOUtils;
import org.apache.commons.lang3.tuple.Triple;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TestHough {

    private static final List<Triple<String, String, String>> IMAGES = ImmutableList.of(
            Triple.of("test/hough_3_sample.jpg", "test/photo_2019-04-14_14-42-35.jpg", "1"),
            Triple.of("test/photo_2019-04-14_21-44-17.jpg", "test/photo_2019-04-14_21-14-50.jpg", "2")
    );

    public static void main(String[] args) throws Exception {
        for(Triple<String, String, String> test : IMAGES) {
            Mat sample = IOUtils.readGreyscaleFromFile(new File(test.getLeft()));
            Mat image = IOUtils.readGreyscaleFromFile(new File(test.getMiddle()));

            HoughTransform transform = HoughTransform.find(sample, image);

            BufferedImage result = ImageIO.read(new File(test.getMiddle()));
            for(Polygon object : transform.getObjects()) DrawUtils.drawPolygon(result, object, Color.GREEN, 3);

            IOUtils.writeToJpegFile(
                    TestDescriptors.drawMatches(
                            sample, image, new ArrayList<>(), new ArrayList<>(), transform.getReverseMatching()
                    ),
                    new File("test/hough/" + test.getRight() + "-a.jpeg")
            );
            IOUtils.writeToJpegFile(transform.getVotesImage(), new File("test/hough/" + test.getRight() + "-b.jpeg"));
            IOUtils.writeToJpegFile(result, new File("test/hough/" + test.getRight() + "-c.jpeg"));
        }
    }

}
