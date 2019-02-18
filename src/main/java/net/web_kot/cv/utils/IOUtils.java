package net.web_kot.cv.utils;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import net.web_kot.cv.image.GreyscaleImage;

import javax.imageio.ImageIO;
import java.io.File;

@UtilityClass
public class IOUtils {
    
    @SneakyThrows
    public GreyscaleImage readGreyscaleFromFile(@NonNull File file) {
        return GreyscaleImage.fromBufferedImage(ImageIO.read(file));
    }
    
    @SneakyThrows
    public void writeToPngFile(@NonNull GreyscaleImage image, @NonNull File file) {
        if(!file.getParentFile().exists() && !file.getParentFile().mkdirs()) 
            throw new RuntimeException("Unable to create directory");
        ImageIO.write(image.toBufferedImage(), "png", file);
    }
    
}
