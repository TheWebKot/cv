package net.web_kot.cv.lab4;

import com.google.common.collect.ImmutableList;
import net.web_kot.cv.features.corners.Harris;
import net.web_kot.cv.features.corners.NonMaximumSuppression;
import net.web_kot.cv.features.corners.PointOfInterest;
import net.web_kot.cv.features.descriptors.Descriptor;
import net.web_kot.cv.features.descriptors.Matcher;
import net.web_kot.cv.features.descriptors.impl.HOG;
import net.web_kot.cv.features.descriptors.impl.Patches;
import net.web_kot.cv.mat.Mat;
import net.web_kot.cv.utils.DrawUtils;
import net.web_kot.cv.utils.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TestDescriptors {

    private static final int X_DISTANCE = 10;
    private static final int Y_OFFSET = 30;

    private static final List<Triple<String, String, String>> IMAGES = ImmutableList.of(
            Triple.of("test/cat2.jpg", "test/cat2-transformed.jpg", "cat"),
            Triple.of("test/blocks.gif", "test/blocks-rotated.jpg", "blocks"),
            Triple.of("test/building.jpg", "test/building2.jpg", "building")
    );

    public static void main(String[] args) {
        for(Triple<String, String, String> test : IMAGES) {
            Mat image1 = IOUtils.readGreyscaleFromFile(new File(test.getLeft()));
            Mat image2 = IOUtils.readGreyscaleFromFile(new File(test.getMiddle()));

            test("detectors", image1, image2, Patches::calculate, test.getRight() + "-patches");
            test("detectors", image1, image2, HOG::calculate, test.getRight() + "-hog");
        }
    }

    public static void test(String folder, Mat image1, Mat image2,
                            BiFunction<Mat, List<PointOfInterest>, List<Descriptor>> func, String name) {
        test(folder, image1, image2, image -> func.apply(image, getPoints(image)), name, false);
    }

    public static void test(String folder, Mat image1, Mat image2,
                            Function<Mat, List<Descriptor>> func, String name, boolean extended) {
        List<Descriptor> desc1 = func.apply(image1), desc2 = func.apply(image2);
        List<Pair<Descriptor, Descriptor>> matching = Matcher.match(desc1, desc2);

        int width = image1.getWidth() + image2.getWidth() + X_DISTANCE;
        int height = Math.max(image1.getHeight(), image2.getHeight() + Y_OFFSET);

        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        DrawUtils.drawImage(result, drawPoints(image1, desc1, extended), 0, 0);
        DrawUtils.drawImage(result, drawPoints(image2, desc2, extended), image1.getWidth() + X_DISTANCE, Y_OFFSET);

        for(Pair<Descriptor, Descriptor> match : matching) {
            PointOfInterest from = match.getLeft().getPoint(), to = match.getRight().getPoint();
            DrawUtils.drawLine(result,
                               from.getX(), from.getY(),
                               to.getX() + image1.getWidth() + X_DISTANCE, to.getY() + Y_OFFSET);
        }

        IOUtils.writeToJpegFile(result, new File("test/" + folder + "/" + name + ".jpeg"));
    }

    private static List<PointOfInterest> getPoints(Mat image) {
        Mat harris = Harris.find(image, 3, 0.1);
        return NonMaximumSuppression.filter(harris, 30);
    }

    private static BufferedImage drawPoints(Mat image, List<Descriptor> descriptors, boolean extended) {
        BufferedImage result = IOUtils.toBufferedImage(image);
        if(!extended) {
            List<PointOfInterest> points = descriptors.stream().map(Descriptor::getPoint).collect(Collectors.toList());
            DrawUtils.drawCorners(result, points);
        } else {
            DrawUtils.drawDescriptors(result, descriptors);
        }
        return result;
    }

}
