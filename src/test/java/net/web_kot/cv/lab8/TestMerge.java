package net.web_kot.cv.lab8;

import com.google.common.collect.ImmutableList;
import net.web_kot.cv.features.descriptors.Descriptor;
import net.web_kot.cv.features.descriptors.impl.SIFT;
import net.web_kot.cv.features.descriptors.matcher.MatcherNNDR;
import net.web_kot.cv.lab4.TestDescriptors;
import net.web_kot.cv.mat.Mat;
import net.web_kot.cv.transformation.ImageTransformer;
import net.web_kot.cv.transformation.RandomSampleConsensus;
import net.web_kot.cv.transformation.Transformation;
import net.web_kot.cv.utils.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import javax.imageio.ImageIO;
import java.io.File;
import java.util.List;

public class TestMerge {

    private static final List<Triple<String, String, String>> IMAGES = ImmutableList.of(
            Triple.of("test/architecture1.jpg", "test/architecture2.jpg", "architecture"),
            Triple.of("test/bridge1.jpg", "test/bridge2.jpg", "bridge")
    );

    public static void main(String[] args) throws Exception {
        for(Triple<String, String, String> test : IMAGES) {
            Mat image1 = IOUtils.readGreyscaleFromFile(new File(test.getLeft()));
            Mat image2 = IOUtils.readGreyscaleFromFile(new File(test.getMiddle()));

            List<Descriptor> desc1 = SIFT.calculate(image1), desc2 = SIFT.calculate(image2);
            List<Pair<Descriptor, Descriptor>> matching = MatcherNNDR.INSTANCE.match(desc1, desc2);

            IOUtils.writeToJpegFile(TestDescriptors.drawMatches(image1, image2, desc1, desc2, matching),
                                    new File("test/merge/" + test.getRight() + ".jpeg"));

            Transformation transformation = RandomSampleConsensus.apply(matching);
            List<Pair<Descriptor, Descriptor>> filtered = transformation.getMatches();

            IOUtils.writeToJpegFile(TestDescriptors.drawMatches(image1, image2, desc1, desc2, filtered),
                                    new File("test/merge/" + test.getRight() + "-filtered.jpeg"));

            IOUtils.writeToJpegFile(
                    ImageTransformer.apply(
                            ImageIO.read(new File(test.getLeft())),
                            ImageIO.read(new File(test.getMiddle())),
                            transformation
                    ),
                    new File("test/merge/" + test.getRight() + "-merged.jpeg")
            );
        }
    }

}
