package net.web_kot.cv.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import net.web_kot.cv.mat.Mat;

import javax.imageio.ImageIO;
import java.io.File;

@UtilityClass
public class IOUtils {

    @SneakyThrows
    public Mat readGreyscaleFromFile(File file) {
        return Mat.fromBufferedImage(ImageIO.read(file));
    }

    @SneakyThrows
    public void writeToPngFile(Mat image, File file) {
        if(!file.getParentFile().exists() && !file.getParentFile().mkdirs())
            throw new RuntimeException("Unable to create directory");
        ImageIO.write(image.toBufferedImage(), "png", file);
    }

}
