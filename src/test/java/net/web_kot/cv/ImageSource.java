package net.web_kot.cv;

import java.io.File;
import java.util.Objects;

public class ImageSource {
    
    public static File getImageFile() {
        return Objects.requireNonNull((new File("test")).listFiles())[0];
    }
    
}
