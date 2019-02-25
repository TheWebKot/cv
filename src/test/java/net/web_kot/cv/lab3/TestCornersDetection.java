package net.web_kot.cv.lab3;

import com.google.common.collect.ImmutableList;
import net.web_kot.cv.features.corners.Harris;
import net.web_kot.cv.features.corners.Moravec;
import net.web_kot.cv.features.corners.NonMaximumSuppression;
import net.web_kot.cv.features.corners.PointOfInterest;
import net.web_kot.cv.mat.Mat;
import net.web_kot.cv.utils.DrawUtils;
import net.web_kot.cv.utils.IOUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

public class TestCornersDetection {

    private static final List<Pair<String, Integer>> IMAGES = ImmutableList.of(
            Pair.of("test/blocks.gif", 30),
            Pair.of("test/grid.png", 30)
    );

    public static void main(String[] args) throws Exception{
        FileUtils.deleteDirectory(new File("test/corners/"));

        for(Pair<String, Integer> entry : IMAGES) {
            String name = IOUtils.getFileName(entry.getKey());
            Mat image = IOUtils.readGreyscaleFromFile(new File(entry.getKey()));

            Mat moravec = Moravec.find(image, 2, 0.1);
            save(image, moravec, name + "-moravec");
            filter(image, moravec, name + "-moravec", entry.getValue());

            Mat harris = Harris.find(image, 2, 0.1);
            save(image, harris, name + "-harris");
            filter(image, harris, name + "-harris", entry.getValue());
        }
    }

    private static void filter(Mat image, Mat mat, String name, int amount) {
        save(image, NonMaximumSuppression.filter(mat, amount), name + "-filter");
        save(image, NonMaximumSuppression.filter2(mat, amount), name + "-filter2");
    }

    @SuppressWarnings("unchecked")
    public static void save(Mat image, Object corners, String fileName) {
        BufferedImage result = IOUtils.toBufferedImage(image);
        if(corners instanceof Mat)
            DrawUtils.drawCorners(result, (Mat) corners);
        else
            DrawUtils.drawCorners(result, (List<PointOfInterest>)corners);

        IOUtils.writeToJpegFile(result, new File("test/corners/" + fileName + ".jpeg"));
    }

}
