package net.web_kot.cv.lab6;

import com.google.common.collect.ImmutableList;
import net.web_kot.cv.features.descriptors.impl.RotationInvariant;
import net.web_kot.cv.features.descriptors.impl.SIFT;
import net.web_kot.cv.lab4.TestDescriptors;
import net.web_kot.cv.mat.Mat;
import net.web_kot.cv.utils.IOUtils;
import org.apache.commons.lang3.tuple.Triple;

import java.io.File;
import java.util.List;

public class TestScaleInvariant {

    private static final List<Triple<String, String, String>> IMAGES = ImmutableList.of(
            Triple.of("test/cat2.jpg", "test/cat2-scaled.jpg", "cat-scaled"),
            Triple.of("test/cat2.jpg", "test/cat2-rotated.jpg", "cat"),
            Triple.of("test/cat2.jpg", "test/cat2-scaled-rotated.jpg", "cat-sr")
    );

    public static void main(String[] args) {
        for(Triple<String, String, String> test : IMAGES) {
            Mat image1 = IOUtils.readGreyscaleFromFile(new File(test.getLeft()));
            Mat image2 = IOUtils.readGreyscaleFromFile(new File(test.getMiddle()));

            TestDescriptors.test("scale", image1, image2, RotationInvariant::calculate, test.getRight() + "-ri");
            TestDescriptors.test("scale", image1, image2, SIFT::calculate, test.getRight() + "-s", true);
        }
    }

}
