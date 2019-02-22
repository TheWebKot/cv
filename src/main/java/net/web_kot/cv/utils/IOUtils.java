package net.web_kot.cv.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import net.web_kot.cv.mat.Mat;

import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.File;

@UtilityClass
public class IOUtils {

    @SneakyThrows
    public Mat readGreyscaleFromFile(File file) {
        BufferedImage image = ImageIO.read(file);
        int width = image.getWidth(), height = image.getHeight();

        Mat mat = new Mat(width, height);
        for(int x = 0; x < width; x++)
            for(int y = 0; y < height; y++)
                mat.set(x, y, ColorUtils.rgbToGreyscale(image.getRGB(x, y)));

        return mat;
    }

    @SneakyThrows
    public void writeToPngFile(Mat mat, File file) {
        if(!file.getParentFile().exists() && !file.getParentFile().mkdirs())
            throw new RuntimeException("Unable to create directory");

        int width = mat.getWidth(), height = mat.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

        double[] buffer = mat.getBuffer();
        for(int i = 0; i < buffer.length; i++)
            pixels[i] = ColorUtils.greyscaleToRgb(buffer[i]);

        ImageIO.write(image, "jpeg", file);
    }

}
