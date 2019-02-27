package net.web_kot.cv.utils;

import lombok.SneakyThrows;
import net.web_kot.cv.mat.Mat;

import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.File;

public class IOUtils {

    @SneakyThrows
    public static Mat readGreyscaleFromFile(File file) {
        BufferedImage image = ImageIO.read(file);
        int width = image.getWidth(), height = image.getHeight();

        Mat mat = new Mat(width, height);
        for(int x = 0; x < width; x++)
            for(int y = 0; y < height; y++)
                mat.set(x, y, ColorUtils.rgbToGreyscale(image.getRGB(x, y)));

        return mat;
    }

    @SneakyThrows
    public static void writeToJpegFile(Mat mat, File file) {
        writeToJpegFile(toBufferedImage(mat), file);
    }

    @SneakyThrows
    public static void writeToJpegFile(BufferedImage image, File file) {
        if(!file.getParentFile().exists() && !file.getParentFile().mkdirs())
            throw new RuntimeException("Unable to create directory");
        ImageIO.write(image, "jpeg", file);
    }

    public static BufferedImage toBufferedImage(Mat mat) {
        int width = mat.getWidth(), height = mat.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

        for(int x = 0; x < width; x++)
            for(int y = 0; y < height; y++)
                pixels[y * width + x] = ColorUtils.greyscaleToRgb(mat.get(x, y));

        return image;
    }

    public static String getFileName(String path) {
        if(path.contains("/")) path = path.substring(path.lastIndexOf('/'));
        return path.substring(0, path.lastIndexOf('.'));
    }

}
